package cli;

import java.util.List;

import cli.screens.FeatureInputScreen;
import cli.screens.FilterInputScreen;
import cli.screens.MatchedPlansScreen;
import cli.screens.PlatformSelectionScreen;
import cli.screens.RecommendationScreen;
import cli.ui.ConsolePrinter;
import feature.HtmlParser;
import feature.Recommender;
import feature.WebCrawler;
import model.Plan;
import model.UserRequest;

public class Main {
    public static void main(String[] args) {
    	// Getting Platform 
		String platform =  PlatformSelectionScreen.show();
		
		// Crawling
		ConsolePrinter.printInfo("Crawling data from " + platform + " ...");
        String rawHtml = new WebCrawler().crawl(platform);
        List<Plan> plans = new HtmlParser().parse(rawHtml);
        ConsolePrinter.printSuccess("Data loaded.\n");
		
        // Asking for features
        List<String> selectedFeatures = new FeatureInputScreen().showAndGetResult();
		
		// User Filters
        UserRequest request = new FilterInputScreen().showAndGetResult();
		request.setFeatureKeywords(selectedFeatures);
		
		List<Plan> filteredPlans = new Recommender().recommend(plans, request);
		
		List<Plan> ranked = new MatchedPlansScreen().showAndGetResult(filteredPlans);
		if (ranked.isEmpty()) {
		    return;
		}
		
		RecommendationScreen.show(ranked.get(0));
    }
}
