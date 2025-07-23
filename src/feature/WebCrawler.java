package feature;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class WebCrawler {

	public static void main(String[] args) {

		// 获取用户输入平台
		// Ask user which platform to crawl
		Scanner scanner = new Scanner(System.in);
		System.out.println("Select platform to crawl: Google / Dropbox / Box / OneDrive / All");
		System.out.print(">>> "); // 给个明显提示
		String selected = scanner.nextLine().trim().toLowerCase();
		System.out.println("You selected: " + selected);
		scanner.close();

		// 启动 Chrome 浏览器无痕模式
		// Launch Chrome in incognito mode
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--lang=en");
		options.addArguments("--incognito");
		Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
		WebDriver driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

		List<Map<String, Object>> allPlatforms = new ArrayList<>();

		if (selected.equals("google") || selected.equals("all")) {
			// System.out.println(1111111);
			allPlatforms.add(crawlGoogle(driver));
		}
		if (selected.equals("dropbox") || selected.equals("all")) {
			// System.out.println(2111111);
			allPlatforms.add(crawlDropbox(driver));
		}
		// if (selected.equals("box") || selected.equals("all")) { ... }
		// if (selected.equals("onedrive") || selected.equals("all")) { ... }

		// 输出 JSON 文件
		// Write output to JSON
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try (FileWriter writer = new FileWriter("data/cloud_storage.json", StandardCharsets.UTF_8)) {
			gson.toJson(allPlatforms, writer);
			System.out.println("Saved to data/cloud_storage.json");
		} catch (IOException e) {
			System.out.println("Failed to write JSON: " + e.getMessage());
		}

		driver.quit();
	}

	// Google Drive 爬虫流程
	// Google Drive crawling logic
	private static Map<String, Object> crawlGoogle(WebDriver driver) {
		driver.get("https://one.google.com/about/plans?hl=en");
		sleep(1500);
		PlanExtractor extractor = new GooglePlanExtractor();
		List<Map<String, Object>> plans = new ArrayList<>();
		plans = extractPlans(driver, "Monthly", plans, extractor);
		switchToGoogleAnnual(driver);
		plans = extractPlans(driver, "Annual", plans, extractor);

		return buildPlatformObject("Google Drive", plans, Arrays.asList("Monthly", "Annual"),
				Arrays.asList("Documents", "Photos", "Videos", "Large Files"),
				"Supports upload of single files up to 5TB (non-Google formats)",
				Arrays.asList("Google Photos", "Gmail", "Google Workspace"),
				"Supports upgrade/downgrade at any time, prorated daily");
	}

	// Dropbox 爬虫流程
	// Dropbox crawling logic
	private static Map<String, Object> crawlDropbox(WebDriver driver) {
		driver.get("https://www.dropbox.com/plans?billing=monthly");
		sleep(1500);

		try {
			for (int i = 0; i < 3; i++) {
				((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500);");
				Thread.sleep(1000);
			}
		} catch (Exception ignored) {
		}

		PlanExtractor extractor = new DropboxPlanExtractor();
		Map<String, Map<String, Object>> planMap = new LinkedHashMap<>();

		// 月付
		List<WebElement> monthlyBlocks = driver.findElements(By.cssSelector("div._dwg-plan-card-v2__section_nnw6p_6"));
		for (WebElement block : monthlyBlocks) {
			String title = extractor.extractTitle(block);
			String price = extractor.extractPrice(block, "Monthly");
			List<String> features = extractor.extractFeatures(block);

			Map<String, Object> plan = planMap.getOrDefault(title, new LinkedHashMap<>());
			plan.put("PlanName", title);
			plan.put("Features", features);
			plan.put("Storage", extractStorageFromFeatures(features));
			// System.out.print(plan);
			List<Map<String, String>> pricing = (List<Map<String, String>>) plan.getOrDefault("PricingOptions",
					new ArrayList<>());
			pricing.add(Map.of("PlanType", "Monthly", "Price", price));
			plan.put("PricingOptions", pricing);

			planMap.put(title, plan);
		}

		// 切换年付
		switchToDropboxAnnual(driver);
		sleep(1500);

		try {
			for (int i = 0; i < 3; i++) {
				((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500);");
				Thread.sleep(1000);
			}
		} catch (Exception ignored) {
		}

		// 年付
		List<WebElement> annualBlocks = driver.findElements(By.cssSelector("div._dwg-plan-card-v2__section_nnw6p_6"));
		for (WebElement block : annualBlocks) {
			String title = extractor.extractTitle(block);
			String price = extractor.extractPrice(block, "Annual");
			List<String> features = extractor.extractFeatures(block);

			Map<String, Object> plan = planMap.getOrDefault(title, new LinkedHashMap<>());
			plan.put("PlanName", title);
			plan.put("Features", features);
			plan.put("Storage", extractStorageFromFeatures(features));

			List<Map<String, String>> pricing = (List<Map<String, String>>) plan.getOrDefault("PricingOptions",
					new ArrayList<>());
			pricing.add(Map.of("PlanType", "Annual", "Price", price));
			plan.put("PricingOptions", pricing);

			planMap.put(title, plan);
		}

		// 最终结构输出
		List<Map<String, Object>> plans = new ArrayList<>(planMap.values());

		return buildPlatformObject("Dropbox", plans, Arrays.asList("Monthly", "Annual"),
				Arrays.asList("Documents", "Photos", "Videos"), "Free plan: web upload limit is 2GB per file",
				new ArrayList<>(), // Integrations
				"Supports upgrade; some plans do not support downgrade");
	}

	private static String extractStorageFromFeatures(List<String> features) {
		for (String f : features) {
			String match = f.replaceAll("(?i).*?(\\d+(\\.\\d+)?\\s*(GB|TB)).*", "$1").toUpperCase();
			if (!match.equalsIgnoreCase(f))
				return match;
		}
		return "N/A";
	}

	// 切换 Google 年付按钮
	// Toggle to Annual plans on Google
	private static void switchToGoogleAnnual(WebDriver driver) {
		try {
			WebElement toggle = driver.findElement(By.className("wnccze"));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", toggle);
			sleep(300);
		} catch (Exception e) {
			System.out.println("Google toggle failed: " + e.getMessage());
		}
	}

	// 切换 Dropbox 年付按钮
	// Toggle to Annual plans on Dropbox
	private static void switchToDropboxAnnual(WebDriver driver) {
		try {
			WebElement input = driver.findElement(
					By.cssSelector("input[type='radio'][value='yearly'][data-uxa-log='billing_period_yearly_toggle']"));
			WebElement label = input.findElement(By.xpath("./parent::label"));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);
			Thread.sleep(1500);
		} catch (Exception e) {
			System.out.println("切换年付失败：" + e.getMessage());
		}
	}

	// 提取所有计划并合并付费选项
	// Extract and merge plan data across billing cycles
	private static List<Map<String, Object>> extractPlans(WebDriver driver, String planType,
			List<Map<String, Object>> existingPlans, PlanExtractor extractor) {
		Map<String, Map<String, Object>> planMap = new LinkedHashMap<>();
		for (Map<String, Object> plan : existingPlans) {
			String name = (String) plan.get("PlanName");
			planMap.put(name, plan);
		}

		List<WebElement> blocks = driver.findElements(By.cssSelector("div.staJ7e, div[data-testid='plan-card']"));
		for (WebElement block : blocks) {
			String title = extractor.extractTitle(block);
			String price = extractor.extractPrice(block, planType);
			List<String> features = extractor.extractFeatures(block);

			Map<String, String> priceEntry = new LinkedHashMap<>();
			priceEntry.put("PlanType", planType);
			priceEntry.put("Price", price);

			Map<String, Object> plan;
			if (planMap.containsKey(title)) {
				plan = planMap.get(title);
				@SuppressWarnings("unchecked")
				List<Map<String, String>> pricing = (List<Map<String, String>>) plan.get("PricingOptions");
				pricing.add(priceEntry);
			} else {
				plan = new LinkedHashMap<>();
				plan.put("PlanName", title);
				System.out.print(plan);
				plan.put("Storage", extractStorageFromFeatures(features));
				plan.put("Features", features);
				List<Map<String, String>> pricing = new ArrayList<>();
				pricing.add(priceEntry);
				plan.put("PricingOptions", pricing);
				planMap.put(title, plan);
			}
		}
		return new ArrayList<>(planMap.values());
	}

	// 构建平台 JSON 对象
	// Build structured platform-level JSON object
	private static Map<String, Object> buildPlatformObject(String name, List<Map<String, Object>> plans,
			List<String> paymentFreq, List<String> fileTypes, String notes, List<String> integrations,
			String upgradeInfo) {
		Map<String, Object> platform = new LinkedHashMap<>();
		platform.put("Platform", name);
		platform.put("PaymentFrequency", paymentFreq);
		platform.put("AvailableStorageOptions", extractStorageOptions(plans));
		platform.put("UpgradePaths", upgradeInfo);
		platform.put("FileTypesSupported", fileTypes);
		platform.put("Notes", notes);

		Map<String, Object> compatibility = new LinkedHashMap<>();
		compatibility.put("PC", null);
		compatibility.put("Mac", null);
		compatibility.put("Mobile", null);
		compatibility.put("Integrations", integrations);
		platform.put("PlatformCompatibility", compatibility);

		platform.put("Plans", plans);
		return platform;
	}

	// 提取出现过的存储选项
	// Extract unique storage values
	private static Set<String> extractStorageOptions(List<Map<String, Object>> plans) {
		Set<String> storage = new LinkedHashSet<>();
		for (Map<String, Object> plan : plans) {
			Object s = plan.get("Storage");
			if (s instanceof String) {
				String value = ((String) s).trim();
				if (!value.isEmpty() && !value.equalsIgnoreCase("N/A")) {
					storage.add(value);
				}
			} else if (s == null || String.valueOf(s).trim().isEmpty()
					|| "N/A".equalsIgnoreCase(String.valueOf(s).trim())) {
				// 可在此加入 fallback 逻辑，例如从 Features 提取（你若需要我可补）
				// 示例：plan.put("Storage", extractFromFeatures(plan.get("Features")));
			}
		}
		return storage;
	}

	// // 从标题推测存储容量
	// // Infer storage from plan name
	// private static String extractStorageFromTitle(String title) {
	// if (title.contains("15"))
	// return "15GB";
	// if (title.contains("100"))
	// return "100GB";
	// if (title.contains("2"))
	// return "2TB";
	// if (title.contains("3"))
	// return "3TB";
	// if (title.contains("5"))
	// return "5TB";
	// if (title.contains("6"))
	// return "6TB";
	// return title;
	// }

	// 通用文本抓取工具
	// Shared text extraction helper
	public static String getTextOrNA(WebElement parent, By selector) {
		List<WebElement> found = parent.findElements(selector);
		return found.isEmpty() ? "N/A" : found.get(0).getText().trim();
	}

	// 睡眠工具
	// Sleep helper
	private static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ignored) {
		}
	}
}
