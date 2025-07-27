package model;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class Plan {
    @SerializedName("PlanName")
    private String planName;

    @SerializedName("Storage")
    private String storage;

    @SerializedName("Features")
    private List<String> features;

    @SerializedName("PricingOptions")
    private List<PricingOption> pricingOptions;

    @SerializedName("Highlight")
    private String highlight;

    @SerializedName("Platform")
    private String platform;            

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

    public List<PricingOption> getPricingOptions() {
        return pricingOptions;
    }

    public void setPricingOptions(List<PricingOption> pricingOptions) {
        this.pricingOptions = pricingOptions;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }
    
    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDisplayPrice() {
        if (pricingOptions == null || pricingOptions.isEmpty()) return "N/A";
        List<String> prices = new ArrayList<>();
        for (PricingOption p : pricingOptions) {
            prices.add(p.getPrice());
        }
        return String.join(" | ", prices);
    }

    public String getShortDescription() {
        return storage + " - " + (features != null && !features.isEmpty() ? features.get(0) : "No detail");
    }