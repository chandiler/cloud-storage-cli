package cli;

import java.util.List;
import java.util.Set;

import cli.screens.*;
import cli.ui.ConsolePrinter;
import cli.utils.InputReader;
import feature.*;
import model.Plan;
import model.UserRequest;
import types.BudgetRange;
import types.CloudStoragePlatform;
import types.StorageRange;
import types.SubscriptionPlan;

public class Main {
	public static void main(String[] args) {
		// Autocomplete
		WordCompleter completer = new WordCompleter();

		// Platform selection
		CloudStoragePlatform platform = PlatformSelectionScreen.show();
		ConsolePrinter.printInfo("Selected platform: " + platform + "\n");

		// Crawl and parse
		ConsolePrinter.printInfo("Crawling data from " + platform + " ...");
		String rawHtml = new WebCrawler().crawl(platform.getDescription());
		List<Plan> plans = new HtmlParser().parse(rawHtml);
		ConsolePrinter.printSuccess("Data loaded.\n");

		// Load words from crawled data
		completer.insertWords(
				Set.of("admin", "dropbox", "drive", "onedrive", "icloud", "storage", "encryption", "version history"));
		InputReader.initAutoComplete(completer);

		// Subscription Plan
		SubscriptionPlan subscriptionPlan = SubscriptionPlanScreen.show();
		ConsolePrinter.printInfo("Selected Subscription Plan: " + subscriptionPlan.getDescription() + "\n");

		// Filtered Budget options
		BudgetRange[] allowedBudgets = BudgetRange.getBySubscriptionPlan(subscriptionPlan);
		BudgetRange selectedBudget = BudgetSelectionScreen.show(allowedBudgets);
		ConsolePrinter.printInfo("Selected Budget: " + selectedBudget.getDescription() + "\n");

		// Optional: Filter by storage capacity
		StorageRange[] allowedStorage = StorageRange.getByBudget(selectedBudget);
		StorageRange selectedStorage = StorageCapacityScreen.show(allowedStorage);
		ConsolePrinter.printInfo("Selected Storage: " + selectedStorage.getDescription() + "\n");

		// Optional: Ask for features
		List<String> selectedFeatures = new FeatureInputScreen().showAndGetResult();

		// Build user request
		UserRequest request = new FilterInputScreen().showAndGetResult();
		request.setFeatureKeywords(selectedFeatures);
		request.setSubscriptionPlan(subscriptionPlan);
		request.setBudgetRange(selectedBudget);
		request.setStorageRange(selectedStorage); // Can be null if not filtered

		// Recommendation
		List<Plan> filteredPlans = new Recommender().recommend(plans, request);

		List<Plan> ranked = new MatchedPlansScreen().showAndGetResult(filteredPlans);
		if (!ranked.isEmpty()) {
			RecommendationScreen.show(ranked.get(0));
		}
	}
}
