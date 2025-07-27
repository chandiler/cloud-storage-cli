package cli.screens;

import cli.ui.ConsolePrinter;
import cli.ui.MenuOption;
import cli.ui.MenuScreen;
import cli.utils.InputReader;
import feature.SpellChecker;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FeatureInputScreen {


    private final SpellChecker spellChecker;

    public FeatureInputScreen(SpellChecker spellChecker) {
        this.spellChecker = spellChecker;
    }

    public String showAndGetResult() {
        // Preguntar por la palabra clave
        String keyword = InputReader.readString("Do you have a specific feature in mind?\n(Press Enter to skip)");

        // Si el usuario no escribe nada
        if (keyword.trim().isEmpty()) {
            return null;
        }

        // Obtener correcciones del SpellChecker
        List<String> corrected = spellChecker.check(keyword, 3, 5);

        if (corrected.isEmpty()) {
            ConsolePrinter.printInfo("No suggestions found for: " + keyword);
            return null;
        }

        // Mostrar sugerencias en una pantalla tipo menú
        AtomicReference<String> selected = new AtomicReference<>(corrected.get(0));
        MenuScreen screen = new MenuScreen("Did you mean?");
        for (String word : corrected) {
            screen.addOption(new MenuOption(word, () -> selected.set(word)));
        }

        screen.show();
        return selected.get();
    }

    /* For testing without wordcompeter and spellchecking features
    public List<String> showAndGetResult() {
	        printBoxTitle();
	
	        while (true) {
	            String keyword = InputReader.readString("Do you have a specific feature in mind?\n(Press Enter to skip)")
	                    .trim();
	
	            if (keyword.isEmpty()) {
	                return null; // skip input
	            }
	
	            String corrected = spellChecker.check(keyword);
	
	            if (!corrected.equalsIgnoreCase(keyword)) {
	                boolean confirmed = InputReader.readYesNo("Did you mean: " + corrected + "?");
	                if (!confirmed) {
	                    ConsolePrinter.printInfo("Let's try again.");
	                    continue;
	                }
	            }
	
	            return List.of(corrected);
	        }
	 }
     */
}