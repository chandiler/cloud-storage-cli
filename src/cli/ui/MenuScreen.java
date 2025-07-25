package cli.ui;

import cli.utils.InputReader;

import java.util.ArrayList;
import java.util.List;

public class MenuScreen extends BaseScreen {

    private final List<MenuOption> options = new ArrayList<>();

    public MenuScreen(String title) {
        super(title);
    }

    public void addOption(MenuOption option) {
        options.add(option);
    }

    @Override
    public void show() {
        printBoxTitle();

        for (int i = 0; i < options.size(); i++) {
        	String numberColor = AnsiColors.YELLOW_BRIGHT;
        	String textColor = AnsiColors.GREEN;

        	String label = numberColor + (i + 1) + ". " + textColor + options.get(i).getLabel() + AnsiColors.RESET;
        	ConsolePrinter.printListItem(label);

        }

        while (true) {
            String input = InputReader.readString("Select an option");
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= options.size()) {
                    options.get(choice - 1).execute();
                    break; // exit the loop after valid selection
                } else {
                    ConsolePrinter.printError("Invalid option. Please select a valid number from the list.");
                }
            } catch (NumberFormatException e) {
                ConsolePrinter.printError("Invalid input. Please enter a number.");
            }
        }
    }
}
