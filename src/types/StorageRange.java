package types;

public enum StorageRange {
    RANGE_0_5("0 – 5 GB", 0),
    RANGE_6_100("6 – 100 GB", 100),
    RANGE_101_999("101 – 999 GB", 999),
    RANGE_1_2_TB("1 – 2 TB", 2048),
    RANGE_3_5_TB("3 – 5 TB", 5120),
    RANGE_6_10_TB("6 – 10 TB", 10240),
    UNLIMITED("Unlimited / 10+ TB", Integer.MAX_VALUE);

    private final String label;
    private final int maxGB;

    StorageRange(String label, int maxGB) {
        this.label = label;
        this.maxGB = maxGB;
    }

    public String getDescription() {
        return label;
    }

    public static StorageRange[] getByBudget(BudgetRange budget) {
        return switch (budget) {
            case FREE -> new StorageRange[]{RANGE_0_5};
            case RANGE_1_5, RANGE_6_10 -> new StorageRange[]{RANGE_0_5, RANGE_6_100};
            case RANGE_11_20 -> new StorageRange[]{RANGE_6_100, RANGE_101_999, RANGE_1_2_TB};
            case RANGE_21_30 -> new StorageRange[]{RANGE_1_2_TB, RANGE_3_5_TB};
            case RANGE_31_50 -> new StorageRange[]{RANGE_1_2_TB, RANGE_3_5_TB, RANGE_6_10_TB};
            case RANGE_51_PLUS -> values(); // Todos
        };
    }
}
