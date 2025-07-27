package cli.screens;

import cli.ui.ConsolePrinter;
import cli.ui.MenuOption;
import cli.ui.MenuScreen;
import types.BudgetRange;
import types.SubscriptionPlan;

import java.util.concurrent.atomic.AtomicReference;

public class BudgetSelectionScreen {

    public static BudgetRange show(SubscriptionPlan subscriptionPlan,BudgetRange[] allowedRanges) {
        AtomicReference<BudgetRange> selected = new AtomicReference<>(allowedRanges[0]);

        String title = "Select your monthly budget";
        if(subscriptionPlan == SubscriptionPlan.ANNUAL) {
        	title = "Select your annual budget";
        }
        MenuScreen screen = new MenuScreen(title);

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
