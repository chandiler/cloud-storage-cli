package feature;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracts plan data from Dropbox plan cards
 */
public class DropboxPlanExtractor implements PlanExtractor {

	@Override
	public String extractTitle(WebElement block) {
		try {
			List<WebElement> titles = block.findElements(By.cssSelector("h2[data-testid='plan_name_test_id']"));
			for (WebElement el : titles) {
				if (el.isDisplayed())
					return el.getText().trim();
			}
		} catch (Exception ignored) {
		}
		return "N/A";
	}

	@Override
	public String extractPrice(WebElement block, String planType) {
		try {
			List<WebElement> prices = block.findElements(By.cssSelector("span[data-testid='price_test_id']"));
			for (WebElement el : prices) {
				if (el.isDisplayed()) {
					String raw = el.getText().trim(); // e.g. "$11.99/mo"
					String[] tokens = raw.split("\\$");
					if (tokens.length > 1) {
						String amountPart = tokens[1].split(" ")[0]; // e.g. "11.99"
						double value = Double.parseDouble(amountPart);
						// int rounded = (int) Math.round(value);
						// return String.valueOf(value);
						if (planType.equalsIgnoreCase("Annual")) {
							int annual = (int) Math.round(value * 12);
							return String.valueOf(annual);
						} else if (planType.equalsIgnoreCase("Monthly")) {
							// int monthly = (int) Math.round(annualMonthly / 0.75);
							return String.valueOf(value);
						}
					}
				}
			}
		} catch (Exception ignored) {
		}
		return "0";
	}

	@Override
	public List<String> extractFeatures(WebElement block) {
		List<String> features = new ArrayList<>();
		try {
			List<WebElement> items = block
					.findElements(By.cssSelector("div._dwg-plan-card-v2__feature-list_nnw6p_99 li"));
			for (WebElement li : items) {
				// Dropbox 每个功能在 li 下层 span 中
				String text = li.getText().trim();
				if (!text.isEmpty())
					features.add(text);
			}
		} catch (Exception ignored) {
		}
		return features;
	}

}
