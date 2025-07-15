package model;

import java.util.List;

public class UserRequest {
    private Double maxBudget;          
    private String billingType;         
    private String minStorage;          
    private List<String> featureKeywords;

    public Double getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(Double maxBudget) {
        this.maxBudget = maxBudget;
    }

    public String getBillingType() {
        return billingType;
    }

    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }

    public String getMinStorage() {
        return minStorage;
    }

    public void setMinStorage(String minStorage) {
        this.minStorage = minStorage;
    }

    public List<String> getFeatureKeywords() {
        return featureKeywords;
    }

    public void setFeatureKeywords(List<String> featureKeywords) {
        this.featureKeywords = featureKeywords;
    }
}
