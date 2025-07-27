package cli.screens;

import cli.ui.ConsolePrinter;
import cli.utils.InputReader;

public class ContinueSearchScreen {

    public static boolean show() {
        ConsolePrinter.printTitle("Do you want to perform another search?");

        ConsolePrinter.printListItem("1. Yes");
        ConsolePrinter.printListItem("2. No (Exit)");

        while (true) {
            String input = InputReader.readString("Select an option").trim();
            switch (input) {
                case "1":
                    return true;
                case "2":
                    return false;
                default:
                    ConsolePrinter.printError("Invalid input. Please enter 1 or 2.");
            }
        }
    }
}