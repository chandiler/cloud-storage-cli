package cli.screens;

import cli.ui.ConsolePrinter;
import cli.ui.MenuOption;
import cli.ui.MenuScreen;
import types.BudgetRange;

import java.util.concurrent.atomic.AtomicReference;

public class BudgetSelectionScreen {

    public static BudgetRange show(BudgetRange[] allowedRanges) {
        AtomicReference<BudgetRange> selected = new AtomicReference<>(allowedRanges[0]);

        MenuScreen screen = new MenuScreen("Select your monthly budget");

        for (BudgetRange range : allowedRanges) {
            screen.addOption(new MenuOption(range.getDescription(), () -> {
                selected.set(range);
                ConsolePrinter.printSuccess("You selected: " + range.getDescription());
            }));
        }

        screen.show();
        return selected.get();
    }
}
