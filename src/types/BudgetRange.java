package types;

public enum BudgetRange {
    // MONTHLY ranges
    FREE_MONTHLY("Free (0 CAD/mo)", 0, 0, SubscriptionPlan.MONTHLY),
    RANGE_1_5_MONTHLY("1 – 5 CAD/mo", 1, 5, SubscriptionPlan.MONTHLY),
    RANGE_6_10_MONTHLY("6 – 10 CAD/mo", 6, 10, SubscriptionPlan.MONTHLY),
    RANGE_11_20_MONTHLY("11 – 20 CAD/mo", 11, 20, SubscriptionPlan.MONTHLY),
    RANGE_21_30_MONTHLY("21 – 30 CAD/mo", 21, 30, SubscriptionPlan.MONTHLY),
    RANGE_31_50_MONTHLY("31 – 50 CAD/mo", 31, 50, SubscriptionPlan.MONTHLY),
    RANGE_51_PLUS_MONTHLY("51+ CAD/mo", 51, Integer.MAX_VALUE, SubscriptionPlan.MONTHLY),

    // ANNUAL ranges (usando total anual)
    FREE_ANNUAL("Free (0 CAD/year)", 0, 0, SubscriptionPlan.ANNUAL),
    RANGE_1_60_ANNUAL("1 – 60 CAD/year", 1, 60, SubscriptionPlan.ANNUAL),
    RANGE_61_120_ANNUAL("61 – 120 CAD/year", 61, 120, SubscriptionPlan.ANNUAL),
    RANGE_121_180_ANNUAL("121 – 180 CAD/year", 121, 180, SubscriptionPlan.ANNUAL),
    RANGE_181_300_ANNUAL("181 – 300 CAD/year", 181, 300, SubscriptionPlan.ANNUAL),
    RANGE_301_500_ANNUAL("301 – 500 CAD/year", 301, 500, SubscriptionPlan.ANNUAL),
    RANGE_501_PLUS_ANNUAL("501+ CAD/year", 501, Integer.MAX_VALUE, SubscriptionPlan.ANNUAL);

    private final String label;
    private final int min;
    private final int max;
    private final SubscriptionPlan subscriptionPlan;

    BudgetRange(String label, int min, int max, SubscriptionPlan subscriptionPlan) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.subscriptionPlan = subscriptionPlan;
    }

    public String getDescription() {
        return label;
    }
    public int getMin() { return min; }
    public int getMax() { return max; }
    public SubscriptionPlan getSubscriptionPlan() { return subscriptionPlan; }

    // Filtrar rangos por plan de suscripción
    public static BudgetRange[] getBySubscriptionPlan(SubscriptionPlan plan) {
        return java.util.Arrays.stream(values())
                .filter(range -> range.subscriptionPlan == plan)
                .toArray(BudgetRange[]::new);
    }
}
