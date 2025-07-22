package feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SpellChecker {

	private final List<String> vocabulary = new ArrayList<>();

	public SpellChecker() {

	}

	public String check(String keyword) {
		if (vocabulary.contains(keyword.toLowerCase())) {
			return keyword;
		}

		String bestMatch = null;
		int minDistance = Integer.MAX_VALUE;

		for (String word : vocabulary) {
			int distance = calculateEditDistance(keyword.toLowerCase(), word);
			if (distance < minDistance) {
				minDistance = distance;
				bestMatch = word;
			}
		}

		return (minDistance <= 3) ? bestMatch : keyword;
	}

	// Calculates the Edit Distance between two words using dynamic programming
	public static int calculateEditDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();

		// Create a DP table to store the results of subproblems
		int[][] dp = new int[len1 + 1][len2 + 1];

		// Fill dp[][] in a bottom-up manner
		for (int i = 0; i <= len1; i++) {
			for (int j = 0; j <= len2; j++) {
				// If the first word is empty, insert all characters of the second word
				if (i == 0) {
					dp[i][j] = j; // j insertions
				}
				// If the second word is empty, remove all characters of the first word
				else if (j == 0) {
					dp[i][j] = i; // i deletions
				}
				// If the last characters of both words are the same, ignore the last character
				// and recur for the remaining words
				else if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
					dp[i][j] = dp[i - 1][j - 1];
				}
				// If the last characters are different, consider all possibilities:
				// insert, delete, or replace the last character
				else {
					dp[i][j] = 1 + Math.min(dp[i - 1][j], // Remove
							Math.min(dp[i][j - 1], // Insert
									dp[i - 1][j - 1])); // Replace
				}
			}
		}

		// The final value in dp[len1][len2] will be the answer
		return dp[len1][len2];
	}
	
	public void insertWords(Set<String> words) {
		this.vocabulary.addAll(words);
	}
}
