package cli.screens;

import cli.ui.ConsolePrinter;
import cli.ui.MenuOption;
import cli.ui.MenuScreen;
import cli.utils.InputReader;
import feature.SpellChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles the input and selection of features using SpellChecker suggestions.
 * Allows up to 3 unique features to be selected by the user.
 */
public class FeatureInputScreen {

    private final SpellChecker spellChecker;

    /**
     * Constructor to initialize FeatureInputScreen with a SpellChecker instance.
     *
     * @param spellChecker SpellChecker used to suggest corrected feature names.
     */
    public FeatureInputScreen(SpellChecker spellChecker) {
        this.spellChecker = spellChecker;
    }

    /**
     * Displays the feature input flow:
     * <ul>
     *     <li>Asks the user for a feature keyword.</li>
     *     <li>Shows SpellChecker suggestions if available.</li>
     *     <li>Allows the user to select up to 3 unique features.</li>
     * </ul>
     *
     * @return A list of selected feature names (max 3). May return an empty list if the user skips.
     */
    public List<String> showAndGetResult() {
        List<String> selectedFeatures = new ArrayList<>();

        while (true) {
            // Stop if user already selected the maximum of 3 features
            if (selectedFeatures.size() == 3) {
                ConsolePrinter.printInfo("You have reached the maximum of 3 features.");
                break;
            }

            // Ask user for a keyword
            String keyword = InputReader.readString(
                    "Do you have a specific feature in mind?\n(Press Enter to skip)"
            ).trim();

            // If the user presses Enter without typing anything
            if (keyword.isEmpty()) {
                if (selectedFeatures.isEmpty()) {
                    ConsolePrinter.printInfo("No features selected.");
                }
                break;
            }

            // Get corrected suggestions from the SpellChecker
            List<String> corrected = spellChecker.check(keyword, 4, 3);

            // If no suggestions were found
            if (corrected.isEmpty()) {
                ConsolePrinter.printInfo("No suggestions found for: " + keyword);
                boolean retry = InputReader.readYesNo("Do you want to try again?");
                if (!retry) break;
                continue;
            }

            // Show suggestions in a menu screen for selection
            AtomicReference<String> selected = new AtomicReference<>(corrected.get(0));
            MenuScreen screen = new MenuScreen("Did you mean?");
            for (String word : corrected) {
                screen.addOption(new MenuOption(word, () -> selected.set(word)));
            }
            screen.show();

            // Add the chosen word to the list if it's not a duplicate
            String chosen = selected.get();
            if (selectedFeatures.contains(chosen)) {
                ConsolePrinter.printInfo("Feature '" + chosen + "' is already selected. Please choose another one.");
            } else {
                selectedFeatures.add(chosen);
                ConsolePrinter.printSuccess("Feature added: " + chosen);
            }

            // Check again if we reached the maximum of 3 features
            if (selectedFeatures.size() == 3) {
                ConsolePrinter.printInfo("You have selected 3 features. Exiting...");
                break;
            }

            // Ask if the user wants to add another feature
            boolean addMore = InputReader.readYesNo("Do you want to add another feature?");
            if (!addMore) {
                break;
            }
        }

        // Show final summary before returning
        if (!selectedFeatures.isEmpty()) {
            ConsolePrinter.printInfo("Final selected features: " + selectedFeatures);
        }

        return selectedFeatures;
    }
}