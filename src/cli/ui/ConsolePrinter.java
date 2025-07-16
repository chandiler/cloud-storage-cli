package cli.ui;

public class ConsolePrinter {

    public static void printTitle(String title) {
        int boxWidth = title.length() + 4;
        String top = AnsiColors.CYAN_BRIGHT + "┌" + "─".repeat(boxWidth) + "┐";
        String middle = "│  " + title + "  │";
        String bottom = "└" + "─".repeat(boxWidth) + "┘";
        System.out.println();
        System.out.println(top);
        System.out.println(middle);
        System.out.println(bottom + AnsiColors.RESET);
    }

    public static void printQuestion(String question) {
        System.out.println(AnsiColors.YELLOW_BRIGHT + question + AnsiColors.RESET);
    }

    public static void printListItem(String item) {
        System.out.println(AnsiColors.GREEN_BRIGHT + item + AnsiColors.RESET);
    }

    public static void printInfo(String message) {
        System.out.println(AnsiColors.CYAN_BRIGHT + message + AnsiColors.RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(AnsiColors.GREEN_BRIGHT + message + AnsiColors.RESET);
    }

    public static void printWarning(String message) {
        System.out.println(AnsiColors.YELLOW_BRIGHT + message + AnsiColors.RESET);
    }

    public static void printError(String message) {
        System.out.println(AnsiColors.RED_BRIGHT + message + AnsiColors.RESET);
    }

    public static void printPrompt(String prompt) {
        System.out.print(AnsiColors.WHITE_BRIGHT + AnsiColors.BLUE_BG + " " + prompt + ": " + AnsiColors.RESET);
    }
}
