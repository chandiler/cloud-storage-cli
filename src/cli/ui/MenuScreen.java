package cli.ui;

import java.util.*;

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
            String label = String.format("%d. %s", (i + 1), options.get(i).getLabel());
            ConsolePrinter.printListItem(label);
        }

        ConsolePrinter.printPrompt("Select an option");
        Scanner scanner = new Scanner(System.in);
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice >= 1 && choice <= options.size()) {
                options.get(choice - 1).execute();
            } else {
                ConsolePrinter.printError("Invalid option.");
            }
        } catch (NumberFormatException e) {
            ConsolePrinter.printError("Invalid input, please enter a number.");
        }
    }
}
