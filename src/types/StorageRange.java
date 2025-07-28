package types;

public enum StorageRange {
    RANGE_0_5("0 – 5 GB", 5),
    RANGE_6_100("6 – 100 GB", 100),
    RANGE_101_999("101 – 999 GB", 999),
    RANGE_1_2_TB("1 – 2 TB", 2_048),
    RANGE_3_5_TB("3 – 5 TB", 5_120),
    RANGE_6_10_TB("6 – 10 TB", 10_240),
    UNLIMITED("Unlimited / 10+ TB", Integer.MAX_VALUE);

    private final String label;
    private final int maxGB;

    StorageRange(String label, int maxGB) {
        this.label = label;
        this.maxGB = maxGB;
    }

    public int getMaxGB() {
        return maxGB;
    }

    public String getDescription() {
        return label;
    }

    /**
     * Find the matching StorageRange for a given storage value in GB
     */
    public static StorageRange getBySizeInGB(int sizeInGB) {
        for (StorageRange range : values()) {
            if (sizeInGB <= range.getMaxGB()) {
                return range;
            }
        }
        return UNLIMITED;
    }

    /**
     * Optional: Filter StorageRanges based on BudgetRange if needed.
     */
    /*public static StorageRange[] getByBudget(BudgetRange budget) {
      
        return values();
    }*/
    
    public static StorageRange[] getByBudget(BudgetRange budget) {
        return switch (budget) {
            // MONTHLY cases
            case FREE_MONTHLY, FREE_ANNUAL -> new StorageRange[]{
                    RANGE_0_5
            };

            case RANGE_1_5_MONTHLY, RANGE_1_60_ANNUAL,
                 RANGE_6_10_MONTHLY, RANGE_61_120_ANNUAL -> new StorageRange[]{
                    RANGE_0_5, RANGE_6_100
            };

            case RANGE_11_20_MONTHLY, RANGE_121_180_ANNUAL -> new StorageRange[]{
                    RANGE_6_100, RANGE_101_999, RANGE_1_2_TB
            };

            case RANGE_21_30_MONTHLY, RANGE_181_300_ANNUAL -> new StorageRange[]{
                    RANGE_1_2_TB, RANGE_3_5_TB
            };

            case RANGE_31_50_MONTHLY, RANGE_301_500_ANNUAL -> new StorageRange[]{
                    RANGE_1_2_TB, RANGE_3_5_TB, RANGE_6_10_TB
            };

            case RANGE_51_PLUS_MONTHLY, RANGE_501_PLUS_ANNUAL -> new StorageRange[]{
                    RANGE_1_2_TB, RANGE_3_5_TB, RANGE_6_10_TB, UNLIMITED
            };
        };
    }
}