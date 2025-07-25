package cli.screens;

import java.util.concurrent.atomic.AtomicReference;
import cli.ui.MenuOption;
import cli.ui.MenuScreen;
import types.CloudStoragePlatform;

public class PlatformSelectionScreen {

    public static CloudStoragePlatform show() {
        AtomicReference<CloudStoragePlatform> selectedPlatform = new AtomicReference<>(CloudStoragePlatform.ALL);

        MenuScreen screen = new MenuScreen("Select Preferred Platform");

        for (CloudStoragePlatform service : CloudStoragePlatform.values()) {
            screen.addOption(new MenuOption(
                service.getDescription(), 
                () -> selectedPlatform.set(service)
            ));
        }

        screen.show();
        return selectedPlatform.get();
    }
}
