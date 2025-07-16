package cli.screens;

import java.util.concurrent.atomic.AtomicReference;

import cli.ui.MenuOption;
import cli.ui.MenuScreen;

public class PlatformSelectionScreen {

    public static String show() {
        AtomicReference<String> selectedPlatform = new AtomicReference<>("All");

        MenuScreen screen = new MenuScreen("Select Preferred Platform");

        screen.addOption(new MenuOption("Google Drive", () -> selectedPlatform.set("Google Drive")));
        screen.addOption(new MenuOption("Dropbox", () -> selectedPlatform.set("Dropbox")));
        screen.addOption(new MenuOption("OneDrive", () -> selectedPlatform.set("OneDrive")));
        screen.addOption(new MenuOption("Box", () -> selectedPlatform.set("Box")));
        screen.addOption(new MenuOption("No preference", () -> selectedPlatform.set("All")));

        screen.show();
        return selectedPlatform.get();
    }
}
