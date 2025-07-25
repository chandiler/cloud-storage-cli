package feature;

import org.openqa.selenium.WebElement;
import java.util.List;

/**
 * 页面解析接口：定义如何从 block 元素中提取标题、价格、功能
 * Interface for parsing a plan block into structured fields
 */
public interface PlanExtractor {
    String extractTitle(WebElement block);
    String extractPrice(WebElement block, String planType);
    List<String> extractFeatures(WebElement block);
}
