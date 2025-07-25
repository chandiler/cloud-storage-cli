package feature;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * OneDrive 页面解析器：提取计划名、价格、功能信息
 * OneDrive plan extractor: extracts title, price, and features
 */
public class OneDrivePlanExtractor implements PlanExtractor {

	// 提取计划标题
	// Extract plan title
	@Override
	public String extractTitle(WebElement block) {
		try {
			return block.findElement(By.cssSelector("div.oc-product-title .h3")).getText().trim();
		} catch (Exception e) {
			return "Unknown Plan";
		}
	}

	// 提取价格（包含年付）
	// Extracts both yearly pricing if available
	@Override
	public String extractPrice(WebElement block, String planType) {
		try {
			// 年付价格
			// return
			// block.findElement(By.cssSelector("span.oc-list-price.font-weight-semibold.text-primary")).getText().trim();
			List<WebElement> prices = block
					.findElements(By.cssSelector("span.oc-list-price.font-weight-semibold.text-primary"));
			for (WebElement el : prices) {
				if (el.isDisplayed()) {
					String raw = el.getText().trim(); // e.g. "$11.99/mo"
					String[] tokens = raw.split("\\$");
					if (tokens.length > 1) {
						String amountPart = tokens[1].split(" ")[0]; // e.g. "11.99"
						double value = Double.parseDouble(amountPart);
						// int rounded = (int) Math.round(value);
						return String.valueOf(value);
					}
				}
			}
		} catch (Exception e) {
			return "Price not found";
		}
		return "0";
	}

	public String extractMonthlyPrice(WebElement block) {
		try {
			List<WebElement> prices = block.findElements(By.cssSelector("span.oc-token.oc-list-price"));
			for (WebElement el : prices) {
				if (el.isDisplayed()) {
					String raw = el.getText().trim(); // e.g. "$11.99/mo"
					String[] tokens = raw.split("\\$");
					if (tokens.length > 1) {
						String amountPart = tokens[1].split(" ")[0]; // e.g. "11.99"
						double value = Double.parseDouble(amountPart);
						// int rounded = (int) Math.round(value);
						return String.valueOf(value);
					}
				}
			}
		} catch (Exception ignored) {
		}

		// 若无月付信息，则返回free
		// If not found, return free
		return "0";
	}

	// 提取功能列表
	// Extract features list
	@Override
	public List<String> extractFeatures(WebElement block) {
		List<String> features = new ArrayList<>();
		try {
			List<WebElement> featureElements = block
					.findElements(By.cssSelector("ul.list-unstyled.mb-n4 li div.card-body p span"));
			for (WebElement element : featureElements) {
				String text = element.getText().trim();
				if (!text.isEmpty()) {
					features.add(text);
				}
			}
		} catch (Exception ignored) {
		}
		return features;
	}

}
