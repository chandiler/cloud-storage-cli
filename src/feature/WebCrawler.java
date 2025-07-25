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
		// call cli.screen. menuscreen()

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
		if (selected.equals("onedrive") || selected.equals("all")) {
			allPlatforms.add(crawlOneDrive(driver));
		}
		if (selected.equals("box") || selected.equals("all")) {
			allPlatforms.add(crawlBox(driver));
		}

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
		plans = extractPlans(driver, "Monthly", plans, extractor, "div.staJ7e, div[data-testid='plan-card']");
		switchToGoogleAnnual(driver);
		plans = extractPlans(driver, "Annual", plans, extractor, "div.staJ7e, div[data-testid='plan-card']");

		return buildPlatformObject("Google Drive", plans, Arrays.asList("Monthly", "Annual"),
				Arrays.asList("Documents", "Photos", "Videos", "Large Files"),
				"Supports upload of single files up to 5TB (non-Google formats)",
				Arrays.asList("Google Photos", "Gmail", "Google Workspace"),
				"Supports upgrade/downgrade at any time, prorated daily");
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

	// Dropbox 爬虫流程（重构版，复用 extractPlans）
	// Dropbox crawling logic (refactored with extractPlans)
	private static Map<String, Object> crawlDropbox(WebDriver driver) {
		driver.get("https://www.dropbox.com/plans?billing=monthly");
		sleep(1500);

		// 页面下拉以加载完整内容
		// Scroll to ensure content loads
		try {
			for (int i = 0; i < 3; i++) {
				((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 800);");
				Thread.sleep(1000);
			}
		} catch (Exception ignored) {
		}

		PlanExtractor extractor = new DropboxPlanExtractor();
		List<Map<String, Object>> plans = new ArrayList<>();

		// 提取月付计划 / Extract monthly plans
		// plans = extractPlans(driver, "Monthly", plans, extractor);
		plans = extractPlans(driver, "Monthly", plans, extractor, "div._dwg-plan-card-v2__section_nnw6p_6");

		// 切换到年付 / Switch to annual plans
		switchToDropboxAnnual(driver);
		sleep(1500);

		// 再次下拉加载页面内容 / Scroll again for annual section
		try {
			for (int i = 0; i < 3; i++) {
				((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 800);");
				Thread.sleep(1000);
			}
		} catch (Exception ignored) {
		}

		// 提取年付计划 / Extract annual plans
		plans = extractPlans(driver, "Annual", plans, extractor, "div._dwg-plan-card-v2__section_nnw6p_6");

		// 构建平台结构输出 / Build final structured output
		return buildPlatformObject("Dropbox", plans, Arrays.asList("Monthly", "Annual"),
				Arrays.asList("Documents", "Photos", "Videos"), "Free plan: web upload limit is 2GB per file",
				new ArrayList<>(), // Integrations
				"Supports upgrade; some plans do not support downgrade");
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

	// OneDrive 爬虫流程
	// OneDrive crawling logic
	private static Map<String, Object> crawlOneDrive(WebDriver driver) {
		driver.get("https://www.microsoft.com/en-ca/microsoft-365/onedrive/compare-onedrive-plans");
		sleep(1500);

		// 模拟滚动，确保页面加载全部计划
		// Scroll to load all plan blocks
		try {
			for (int i = 0; i < 3; i++) {
				((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 800);");
				Thread.sleep(1000);
			}
		} catch (Exception ignored) {
		}

		// 初始化解析器
		// Initialize extractor
		PlanExtractor extractor = new OneDrivePlanExtractor();

		// 抓取所有卡片容器，但只使用前4个（剩下为未显示内容）
		// Fetch all cards but only use first 4 (others are hidden)
		List<WebElement> allBlocks = driver.findElements(By.cssSelector("div.sku-card.g-col-12.g-start-1"));
		List<WebElement> blocks = allBlocks.subList(0, Math.min(4, allBlocks.size()));
		// System.out.println("Block size: " + blocks.size());

		// 抓取年付计划信息
		// Extract annual plan info
		List<Map<String, Object>> plans = extractPlans(driver, "Annual", new ArrayList<>(), extractor,
				"div.sku-card.g-col-12.g-start-1");

		// 手动加入月付价格
		// Manually add monthly price per card
		for (int i = 0; i < blocks.size(); i++) {
			WebElement block = blocks.get(i);
			String title = extractor.extractTitle(block);

			// 向下转型以访问 OneDrivePlanExtractor 的特有方法
			// Cast to OneDrivePlanExtractor to access monthly method
			String monthlyPrice = ((OneDrivePlanExtractor) extractor).extractMonthlyPrice(block);

			for (Map<String, Object> plan : plans) {
				if (plan.get("PlanName").equals(title)) {
					@SuppressWarnings("unchecked")
					List<Map<String, String>> pricing = (List<Map<String, String>>) plan.get("PricingOptions");

					Map<String, String> monthly = new LinkedHashMap<>();
					monthly.put("PlanType", "Monthly");
					monthly.put("Price", monthlyPrice);
					pricing.add(monthly);
				}
			}
		}

		// 构建最终输出结构
		// Build final platform object
		return buildPlatformObject("OneDrive", plans, Arrays.asList("Monthly", "Annual"),
				Arrays.asList("Documents", "Photos", "Videos"),
				"Business plans require minimum 1–3 users, depending on tier", new ArrayList<>(), // integrations
				"Supports upgrade and downgrade through Microsoft Account");
	}

	private static Map<String, Object> crawlBox(WebDriver driver) {
		driver.get("https://www.box.com/pricing/individual");
		sleep(1500);

		try {
			for (int i = 0; i < 2; i++) {
				((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 800);");
				Thread.sleep(800);
			}
		} catch (Exception ignored) {
		}

		PlanExtractor extractor = new BoxPlanExtractor();
		String selector = "div.pricing-plans-row.pricing-plans--has-common-features div.pricing-package.pricing-package--recommended";

		// 两次调用 extractPlans，合并月付与年付
		List<Map<String, Object>> plans = extractPlans(driver, "Annual", new ArrayList<>(), extractor, selector);
		plans = extractPlans(driver, "Monthly", plans, extractor, selector);

		return buildPlatformObject("Box", plans, Arrays.asList("Monthly", "Annual"),
				Arrays.asList("Documents", "Photos", "Videos", "Audio"),
				"Free plan has limited integrations; paid plans include Microsoft 365 and G Suite support",
				new ArrayList<>(), "Supports upgrading online via Box account");
	}

	// 提取所有计划并合并付费选项（选择器参数化）
	// Extract and merge plan data across billing cycles (with flexible selector)
	private static List<Map<String, Object>> extractPlans(WebDriver driver, String planType,
			List<Map<String, Object>> existingPlans, PlanExtractor extractor, String blockSelector) {

		Map<String, Map<String, Object>> planMap = new LinkedHashMap<>();

		// 将已有计划映射为 Map 方便后续合并
		// Map existing plans for merging
		for (Map<String, Object> plan : existingPlans) {
			String name = (String) plan.get("PlanName");
			planMap.put(name, plan);
		}

		// 获取所有计划块元素（通过参数传入的选择器）
		// Get all plan block elements using provided selector
		List<WebElement> allBlocks = driver.findElements(By.cssSelector(blockSelector));
		List<WebElement> blocks = allBlocks.subList(0, Math.min(4, allBlocks.size()));

		for (WebElement block : blocks) {
			String title = extractor.extractTitle(block);

			// 跳过 Google AI Pro 年付（它只支持月付）
			// Skip Google AI Pro Annual
			if (title.equalsIgnoreCase("Google AI Pro (2 TB)") && planType.equalsIgnoreCase("Annual")) {
				continue;
			}

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
				// System.out.print(111);
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

	private static String extractStorageFromFeatures(List<String> features) {
		Pattern pattern = Pattern.compile("(?i)(\\d+(\\.\\d+)?\\s*(GB|TB))");
		// System.out.print(features);

		for (String f : features) {
			// 只处理包含“of storage”或“of cloud storage”的行
			String lower = f.toLowerCase();
			// System.out.print(lower);
			if (lower.contains("of storage") || lower.contains("of cloud storage") || lower.contains("for the team")) {
				Matcher matcher = pattern.matcher(f);
				if (matcher.find()) {
					return matcher.group(1); // 提取 "1 TB" 这样的字段
				}
			}
		}
		return "Unknown";
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
