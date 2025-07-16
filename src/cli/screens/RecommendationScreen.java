package cli.screens;

import model.Plan;

public class RecommendationScreen {

    public static void show(Plan plan) {
        System.out.println("\nTop Recommendation:");
        System.out.println("→ " + plan.getPlanName());
        System.out.println("   " + plan.getDisplayPrice() + " – " + plan.getHighlight());
    }
}
