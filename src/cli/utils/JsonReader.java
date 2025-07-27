package cli.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.PlatformWrapper;
import model.Plan;
import types.CloudStoragePlatform;

import java.io.FileReader;
import java.util.*;

public class JsonReader {

    public static Map<CloudStoragePlatform, List<Plan>> loadGroupedByPlatform(String path) {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(path);

            List<PlatformWrapper> platforms = gson.fromJson(
                reader,
                new TypeToken<List<PlatformWrapper>>() {}.getType()
            );

            Map<CloudStoragePlatform, List<Plan>> groupedPlans = new HashMap<>();

            for (PlatformWrapper platformWrapper : platforms) {
            	System.out.println("DEBUG: Raw platform from JSON => " + platformWrapper.getPlatform());
            	
                //String platformName = platformWrapper.getPlatform().toUpperCase().replaceAll("\\s+", "_");
                String platformName = platformWrapper.getPlatform().trim();
                try {
                    //CloudStoragePlatform platformEnum = CloudStoragePlatform.valueOf(platformName);
                	CloudStoragePlatform platformEnum = Arrays.stream(CloudStoragePlatform.values())
                		    .filter(p -> p.getDescription().equalsIgnoreCase(platformName))
                		    .findFirst()
                		    .orElseThrow(() -> new IllegalArgumentException("Unknown platform: " + platformName));

                    List<Plan> plans = platformWrapper.getPlans();
                    if (plans != null && !plans.isEmpty()) {
                        // Gán platform cho từng plan trước khi đưa vào map
                        for (Plan plan : plans) {
                            plan.setPlatform(platformEnum.name());
                        }
                        groupedPlans.put(platformEnum, plans);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Unknown platform: " + platformName);
                }
            }

            return groupedPlans;

        } catch (Exception e) {
            System.err.println("Error loading JSON: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    public static List<Plan> load(String path) {
        Map<CloudStoragePlatform, List<Plan>> grouped = loadGroupedByPlatform(path);
        List<Plan> allPlans = new ArrayList<>();
        for (List<Plan> plans : grouped.values()) {
            allPlans.addAll(plans);
        }
        return allPlans;
    }
}