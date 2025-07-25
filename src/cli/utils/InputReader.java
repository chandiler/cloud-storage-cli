package cli.utils;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Scanner;

import cli.ui.ConsolePrinter;
import feature.WordCompleter;
import feature.WordCompletionAdapter;

public class InputReader {
    private static final Scanner scanner = new Scanner(System.in);

    private static LineReader reader = null;

    public static void initAutoComplete(WordCompleter completer) {
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
         
            WordCompletionAdapter adapter = new WordCompletionAdapter(completer);
            reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(adapter)
                    .build();
        } catch (IOException e) {
            ConsolePrinter.printError("Error initializing autocomplete. Falling back to basic input.");
            reader = null;
        }
    }

    public static String readString(String prompt) {
        ConsolePrinter.printQuestion(prompt + ": ");
        if (reader != null) {
            return reader.readLine();
        } else {
            return scanner.nextLine();
        }
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
