package model;

import com.google.gson.annotations.SerializedName;

public class PricingOption {

    @SerializedName("PlanType")
    private String planType;

    @SerializedName("Price")
    private String price;

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}