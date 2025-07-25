package types;

public enum BudgetRange {
    FREE("Free (0 CAD)", 0, 0),
    RANGE_1_5("1 – 5 CAD", 1, 5),
    RANGE_6_10("6 – 10 CAD", 6, 10),
    RANGE_11_20("11 – 20 CAD", 11, 20),
    RANGE_21_30("21 – 30 CAD", 21, 30),
    RANGE_31_50("31 – 50 CAD", 31, 50),
    RANGE_51_PLUS("51+ CAD", 51, Integer.MAX_VALUE);

    private final String label;
    private final int min;
    private final int max;

    BudgetRange(String label, int min, int max) {
        this.label = label;
        this.min = min;
        this.max = max;
    }

    public String getDescription() {
        return label;
    }
    
    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }


    public boolean isMonthlyOnly() {
        return this == RANGE_1_5 || this == RANGE_6_10 || this == FREE;
    }

    public static BudgetRange[] getBySubscriptionPlan(SubscriptionPlan plan) {
        return switch (plan) {
            case MONTHLY -> values(); 
            case ANNUAL -> new BudgetRange[]{
                    RANGE_6_10, RANGE_11_20, RANGE_21_30, RANGE_31_50, RANGE_51_PLUS
            };
        };
    }
}
