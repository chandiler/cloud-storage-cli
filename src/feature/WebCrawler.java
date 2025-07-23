package feature;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileOutputStream;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebCrawler {

	private static String getTextOrNA(WebElement parent, By selector) {
		List<WebElement> found = parent.findElements(selector);
		return found.isEmpty() ? "N/A" : found.get(0).getText().trim();
	}

	public static void main(String[] args) {
		// Turn off the warning
		System.setProperty("webdriver.chrome.silentOutput", "true");
		Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

		WebDriver driver = new ChromeDriver();
		// Set the Selenium 'implicit wait time' to 10 seconds.
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

		driver.get("https://one.google.com/about/plans?hl=en");

		// Monthly plans
		// Scroll to the bottom to ensure all plans are loaded
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
		try {
			Thread.sleep(1500);
		} catch (InterruptedException ignored) {
		}
		// Scrape all monthly plan information from the page and store it in the Plan
		// object.
		List<Plan> allPlans = extractPlans(driver, "Monthly");

		// Click to switch to Annual plans
		try {
			WebElement toggle = driver.findElement(By.className("wnccze"));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", toggle);
			System.out.println("Switched to Annual plans");
			Thread.sleep(1500);
		} catch (Exception e) {
			System.out.println("Failed to click annual toggle: " + e.getMessage());
		}
		// Scrape all annual plan information from the page and store it in the Plan
		// object.
		allPlans.addAll(extractPlans(driver, "Annual"));

		writePlansCSV(allPlans, "plans.csv");

		// Crawl Google One benefits
		driver.get("https://one.google.com/about/benefits?hl=en");
		List<Benefit> benefits = extractBenefits(driver);
		writeBenefitsCSV(benefits, "features.csv");

		driver.quit();
		System.out.println("All data saved.");
	}

	private static List<Plan> extractPlans(WebDriver driver, String planType) {
		List<Plan> plans = new ArrayList<>();
		// The main structure of this page to be crawled is:
		// container: .staJ7e
		// It is just a containment rather than a direct parent element
		// plan title: .onuV9b
		// which default plan don't have so I set it to 'Basic(15G)
		// plan prize:.BQxAac
		// The last span under this element,some plans don't have this.
		// plan features: .ZI49d.
		// Structures other than the default plan are all nested buttons and spans, and
		// do not require a specified list, but I believe this is safer.
		List<WebElement> blocks = driver.findElements(By.cssSelector("div.staJ7e"));
		for (WebElement block : blocks) {
			// Plan title
			String title = getTextOrNA(block, By.cssSelector("div.onuV9b"));
			if (title.equals("N/A")) {
				title = "Basic(15G)";
			}

			// Price logic: last <span> for Annual, first for Monthly
			String price;
			List<WebElement> priceSpans = block.findElements(By.cssSelector("div.BQxAac span"));
			if (planType.equals("Annual") && !priceSpans.isEmpty()) {
				price = priceSpans.get(priceSpans.size() - 1).getText().trim();
			} else if (!priceSpans.isEmpty()) {
				price = priceSpans.get(0).getText().trim();
			} else {
				price = "N/A";
			}

			// Plan features
			List<String> features = new ArrayList<>();
			List<WebElement> liList = block.findElements(By.cssSelector("ul.OWqi7c li.rzIFlb"));
			for (WebElement li : liList) {
				String text = "";
				try {
					text = getTextOrNA(li, By.cssSelector("span.ZI49d"));
				} catch (NoSuchElementException ignored) {
				}
				if (!text.isEmpty()) {
					features.add(text);
				}
			}

			plans.add(new Plan(title, price, features, planType));

			// Log to console
			System.out.println("Plan: " + title);
			System.out.println("Price: " + price);
			System.out.println("Type: " + planType);
			System.out.println("Features: " + String.join("; ", features));
			System.out.println("-----");
		}
		return plans;
	}

	private static List<Benefit> extractBenefits(WebDriver driver) {
		List<Benefit> benefits = new ArrayList<>();
		// The main structure of this page to be crawled is:
		// Features titles:._title_ks8oi_172
		// Features bodies:._body_ks8oi_396
		List<WebElement> plans = driver.findElements(By.cssSelector("div._size\\:caption_1rra2_178"));
		List<WebElement> titles = driver.findElements(By.className("_title_ks8oi_172"));
		List<WebElement> bodies = driver.findElements(By.className("_body_ks8oi_396"));

		int count = Math.min(titles.size(), bodies.size());
		for (int i = 0; i < count; i++) {
			String plan = plans.get(i).getText().trim();
			// 特性解释页面有问题，谷歌的锅
			// if (plan.isEmpty()) {
			// plan = "Basic (100 GB)";
			// }
			String name = titles.get(i).getText().trim();
			String desc = bodies.get(i).getText().trim();
			if (!name.isEmpty() && !desc.isEmpty()) {
				benefits.add(new Benefit(plan, name, desc));
			}
		}
		return benefits;
	}

	private static void writePlansCSV(List<Plan> plans, String filename) {
		// I noticed that the CSV had some messed up characters, so I switched to UTF-8
		// encoding (with BOM) when writing the file. That way, it shows up correctly in
		// Excel and other editors.
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename),
				StandardCharsets.UTF_8)) {
			writer.write("\uFEFF");
			writer.append("Plan Name,Price,Features,Plan Type\n");
			for (Plan p : plans) {
				writer.append(escape(p.title)).append(",");
				writer.append(escape(p.price)).append(",");
				writer.append(escape(String.join("; ", p.features))).append(",");
				writer.append(escape(p.planType)).append("\n");
			}
		} catch (IOException e) {
			System.out.println("Failed to write plans.csv: " + e.getMessage());
		}
	}

	private static void writeBenefitsCSV(List<Benefit> benefits, String filename) {
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename),
				StandardCharsets.UTF_8)) {
			writer.write("\uFEFF");
			writer.append("Plan,Feature Name,Feature Description\n");
			for (Benefit b : benefits) {
				writer.append(escape(b.plan)).append(",");
				writer.append(escape(b.name)).append(",");
				writer.append(escape(b.description)).append("\n");
			}
		} catch (IOException e) {
			System.out.println("Failed to write features.csv: " + e.getMessage());
		}
	}

	// Some plan names or feature descriptions contain commas or quotes, which could
	// mess up the CSV format. So I added an escape() function to handle these cases
	// by wrapping the text in quotes and replacing any internal quotes with ""
	private static String escape(String text) {
		if (text.contains(",") || text.contains("\"")) {
			text = text.replace("\"", "\"\"");
			return "\"" + text + "\"";
		}
		return text;
	}
}

// Plan data model
class Plan {
	String title, price, planType;
	List<String> features;

	public Plan(String title, String price, List<String> features, String planType) {
		this.title = title;
		this.price = price;
		this.features = features;
		this.planType = planType;
	}
}

// Benefit data model
class Benefit {
	String plan, name, description;

	public Benefit(String plan, String name, String description) {
		this.plan = plan;
		this.name = name;
		this.description = description;
	}
}
