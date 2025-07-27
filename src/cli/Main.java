package cli;

import java.util.List;
import java.util.Set;

import cli.screens.*;
import cli.ui.ConsolePrinter;
import cli.utils.InputReader;
import feature.*;
import filter.UserFilter;
import model.Plan;
import model.UserRequest;
import types.BudgetRange;
import types.CloudStoragePlatform;
import types.StorageRange;
import types.SubscriptionPlan;
import java.util.concurrent.CompletableFuture;

public class Main {

	public static void main(String[] args) {
		// Autocomplete
		WordCompleter completer = new WordCompleter();

		while(true) {
			// Platform selection
			CloudStoragePlatform platform = PlatformSelectionScreen.show();
			ConsolePrinter.printInfo("Selected platform: " + platform + "\n");

			// Crawl and parse
			ConsolePrinter.printInfo("Crawling data from " + platform + " ...");
			// new WebCrawler().run(platform.getDescription());
			// 异步任务
			// asynac task
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				new WebCrawler().run(platform.getDescription());
			});
			future.join();
			// List<Plan> plans = new HtmlParser().parse(rawHtml);
			//List<Plan> plans = List.of(); // Placeholder: Replace with actual data loading
			List<Plan> plans = JsonReader.load("data/cloud_storage.json");
			ConsolePrinter.printSuccess("Data loaded.\n");
			FeatureExtractor extractor = new FeatureExtractor("data/cloud_storage.json");
			extractor.extractFeaturesFromJson();
			Set<String> featuresKeywords = extractor.singleWordFeatures;
			completer.insertWords(featuresKeywords);
			SpellChecker spellChecker = new SpellChecker();
			spellChecker.insertWords(featuresKeywords);
			InputReader.initAutoComplete(completer);

			// Subscription Plan
			// SubscriptionPlan subscriptionPlan = SubscriptionPlanScreen.show();
			// ConsolePrinter.printInfo("Selected Subscription Plan: " +
			// subscriptionPlan.getDescription() + "\n");
			//
			// // Filtered Budget options
			// BudgetRange[] allowedBudgets =
			// BudgetRange.getBySubscriptionPlan(subscriptionPlan);
			// BudgetRange selectedBudget = BudgetSelectionScreen.show(allowedBudgets);
			// ConsolePrinter.printInfo("Selected Budget: " +
			// selectedBudget.getDescription() + "\n");
			//
			// // Optional: Filter by storage capacity
			/*StorageRange[] allowedStorage = StorageRange.getByBudget(selectedBudget);
			StorageRange selectedStorage = StorageCapacityScreen.show(allowedStorage);*/
			// ConsolePrinter.printInfo("Selected Storage: " +
			// selectedStorage.getDescription() + "\n");

			// Optional: Ask for features

			UserRequest request = UserFilter.collect();

			SubscriptionPlan subscriptionPlan = SubscriptionPlanScreen.show();
			request.setSubscriptionPlan(subscriptionPlan);
			BudgetRange[] allowedBudgets = BudgetRange.getBySubscriptionPlan(subscriptionPlan);
			ConsolePrinter.printInfo("Selected Subscription Plan: " + subscriptionPlan.getDescription() + "\n");

			BudgetRange selectedBudget = BudgetSelectionScreen.show(subscriptionPlan, allowedBudgets);
			request.setBudgetRange(selectedBudget);
			ConsolePrinter.printInfo("Selected Budget: " + selectedBudget.getDescription() + "\n");

			StorageRange[] allowedStorage = StorageRange.values();
			StorageRange selectedStorage = StorageCapacityScreen.show(allowedStorage);
			request.setStorageRange(selectedStorage);
			ConsolePrinter.printInfo("Selected Storage: " + selectedStorage.getDescription() + "\n");

			FeatureInputScreen featureInputScreen = new FeatureInputScreen(spellChecker);
			List<String> selectedFeatures = featureInputScreen.showAndGetResult();
			request.setFeatureKeywords(selectedFeatures);
			ConsolePrinter.printInfo("Selected Storage: " + selectedStorage.getDescription() + "\n");
			selectedFeatures.forEach(System.out::println);

			List<Plan> filteredPlans = new Recommender().recommend(plans, request);
			List<Plan> ranked = new MatchedPlansScreen().showAndGetResult(filteredPlans);
			if (!ranked.isEmpty()) {
				RecommendationScreen.show(ranked.get(0));
			}

			if (!ContinueSearchScreen.show()) {
				ConsolePrinter.printSuccess("Thank you for using our service. Goodbye!");
				break;
			}

			// Build user request
			// UserRequest request = new FilterInputScreen().showAndGetResult();
			/*UserRequest request = new UserRequest();
			request.setFeatureKeywords(selectedFeatures);
			request.setSubscriptionPlan(subscriptionPlan);
			request.setBudgetRange(selectedBudget);
			request.setStorageRange(selectedStorage); // Can be null if not filtered
			
			// Recommendation
			List<Plan> filteredPlans = new Recommender().recommend(plans, request);
			
			List<Plan> ranked = new MatchedPlansScreen().showAndGetResult(filteredPlans);
			if (!ranked.isEmpty()) {
				RecommendationScreen.show(ranked.get(0));
			}*/
		}

	}

	/* For testing without wordcompletor and spellchecking features
	public static void main(String[] args) {
		// Autocomplete
		//WordCompleter completer = new WordCompleter();
		
		while(true) {
			// Platform selection
			CloudStoragePlatform platform = PlatformSelectionScreen.show();
			UserRequest request = UserFilter.collect();
			request.setPlatform(platform);
			ConsolePrinter.printInfo("Selected platform: " + platform + "\n");
	
			// Crawl and parse
			ConsolePrinter.printInfo("Crawling data from " + platform + " ...");
			//String rawHtml = new WebCrawler().crawl(platform.getDescription());
			//List<Plan> plans = new HtmlParser().parse(rawHtml);
			//List<Plan> plans = List.of(); // Placeholder: Replace with actual data loading
			
			List<Plan> plans = JsonReader.load("cloud_storage.json");
			//List<Plan> plans = JsonReader.load("cloud_storage_data_en_cleaned.json");
			
			for (Plan plan : plans) {
			    System.out.printf("DEBUG: %-20s | Features: %s%n",
			        plan.getPlanName(),
			        plan.getFeatures() != null ? String.join(", ", plan.getFeatures()) : "null"
			    );
			}
			
			// Print for debug
			/*for (Plan plan : plans) {
			    System.out.printf("Loaded Plan: name=%s | storage=%s | priceCount=%d%n",
			        plan.getPlanName(), plan.getStorage(),
			        plan.getPricingOptions() != null ? plan.getPricingOptions().size() : 0);
			}*/
	// ConsolePrinter.printSuccess("Data loaded.\n");

	// Load words from crawled data
	/*
	completer.insertWords(
			Set.of("admin", "dropbox", "drive", "onedrive", "icloud", "storage", "encryption", "version history"));
	InputReader.initAutoComplete(completer);
	*/

	/*
	SubscriptionPlan subscriptionPlan = SubscriptionPlanScreen.show();
	request.setSubscriptionPlan(subscriptionPlan);
	ConsolePrinter.printInfo("Selected Subscription Plan: " + subscriptionPlan.getDescription() + "\n");
	
	BudgetRange[] allowedBudgets = BudgetRange.getBySubscriptionPlan(subscriptionPlan);
	BudgetRange selectedBudget = BudgetSelectionScreen.show(allowedBudgets);
	request.setBudgetRange(selectedBudget);
	ConsolePrinter.printInfo("Selected Budget: " + selectedBudget.getDescription() + "\n");
	
	StorageRange[] allowedStorage = StorageRange.values();
	StorageRange selectedStorage = StorageCapacityScreen.show(allowedStorage);
	request.setStorageRange(selectedStorage);
	ConsolePrinter.printInfo("Selected Storage: " + selectedStorage.getDescription() + "\n");
	
	List<String> selectedFeatures = new FeatureInputScreen().showAndGetResult();
	request.setFeatureKeywords(selectedFeatures);
	
	List<Plan> filteredPlans = new Recommender().recommend(plans, request);
	List<Plan> ranked = new MatchedPlansScreen().showAndGetResult(filteredPlans);
	if (!ranked.isEmpty()) {
	    RecommendationScreen.show(ranked.get(0));
	}
	
	if (!ContinueSearchScreen.show()) {
	    ConsolePrinter.printSuccess("Thank you for using our service. Goodbye!");
	    break;
	}
	
	} 
	}*/
}