package cli.screens;

import model.Plan;

public class RecommendationScreen {

    /*
    public static void show(Plan plan) {
        System.out.println("\nTop Recommendation:");
        System.out.println("→ " + plan.getPlanName());
        System.out.println("   " + plan.getDisplayPrice() + " – " + plan.getHighlight());
    }
    */

    public static void show(Plan plan) {
        if (plan == null) {
            System.out.println("No plan to recommend.");
            return;
        }

        System.out.println("\nTop Recommendation:");
        System.out.println("→ Plan Name: " + (plan.getPlanName() != null ? plan.getPlanName() : "Unnamed"));
        System.out.println("→ Price: " + plan.getDisplayPrice());

        if (plan.getHighlight() != null && !plan.getHighlight().isEmpty()) {
            System.out.println("Highlight: " + plan.getHighlight());
        }

        if (plan.getStorage() != null) {
            System.out.println("Storage: " + plan.getStorage());
        }

        if (plan.getFeatures() != null && !plan.getFeatures().isEmpty()) {
            System.out.println("Features: " + String.join(", ", plan.getFeatures()));
        }

        if (plan.getPricingOptions() != null && !plan.getPricingOptions().isEmpty()) {
            System.out.println("→ Pricing Options:");
            for (int i = 0; i < plan.getPricingOptions().size(); i++) {
                var option = plan.getPricingOptions().get(i);
                System.out.printf("   %d. %s – %s%n", i + 1, option.getPlanType(), option.getPrice());
            }
        }
    }
}
