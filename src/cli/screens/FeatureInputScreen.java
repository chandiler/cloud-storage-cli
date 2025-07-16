package cli.screens;

import cli.ui.BaseScreen;
import cli.ui.ConsolePrinter;
import cli.utils.InputReader;
import feature.SpellChecker;
import feature.WordCompleter;

import java.util.List;

public class FeatureInputScreen extends BaseScreen {

    public FeatureInputScreen() {
        super("Feature Keyword Input");
    }

    public List<String> showAndGetResult() {
        printBoxTitle();

        String keyword = InputReader.readString("Do you have a specific feature in mind?\n(Press Enter to skip)").trim();

        if (keyword.isEmpty()) {
            return null;
        }

        String corrected = new SpellChecker().check(keyword);
  
        ConsolePrinter.printQuestion("\nDid you mean: " + corrected + "?");

        List<String> suggestions = new WordCompleter().complete(corrected);

        if (suggestions.isEmpty()) {
        
            ConsolePrinter.printInfo("No related features found.");
            return null;
        }

        System.out.println("Related features:");
        for (String s : suggestions) {
            System.out.println(" - " + s);
        }

        String confirm = InputReader.readString("Use these features to filter plans? [y/n]").trim().toLowerCase();

        return confirm.equals("y") ? suggestions : null;
    }

    @Override
    public void show() {
        // no-op; usamos showAndGetResult()
    }
}
