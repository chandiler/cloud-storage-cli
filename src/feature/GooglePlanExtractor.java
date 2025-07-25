package feature;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析 Google One 页面中的计划卡片信息
 * Extracts plan data from Google One plan blocks
 */
public class GooglePlanExtractor implements PlanExtractor {

	@Override
	public String extractTitle(WebElement block) {
		String title = WebCrawler.getTextOrNA(block, By.cssSelector("div.onuV9b"));
		return title.equals("N/A") ? "Basic(15G)" : title;
	}

	// @Override
	// public String extractPrice(WebElement block, String planType) {
	// List<WebElement> spans = block.findElements(By.cssSelector("div.BQxAac
	// span"));
	// if (spans.isEmpty()) return "FREE";
	// if (planType.equals("Annual")) {
	// return spans.get(spans.size() - 1).getText().trim();
	// } else {
	// return spans.get(0).getText().trim();
	// }
	// }
	//
	@Override
	public String extractPrice(WebElement block, String planType) {
		try {
			List<WebElement> allSpans = block.findElements(By.cssSelector("span"));
			for (WebElement span : allSpans) {
				String raw = span.getText().trim();
				if (raw.contains("$") && !raw.toLowerCase().contains("after")) {
					String[] tokens = raw.split("\\$");
					if (tokens.length > 1) {
						String amountPart = tokens[1].split("[^\\d\\.]+")[0];
						double value = Double.parseDouble(amountPart);
						// int rounded = (int) Math.round(val);
						return String.valueOf(value);
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
		List<WebElement> liList = block.findElements(By.cssSelector("ul.OWqi7c li.rzIFlb"));
		for (WebElement li : liList) {
			String text = WebCrawler.getTextOrNA(li, By.cssSelector("span.ZI49d"));
			if (!text.isEmpty())
				features.add(text);
		}
		return features;
	}
}
