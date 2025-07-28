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
    public static StorageRange[] getByBudget(BudgetRange budget) {
      
        return values();
    }
}
