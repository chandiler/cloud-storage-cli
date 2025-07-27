package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlatformWrapper {

    @SerializedName("Platform")
    private String platform;

    @SerializedName("Plans")
    private List<Plan> plans;

    public String getPlatform() {
        return platform;
    }

    public List<Plan> getPlans() {
        return plans;
    }
}