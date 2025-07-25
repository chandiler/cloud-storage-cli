package feature;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Box 页面解析器：提取计划名、价格、功能信息
 * Box plan extractor: extracts title, price, and features
 */
public class BoxPlanExtractor implements PlanExtractor {

	// 提取计划标题
	@Override
	public String extractTitle(WebElement block) {
		try {
			return block.findElement(By.cssSelector("div.pricing-package--heading h3")).getText().trim();
		} catch (Exception e) {
			return "Unknown Plan";
		}
	}

	// 提取价格（仅支持年付，planType 强制为 "Annual"）
	@Override
	public String extractPrice(WebElement block, String planType) {
		try {
			String raw = block.findElement(By.cssSelector("div.pricing-package--price .annual-price")).getText().trim();
			String[] tokens = raw.split("\\$");
			if (tokens.length > 1) {
				String amountPart = tokens[1].split(" ")[0];
				double annualMonthly = Double.parseDouble(amountPart);

				if (planType.equalsIgnoreCase("Annual")) {
					int annual = (int) Math.round(annualMonthly * 12);
					return String.valueOf(annual);
				} else if (planType.equalsIgnoreCase("Monthly")) {
					int monthly = (int) Math.round(annualMonthly / 0.75);
					return String.valueOf(monthly);
				}
			}
		} catch (Exception ignored) {
		}
		return "0";
	}

	// 提取功能列表
	@Override
	public List<String> extractFeatures(WebElement block) {
		List<String> features = new ArrayList<>();
		try {
			List<WebElement> items = block.findElements(By.cssSelector("div.pricing-featured_features__chart p"));
			for (WebElement item : items) {
				String text = item.getText().trim();
				if (!text.isEmpty())
					features.add(text);
			}
		} catch (Exception ignored) {
		}
		return features;
	}
}
