package cli;

import java.util.List;
import java.util.Set;

import cli.screens.*;
import cli.ui.ConsolePrinter;
import cli.utils.InputReader;
import cli.utils.JsonReader;
import feature.*;
import filter.UserFilter;
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

        while (true) {
            // Platform selection
            CloudStoragePlatform platform = PlatformSelectionScreen.show();
            ConsolePrinter.printInfo("Selected platform: " + platform + "\n");

            // Crawl and parse
            ConsolePrinter.printInfo("Crawling data from " + platform + " ...");
            new WebCrawler().run(platform.getDescription());
            List<Plan> plans = JsonReader.load("data/cloud_storage.json");
            ConsolePrinter.printSuccess("Data loaded.\n");

            // Feature Extraction
            FeatureExtractor extractor = new FeatureExtractor("data/cloud_storage.json");
            extractor.extractFeaturesFromJson();
            Set<String> featuresKeywords = extractor.singleWordFeatures;

            completer.insertWords(featuresKeywords);
            SpellChecker spellChecker = new SpellChecker();
            spellChecker.insertWords(featuresKeywords);
            InputReader.initAutoComplete(completer);

            // Prepare user request object
            UserRequest request = UserFilter.collect();
            request.setPlatform(platform); // <-- IMPORTANT: set platform in request

            // Step 1: Subscription Plan
            SubscriptionPlan subscriptionPlan = SubscriptionPlanScreen.show();
            request.setSubscriptionPlan(subscriptionPlan);
            ConsolePrinter.printInfo("Selected Subscription Plan: " + subscriptionPlan.getDescription() + "\n");

            // Step 2: Budget selection by plan
            BudgetRange[] allowedBudgets = BudgetRange.getBySubscriptionPlan(subscriptionPlan);
            BudgetRange selectedBudget = BudgetSelectionScreen.show(subscriptionPlan, allowedBudgets);
            request.setBudgetRange(selectedBudget);
            ConsolePrinter.printInfo("Selected Budget: " + selectedBudget.getDescription() + "\n");

            // Step 3: Storage selection
            StorageRange[] allowedStorage = StorageRange.values(); // (Optional: can be filtered by budget)
            StorageRange selectedStorage = StorageCapacityScreen.show(allowedStorage);
            request.setStorageRange(selectedStorage);
            ConsolePrinter.printInfo("Selected Storage: " + selectedStorage.getDescription() + "\n");

            // Step 4: Feature selection with spell checker
            FeatureInputScreen featureInputScreen = new FeatureInputScreen(spellChecker);
            List<String> selectedFeatures = featureInputScreen.showAndGetResult();
            request.setFeatureKeywords(selectedFeatures);

            // Step 5: Run recommender
            List<Plan> filteredPlans = new Recommender().recommend(plans, request);
            List<Plan> ranked = new MatchedPlansScreen().showAndGetResult(filteredPlans);
            if (!ranked.isEmpty()) {
                //RecommendationScreen.show(ranked.get(0));
            	for (Plan plan : ranked) {
                    RecommendationScreen.show(plan);
                }
            }

            // Ask to continue
            if (!ContinueSearchScreen.show()) {
                ConsolePrinter.printSuccess("Thank you for using our service. Goodbye!");
                break;
            }
        }
    }
}