package model;

import java.util.List;

import types.BudgetRange;
import types.StorageRange;
import types.SubscriptionPlan;
import types.CloudStoragePlatform;

public class UserRequest {
    private Double maxBudget;          
    private String billingType;         
    private String minStorage;
    private List<String> featureKeywords;
    private SubscriptionPlan subscriptionPlan;
    private BudgetRange budgetRange;
    private StorageRange storageRange;
    
    private CloudStoragePlatform platform; 


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
    
    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public BudgetRange getBudgetRange() {
        return budgetRange;
    }

    public void setBudgetRange(BudgetRange budgetRange) {
        this.budgetRange = budgetRange;
    }

    public StorageRange getStorageRange() {
        return storageRange;
    }

    public void setStorageRange(StorageRange storageRange) {
        this.storageRange = storageRange;
    }

    public CloudStoragePlatform getPlatform() {
        return platform;
    }

    public void setPlatform(CloudStoragePlatform platform) {
        this.platform = platform;
    }
}
