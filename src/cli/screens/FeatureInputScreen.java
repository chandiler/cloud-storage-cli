package cli.screens;

import cli.ui.BaseScreen;
import cli.ui.ConsolePrinter;
import cli.utils.InputReader;
import feature.FeatureExtractor;
import feature.SpellChecker;
import feature.WordCompleter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeatureInputScreen extends BaseScreen {
	private final WordCompleter wordCompleter = new WordCompleter();
	private final SpellChecker spellChecker = new SpellChecker();

	public FeatureInputScreen() {
		super("Feature Keyword Input");
//		String jsonPath = "data/cloud_storage_data_en_cleaned.json";
//
//		FeatureExtractor featureExtractor = new FeatureExtractor(jsonPath);
//		featureExtractor.extractFeaturesFromJson();

		spellChecker.insertWords(getFeatures());
		
//		wordCompleter.insertWords(featureExtractor.singleWordFeatures);
//		wordCompleter.printAllWords();
	}
	
	 public static Set<String> getFeatures() {
	        Set<String> features = new HashSet<>();
	        features.add("Advanced workflow capabilities");
	        features.add("Advanced key management");
	        features.add("Ad-free Outlook and mobile email and calendar with advanced security features");
	        features.add("admin control");
	        features.add("Automated controls protecting against threats and data leaks");
	        features.add("Automatic spam and malware filtering");
	        features.add("AI-powered content portals with intelligent Hubs");
	        features.add("AI-powered image creation and editing with Microsoft Designer");
	        features.add("All other benefits in the Premium plan");
	        features.add("Anytime phone and web support");
	        features.add("Brand your files to share");
	        return features;
	    }

	public List<String> showAndGetResult() {
		printBoxTitle();

		while (true) {
			String keyword = InputReader.readString("Do you have a specific feature in mind?\n(Press Enter to skip)")
					.trim();

			if (keyword.isEmpty()) {
				return null; // skip input
			}

			String corrected = spellChecker.check(keyword);

			// Check if the selected keyword is correct
			if (!corrected.equalsIgnoreCase(keyword)) {
				boolean confirmed = InputReader.readYesNo("Did you mean: " + corrected + "?");
				if (!confirmed) {
					ConsolePrinter.printInfo("Let's try again.");
					// Get back to the input loop
					continue; 
				}
			}

			
			List<String> suggestions = wordCompleter.complete(corrected);

			if (suggestions.isEmpty()) {
				ConsolePrinter.printInfo("No related features found.");
				boolean tryAgain = InputReader.readYesNo("Do you want to try a different keyword?");
				if (tryAgain) continue;
				else return null;
			}

			System.out.println("Related features:");
			for (String s : suggestions) {
				System.out.println(" - " + s);
			}

			boolean use = InputReader.readYesNo("Use these features to filter plans?");
			if (use) return suggestions;
			else {
				ConsolePrinter.printInfo("You can try entering a different feature.");
			}
		}
	}

	@Override
	public void show() {
		// no-op; usamos showAndGetResult()
	}
}
