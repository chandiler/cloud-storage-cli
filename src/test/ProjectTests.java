package test;

import feature.*;
import model.Plan;
import model.UserRequest;
import feature.Recommender;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;

/**
 * 本类用于测试云存储 CLI 项目的关键功能模块，包括：
 * 特征提取、推荐逻辑、拼写检查、关键词补全、价格正则表达式匹配、
 * 以及最终生成的 cloud_storage.json 的结构验证。
 *
 * This class runs unit tests for key features of the cloud storage CLI project.
 * It checks feature extraction, recommender logic, spell checking, word completion,
 * price regex, and JSON structure validation.
 */
public class ProjectTests {
	public static void main(String[] args) {
		testFeatureExtraction();
		RecommenderTest();
		testWordCompletion();
		testRegexPricePattern();
		testSpellChecker();
		testCloudStorageJsonStructure();
	}

	/**
	 * 测试 FeatureExtractor 是否能从 JSON 文件中提取关键词和完整特征短语。
	 * Tests if the FeatureExtractor correctly identifies keywords from JSON.
	 */
	private static void testFeatureExtraction() {
		System.out.println("=== FeatureExtractor Test ===");
		FeatureExtractor extractor = new FeatureExtractor("data/cloud_storage.json");
		extractor.extractFeaturesFromJson();
		Set<String> words = extractor.singleWordFeatures;
		Set<String> full = extractor.completeFeatureStrings;
		if (!words.isEmpty() && !full.isEmpty()) {
			System.out.println("PASS: Extracted " + words.size() + " keywords.");
		} else {
			System.out.println("FAIL: Empty feature extraction.");
		}
	}

	/**
	 * 测试 Recommender 是否能根据用户需求筛选计划（平台、存储、关键词）。
	 * Tests if the Recommender filters plans based on user input.
	 */
	public static void RecommenderTest() {
		System.out.println("=== Result ===");
		Plan plan = new Plan();
		plan.setPlatform("Google Drive");
		plan.setPlanName("Premium (2 TB)");
		plan.setStorage("2 TB");
		plan.setFeatures(Arrays.asList("Documents", "Photos"));

		List<Plan> plans = new ArrayList<>();
		plans.add(plan);

		UserRequest req = new UserRequest();
		req.setBillingType("Monthly");
		req.setMinStorage("2 TB");
		req.setFeatureKeywords(Arrays.asList("Documents"));

		Recommender r = new Recommender();
		List<Plan> result = r.recommend(plans, req);

		for (Plan p : result) {
			System.out.println("Platform: " + p.getPlatform());
			System.out.println("Plan: " + p.getPlanName());
		}
	}

	/**
	 * 测试 WordCompleter 是否能完成前缀匹配。
	 * Tests if WordCompleter correctly completes word prefixes.
	 */
	private static void testWordCompletion() {
		System.out.println("=== WordCompleter Test ===");
		WordCompleter wc = new WordCompleter();
		wc.insertWords(Set.of("storage", "store", "start", "plan"));
		List<String> results = wc.complete("sto");
		if (results.contains("storage") && results.contains("store")) {
			System.out.println("PASS: Word completion works.");
		} else {
			System.out.println("FAIL: Word completion incorrect.");
		}
	}

	/**
	 * 测试价格正则表达式是否能正确匹配美元价格格式。
	 * Tests if price regex matches valid dollar-format prices.
	 */
	private static void testRegexPricePattern() {
		System.out.println("=== Regex Price Pattern Test ===");
		String price1 = "$0", price2 = "$9.99", price3 = "Free";
		String pattern = "^\\$\\d+(\\.\\d{1,2})?$";
		boolean p1 = price1.matches(pattern);
		boolean p2 = price2.matches(pattern);
		boolean p3 = price3.matches(pattern);
		if (p1 && p2 && !p3) {
			System.out.println("PASS: Price regex valid.");
		} else {
			System.out.println("FAIL: Price regex mismatch.");
		}
	}

	/**
	 * 测试拼写检查器是否能提供合理建议。
	 * Tests if the SpellChecker returns correct suggestions.
	 */
	private static void testSpellChecker() {
		System.out.println("=== SpellChecker Test ===");
		SpellChecker sc = new SpellChecker();
		sc.insertWords(Set.of("dropbox", "google", "onedrive"));
		List<String> out = sc.check("drobox", 2, 5);
		if (!out.isEmpty() && out.get(0).equals("dropbox")) {
			System.out.println("PASS: SpellChecker returns suggestion.");
		} else {
			System.out.println("FAIL: SpellChecker failed.");
		}
	}

	/**
	 * 检查 cloud_storage.json 是否包含所有必须字段，结构是否完整。
	 * Checks whether cloud_storage.json contains all required fields and has valid structure.
	 */
	private static void testCloudStorageJsonStructure() {
		System.out.println("=== cloud_storage.json Structure Test ===");

		try {
			String path = "data/cloud_storage.json";
			Scanner scanner = new Scanner(new File(path));
			StringBuilder jsonBuilder = new StringBuilder();
			while (scanner.hasNextLine()) {
				jsonBuilder.append(scanner.nextLine());
			}
			scanner.close();

			Gson gson = new Gson();
			JsonArray platforms = JsonParser.parseString(jsonBuilder.toString()).getAsJsonArray();

			boolean allValid = true;

			for (JsonElement platformEl : platforms) {
				JsonObject platform = platformEl.getAsJsonObject();
				String platformName = platform.get("Platform").getAsString();

				JsonArray plans = platform.getAsJsonArray("Plans");
				if (plans == null || plans.size() == 0) {
					System.out.println("FAIL: No plans under platform " + platformName);
					allValid = false;
					continue;
				}

				for (JsonElement planEl : plans) {
					JsonObject plan = planEl.getAsJsonObject();
					if (!plan.has("PlanName") || !plan.has("Storage") || !plan.has("Features")
							|| !plan.has("PricingOptions")) {
						System.out.println("FAIL: Missing field in plan under " + platformName);
						allValid = false;
					}
					JsonArray pricing = plan.getAsJsonArray("PricingOptions");
					for (JsonElement priceEl : pricing) {
						JsonObject priceObj = priceEl.getAsJsonObject();
						if (!priceObj.has("Price") || !priceObj.has("PlanType")) {
							System.out.println("FAIL: Pricing entry missing fields in plan under " + platformName);
							allValid = false;
						}
					}
				}
			}

			if (allValid) {
				System.out.println("PASS: All platform plans and pricing entries are structurally valid.");
			}

		} catch (Exception e) {
			System.out.println("FAIL: Exception during JSON structure test: " + e.getMessage());
		}
	}
}
