package cli.screens;

import java.util.concurrent.atomic.AtomicReference;

import cli.ui.ConsolePrinter;
import cli.ui.MenuOption;
import cli.ui.MenuScreen;
import types.SubscriptionPlan;

public class SubscriptionPlanScreen {

    public static SubscriptionPlan show() {
        AtomicReference<SubscriptionPlan> selectedPlan = new AtomicReference<>(SubscriptionPlan.MONTHLY);

        MenuScreen screen = new MenuScreen("Choose a Subscription Plan");

        for (SubscriptionPlan plan : SubscriptionPlan.values()) {
            screen.addOption(new MenuOption(plan.getDescription(), () -> {
                selectedPlan.set(plan);
                ConsolePrinter.printSuccess("You selected: " + plan.getDescription());
            }));
        }

        screen.show();
        return selectedPlan.get();
    }
}
