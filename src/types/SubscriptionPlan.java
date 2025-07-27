package types;

public enum SubscriptionPlan {
    MONTHLY("Monthly"),
    ANNUAL("Annual");

    private final String description;

    SubscriptionPlan(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Adjust price dynamically if needed. 
     * (This might be used when validating against BudgetRange min/max values 
     * if you're storing raw monthly prices in the plans.)
     *
     * @param monthlyPrice the monthly price of the plan
     * @return adjusted price if ANNUAL (monthlyPrice * 12), or same value if MONTHLY
     */
    public double adjustPrice(double monthlyPrice) {
        return (this == ANNUAL) ? monthlyPrice * 12 : monthlyPrice;
    }
}
