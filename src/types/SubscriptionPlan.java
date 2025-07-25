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

}