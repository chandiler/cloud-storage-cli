package cli.utils;

import java.util.Scanner;

import cli.ui.ConsolePrinter;

public class InputReader {
    private static final Scanner scanner = new Scanner(System.in);

    public static String readString(String prompt) {

        ConsolePrinter.printQuestion(prompt + ": ");
        return scanner.nextLine();
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            try {
                ConsolePrinter.printPrompt(prompt + ": ");
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid decimal.");
            }
        }
    }

    public static boolean readYesNo(String prompt) {
        while (true) {
            ConsolePrinter.printQuestion(prompt + " [y/n]: ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) return true;
            if (input.equals("n")) return false;
            ConsolePrinter.printError("Invalid input. Please enter 'y' or 'n'.");
        }
    }
}
