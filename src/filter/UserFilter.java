package filter;

import model.UserRequest;
import model.Plan;
import model.PricingOption;
import types.BudgetRange;
import types.CloudStoragePlatform;
import types.StorageRange;
import types.SubscriptionPlan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserFilter {

	public static List<Plan> filter(List<Plan> plans, UserRequest request) {
	    List<ScorePlan> scored = new ArrayList<>();

	    BudgetRange budgetRange = request.getBudgetRange();
	    StorageRange storageRange = request.getStorageRange();
	    SubscriptionPlan subscriptionPlan = request.getSubscriptionPlan();
	    CloudStoragePlatform selectedPlatform = request.getPlatform();
	    List<String> userFeatures = request.getFeatureKeywords();

	    for (Plan plan : plans) {
	        // Platform filter
	        if (selectedPlatform != null) {
	            if (plan.getPlatform() == null || !plan.getPlatform().equalsIgnoreCase(selectedPlatform.name())) {
	                continue;
	            }
	        }

	        boolean priceOk = false;
	        boolean planTypeOk = (subscriptionPlan == null);
	        boolean storageOk = true;

	        // Storage check
	        if (storageRange != null && plan.getStorage() != null) {
	            int planStorage = parseStorageToGB(plan.getStorage());
	            int minStorage = getStorageMin(storageRange);
	            int maxStorage = storageRange.ordinal() == StorageRange.values().length - 1
	                    ? Integer.MAX_VALUE
	                    : storageRange.getMaxGB();

	            if (planStorage < minStorage || planStorage > maxStorage) {
	                storageOk = false;
	            }
	        }

	        // Price and plan type check
	        List<PricingOption> pricingOptions = plan.getPricingOptions();
	        if (pricingOptions != null) {
	            for (PricingOption option : pricingOptions) {
	                String planTypeStr = option.getPlanType() != null ? option.getPlanType().toLowerCase() : "";
	                String expectedPlanType = subscriptionPlan != null ? subscriptionPlan.name().toLowerCase() : "";

	                if (subscriptionPlan == null || planTypeStr.contains(expectedPlanType)) {
	                    planTypeOk = true;

	                    String rawPrice = option.getPrice();
	                    if (rawPrice == null) continue;

	                    String priceStr = rawPrice.trim();
	                    if (priceStr.equalsIgnoreCase("FREE")) {
	                        priceOk = (budgetRange == BudgetRange.FREE);
	                    } else {
	                        try {
	                            Matcher matcher = Pattern.compile("([\\d\\.]+)").matcher(priceStr);
	                            if (matcher.find()) {
	                                double priceValue = Double.parseDouble(matcher.group(1));
	                                if (budgetRange == null ||
	                                        (priceValue >= budgetRange.getMin() && priceValue <= budgetRange.getMax())) {
	                                    priceOk = true;
	                                }
	                            }
	                        } catch (Exception e) {
	                            priceOk = false;
	                        }
	                    }
	                }
	            }
	        }

	        if (priceOk && planTypeOk && storageOk) {
	            int matchScore = 0;
	            if (userFeatures != null && !userFeatures.isEmpty() && plan.getFeatures() != null) {
	                for (String keyword : userFeatures) {
	                    for (String feature : plan.getFeatures()) {
	                        if (feature.toLowerCase().contains(keyword.toLowerCase())) {
	                            matchScore++;
	                        }
	                    }
	                }
	            }
	            scored.add(new ScorePlan(plan, matchScore));
	        }
	        System.out.printf(
                    "DEBUG PLAN: %-25s | Platform: %-10s | Storage: %-10s | SubscriptionPlanOK: %-5s | PriceOK: %-5s | StorageOK: %-5s\n",
                    plan.getPlanName(),
                    plan.getPlatform(),
                    plan.getStorage(),
                    planTypeOk,
                    priceOk,
                    storageOk
                );
	    }

	    Collections.sort(scored, Comparator.comparingInt(ScorePlan::getScore).reversed());

	    List<Plan> results = new ArrayList<>();
	    for (ScorePlan sp : scored) {
	        results.add(sp.getPlan());
	    }

	    return results;
	}
	
	private static int getStorageMin(StorageRange range) {
	    if (range == StorageRange.RANGE_0_5) return 0;

	    StorageRange[] all = StorageRange.values();
	    for (int i = 1; i < all.length; i++) {
	        if (all[i] == range) {
	            return all[i - 1].getMaxGB() + 1;
	        }
	    }

	    return 0; // fallback
	}

    public static UserRequest collect() {
        return new UserRequest();
    }

    private static int parseStorageToGB(String storageStr) {
        if (storageStr == null || storageStr.isEmpty()) return -1;

        try {
            String lower = storageStr.toLowerCase().trim();
            if (lower.contains("unlimited")) return Integer.MAX_VALUE;
            if (lower.contains("tb")) {
                return (int) (Double.parseDouble(lower.replaceAll("[^\\d.]", "")) * 1024);
            } else if (lower.contains("gb")) {
                return (int) Double.parseDouble(lower.replaceAll("[^\\d.]", ""));
            } else {
                return Integer.parseInt(lower.replaceAll("[^\\d.]", ""));
            }
        } catch (Exception e) {
            return -1;
        }
    }
}
