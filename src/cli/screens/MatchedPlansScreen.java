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
            String item = String.format("%d. %s - %s - %s",
                (i + 1),
                p.getPlanName(),
                p.getDisplayPrice(),
                p.getShortDescription()
            );
            ConsolePrinter.printListItem(item);
        }

        ConsolePrinter.printQuestion("\nSort by:");
        ConsolePrinter.printListItem("[1] Price (ascending)");
        ConsolePrinter.printListItem("[2] Storage (descending)");
        ConsolePrinter.printListItem("[3] Feature frequency (descending)");
        ConsolePrinter.printListItem("[4] No sorting (keep recommended order)");

        String rankOption = null;
        while (true) {
            String input = InputReader.readString("→").trim();
            switch (input) {
                case "1":
                    rankOption = "price-asc";
                    break;
                case "2":
                    rankOption = "storage-desc";
                    break;
                case "3":
                    rankOption = "feature-desc";
                    break;
                case "4":
                    rankOption = null;
                    break;
                default:
                    ConsolePrinter.printError("Invalid option. Please enter 1, 2, 3, or 4.");
                    continue;
            }
            break;
        }

        return new Recommender().rank(filteredPlans, rankOption);
    }

    @Override
    public void show() {
        // No-op
    }
}
