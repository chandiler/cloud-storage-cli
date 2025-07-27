package feature;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

/**
 * FeatureExtractor processes JSON data containing cloud storage platform information
 * to extract both individual keywords and complete feature descriptions.
 * 
 * Usage:
 * 1. Initialize with JSON file path: FeatureExtractor extractor = new FeatureExtractor("path/to/file.json");
 * 2. Call extractFeaturesFromJson() to populate the feature sets
 * 3. Access the extracted features through the public fields:
 *    - singleWordFeatures: Set of individual keywords from all features
 *    - completeFeatureStrings: Set of complete feature descriptions
 */
public class FeatureExtractor {
    /**
     * Set containing individual keywords extracted from all features
     */
    public final Set<String> singleWordFeatures = new HashSet<>();
    
    /**
     * Set containing complete feature descriptions as they appear in the JSON
     */
    public final Set<String> completeFeatureStrings = new HashSet<>();
    
    private final String jsonFilePath;

    /**
     * Constructs a FeatureExtractor with the path to the JSON file
     * @param jsonFilePath Path to the JSON file containing cloud storage data
     */
    public FeatureExtractor(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    /**
     * Extracts features from the JSON file and populates both feature sets:
     * - singleWordFeatures: individual keywords from feature descriptions
     * - completeFeatureStrings: full feature descriptions
     */
    public void extractFeaturesFromJson() {
        try (FileReader reader = new FileReader(jsonFilePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> providers = gson.fromJson(reader, listType);

            for (Map<String, Object> provider : providers) {
                processFileTypesSupported(provider);
                processUpgradePaths(provider);
                processNotes(provider);
                processPlatformCompatibility(provider);
                processPlans(provider);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processFileTypesSupported(Map<String, Object> provider) {
        addListStrings(provider.get("FileTypesSupported"), singleWordFeatures);
    }

    private void processUpgradePaths(Map<String, Object> provider) {
        addKeywordsFromText(provider.get("UpgradePaths"), singleWordFeatures);
    }

    private void processNotes(Map<String, Object> provider) {
        addKeywordsFromText(provider.get("Notes"), singleWordFeatures);
    }

    private void processPlatformCompatibility(Map<String, Object> provider) {
        Map<String, Object> platformCompat = (Map<String, Object>) provider.get("PlatformCompatibility");
        if (platformCompat != null) {
            addListStrings(platformCompat.get("Integrations"), singleWordFeatures);
        }
    }

    private void processPlans(Map<String, Object> provider) {
        List<Map<String, Object>> plans = (List<Map<String, Object>>) provider.get("Plans");
        if (plans != null) {
            for (Map<String, Object> plan : plans) {
                List<String> planFeatures = (List<String>) plan.get("Features");
                if (planFeatures != null) {
                    for (String feature : planFeatures) {
                        completeFeatureStrings.add(feature.trim());
                        processFeatureString(feature, singleWordFeatures);
                    }
                }
            }
        }
    }

    private void processFeatureString(String feature, Set<String> targetSet) {
        String[] tokens = feature.split("[^a-zA-Z0-9+]+");
        for (String token : tokens) {
            token = token.trim();
            if (token.length() > 1 && !isStopWord(token)) {
                targetSet.add(token);
            }
        }
    }

    private void addListStrings(Object obj, Set<String> targetSet) {
        if (obj instanceof List<?>) {
            for (Object item : (List<?>) obj) {
                if (item instanceof String s) {
                    targetSet.add(s.trim());
                }
            }
        }
    }

    private void addKeywordsFromText(Object obj, Set<String> targetSet) {
        if (obj instanceof String s) {
            String[] tokens = s.split("[^a-zA-Z0-9+]+");
            for (String token : tokens) {
                token = token.trim();
                if (token.length() > 2 && !isStopWord(token)) {
                    targetSet.add(token);
                }
            }
        }
    }

    private static final Set<String> STOPWORDS = Set.of(
        "and", "or", "the", "with", "for", "your", "our", "you", "per", "up", "to",
        "of", "at", "any", "all", "by", "on", "in", "from", "this", "that", "is",
        "a", "an", "it", "are", "be", "as", "if", "do", "not", "will", "then",
        "more", "some", "new", "gb", "tb", "ca", "mo", "month", "year", "yr",
        "user", "users", "monthly", "annual", "annually"
    );

    public static boolean isStopWord(String word) {
        return STOPWORDS.contains(word.toLowerCase());
    }
}