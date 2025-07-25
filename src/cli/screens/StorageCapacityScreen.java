package cli.screens;

import cli.ui.ConsolePrinter;
import cli.ui.MenuOption;
import cli.ui.MenuScreen;
import types.StorageRange;

import java.util.concurrent.atomic.AtomicReference;

public class StorageCapacityScreen {

    public static StorageRange show(StorageRange[] allowedRanges) {
        AtomicReference<StorageRange> selected = new AtomicReference<>(allowedRanges[0]);

        MenuScreen screen = new MenuScreen("Select desired storage capacity");

        for (StorageRange range : allowedRanges) {
            screen.addOption(new MenuOption(range.getDescription(), () -> {
                selected.set(range);
                ConsolePrinter.printSuccess("You selected: " + range.getDescription());
            }));
        }

        screen.show();
        return selected.get();
    }
}
