package feature;

import model.Plan;
import model.UserRequest;
import filter.UserFilter;
import filter.ScorePlan;

import java.util.*;
import java.util.stream.Collectors;

public class Recommender {

    public List<Plan> recommend(List<Plan> plans, UserRequest request) {
        List<Plan> filteredPlans = UserFilter.filter(plans, request);

        List<String> keywords = request.getFeatureKeywords();
        if (keywords != null && !keywords.isEmpty()) {
            List<String> lowerKeywords = keywords.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            List<ScorePlan> scoredPlans = new ArrayList<>();

            for (Plan plan : filteredPlans) {
                int score = 0;

                String name = plan.getPlanName() == null ? "" : plan.getPlanName().toLowerCase();

                String featureText = "";
                if (plan.getFeatures() != null) {
                    featureText = String.join(" ", plan.getFeatures()).toLowerCase();
                }

                // Frequency Count Using HashMap + Stopword filter
                Map<String, Integer> wordFreq = new HashMap<>();

                for (String word : name.split("\\W+")) {
                    word = word.trim().toLowerCase();
                    if (!word.isEmpty() && !FeatureExtractor.isStopWord(word)) {
                        wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
                    }
                }

                if (plan.getFeatures() != null) {
                    for (String feature : plan.getFeatures()) {
                        for (String word : feature.toLowerCase().split("\\W+")) {
                            word = word.trim();
                            if (!word.isEmpty() && !FeatureExtractor.isStopWord(word)) {
                                wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
                            }
                        }
                    }
                }

                for (String keyword : lowerKeywords) {
                    score += wordFreq.getOrDefault(keyword, 0);
                }

                if (score > 0) {
                    scoredPlans.add(new ScorePlan(plan, score));
                }
            }

            return scoredPlans.stream()
                    .sorted(Comparator.comparingInt(ScorePlan::getScore).reversed())
                    .map(ScorePlan::getPlan)
                    .collect(Collectors.toList());
        }

        return filteredPlans;
    }

    public List<Plan> rank(List<Plan> filteredPlans, String rankOption) {
        if (rankOption == null) return filteredPlans;

        switch (rankOption.toLowerCase()) {
            case "price-asc":
                return filteredPlans.stream()
                        .sorted(Comparator.comparing(Plan::getDisplayPrice))
                        .collect(Collectors.toList());
            case "storage-desc":
                return filteredPlans.stream()
                        .sorted((a, b) -> Integer.compare(
                                extractStorageSize(b.getStorage()),
                                extractStorageSize(a.getStorage())
                        ))
                        .collect(Collectors.toList());
            default:
                return filteredPlans;
        }
    }

    private int extractStorageSize(String storageStr) {
        if (storageStr == null) return 0;
        try {
            String number = storageStr.replaceAll("[^0-9]", "");
            int value = Integer.parseInt(number);
            if (storageStr.toLowerCase().contains("tb")) {
                return value * 1024; // convert TB to GB
            }
            return value;
        } catch (Exception e) {
            return 0; // fallback
        }
    }
}