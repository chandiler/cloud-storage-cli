package cli;

import feature.WebCrawler;
import feature.HtmlParser;
import feature.SpellChecker;
import feature.WordCompleter;
import feature.Recommender;
import filter.UserFilter;
import model.Plan;
import model.UserRequest;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String platform = askPlatform(scanner);

        System.out.println("\nCrawling data from " + platform + "...");
        String rawHtml = new WebCrawler().crawl(platform);
        List<Plan> plans = new HtmlParser().parse(rawHtml);
        System.out.println("Data loaded.\n");

        System.out.println("Do you have a specific feature in mind?");
        System.out.print("Enter keyword or press Enter to skip: ");
        String keyword = scanner.nextLine().trim();

        List<String> selectedFeatures = null;
        if (!keyword.isEmpty()) {
            String corrected = new SpellChecker().check(keyword);
            System.out.println("\nDid you mean: " + corrected + "?");
            List<String> suggestions = new WordCompleter().complete(corrected);
            System.out.println("Related features:");
            for (String s : suggestions) {
                System.out.println(" - " + s);
            }
            System.out.print("\nUse these features to filter plans? [y/n]: ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("y")) {
                selectedFeatures = suggestions;
            }
        }

        System.out.print("\nWould you like to further narrow results using budget & storage filters? [y/n]: ");
        String useFilter = scanner.nextLine().trim().toLowerCase();
        UserRequest request;

        if (useFilter.equals("y")) {
            request = new UserFilter().collect(scanner);
            request.setFeatureKeywords(selectedFeatures);
        } else {
            request = new UserRequest();
            request.setFeatureKeywords(selectedFeatures);
        }

        List<Plan> filteredPlans = new Recommender().recommend(plans, request);

        System.out.println("\nMatched Plans:");
        for (int i = 0; i < filteredPlans.size(); i++) {
            Plan p = filteredPlans.get(i);
            System.out.println((i + 1) + ". " + p.getPlanName() + " - " + p.getDisplayPrice() + " - " + p.getShortDescription());
        }

        if (filteredPlans.isEmpty()) {
            System.out.println("No matching plans found.");
            return;
        }

        System.out.println("\nSort by:");
        System.out.println("[1] Popularity (based on past searches)");
        System.out.println("[2] Feature match strength");
        System.out.print("→ ");
        String rankOption = scanner.nextLine().trim();

        List<Plan> ranked = new Recommender().rank(filteredPlans, rankOption);

        System.out.println("\nTop Recommendation:");
        Plan top = ranked.get(0);
        System.out.println("→ " + top.getPlanName());
        System.out.println("   " + top.getDisplayPrice() + " – " + top.getHighlight());

        System.out.print("\nWould you like to start over or exit?\n[1] Start Over\n[2] Exit\n→ ");
        String again = scanner.nextLine().trim();
        if (again.equals("1")) {
            main(null);
        } else {
            System.out.println("\nThank you for using Cloud Storage Recommender.");
        }
    }

    private static String askPlatform(Scanner scanner) {
        System.out.println("Do you have a preferred platform?");
        System.out.println("[1] Google Drive");
        System.out.println("[2] Dropbox");
        System.out.println("[3] OneDrive");
        System.out.println("[4] Box");
        System.out.println("[5] No preference");
        System.out.print("→ ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                return "Google Drive";
            case "2":
                return "Dropbox";
            case "3":
                return "OneDrive";
            case "4":
                return "Box";
            default:
                return "All";
        }
    }
}
