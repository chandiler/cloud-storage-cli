package model;

import java.util.List;

public class Plan {
    private String planName;
    private String storage;                
    private List<String> features;         
    private List<String> pricingOptions;   
    private String highlight;            

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public List<String> getPricingOptions() {
        return pricingOptions;
    }

    public void setPricingOptions(List<String> pricingOptions) {
        this.pricingOptions = pricingOptions;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public String getDisplayPrice() {
        if (pricingOptions == null || pricingOptions.isEmpty()) return "N/A";
        return String.join(" | ", pricingOptions);
    }

    public String getShortDescription() {
        return storage + " - " + (features != null && !features.isEmpty() ? features.get(0) : "No detail");
    }
}
