package cli.screens;

import cli.ui.BaseScreen;
import cli.ui.ConsolePrinter;
import cli.utils.InputReader;
import feature.Recommender;
import model.Plan;

import java.util.List;

public class MatchedPlansScreen extends BaseScreen {

    public MatchedPlansScreen() {
        super("Matched Plans");
    }

    public List<Plan> showAndGetResult(List<Plan> filteredPlans) {
        printBoxTitle();

        if (filteredPlans.isEmpty()) {
            ConsolePrinter.printWarning("No matching plans found.");
            return List.of();
        }

        ConsolePrinter.printQuestion("Available Plans:");
        for (int i = 0; i < filteredPlans.size(); i++) {
            Plan p = filteredPlans.get(i);
            String item = String.format("%d. %s - %s - %s", (i + 1), p.getPlanName(), p.getDisplayPrice(), p.getShortDescription());
            ConsolePrinter.printListItem(item);
        }

        ConsolePrinter.printQuestion("\nSort by:");
        ConsolePrinter.printListItem("[1] Popularity (based on past searches)");
        ConsolePrinter.printListItem("[2] Feature match strength");

        String rankOption;
        while (true) {
            rankOption = InputReader.readString("→").trim();
            if (rankOption.equals("1") || rankOption.equals("2")) {
                break;
            }
            ConsolePrinter.printError("Invalid option. Please enter 1 or 2.");
        }

        return new Recommender().rank(filteredPlans, rankOption);
    }

    @Override
    public void show() {
        // No-op
    }
}
