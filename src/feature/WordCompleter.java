package feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordCompleter {

	// Represents a single node in the Trie
	public class TrieNode {
		Map<Character, TrieNode> children = new HashMap<>();
		boolean isEndOfWord;

		public TrieNode() {
			this.isEndOfWord = false;
		}
	}

	// Trie implementation with insert and prefix search
	public class Trie {
		private final TrieNode root;

		public Trie() {
			root = new TrieNode();
		}

		// Insert a word into the Trie
		public void insert(String word) {
			TrieNode current = root;
			for (char ch : word.toCharArray()) {
				current = current.children.computeIfAbsent(ch, c -> new TrieNode());
			}
			current.isEndOfWord = true;
		}

		// Retrieve all words starting with the given prefix
		public List<String> getWordsWithPrefix(String prefix) {
			List<String> results = new ArrayList<>();
			TrieNode current = root;

			for (char ch : prefix.toCharArray()) {
				if (!current.children.containsKey(ch)) {
					return results; // Empty list if prefix not found
				}
				current = current.children.get(ch);
			}

			findAllWords(current, prefix, results);
			return results;
		}

		// Helper method to find all words below a given node
		private void findAllWords(TrieNode node, String prefix, List<String> results) {
			if (node.isEndOfWord) {
				results.add(prefix);
			}
			for (char ch : node.children.keySet()) {
				findAllWords(node.children.get(ch), prefix + ch, results);
			}
		}

		// Retrieve all words stored in the Trie
		public List<String> getAllWords() {
			List<String> results = new ArrayList<>();
			findAllWords(root, "", results);
			return results;
		}

	}

	private final Trie trie;

	public WordCompleter() {
		trie = new Trie();
	}

	public List<String> complete(String prefix) {
		return trie.getWordsWithPrefix(prefix.toLowerCase());
	}
	
	public void insertWords(Set<String> words) {
		words.forEach(trie::insert);
	}
	
	public void printAllWords() {
		trie.getAllWords().forEach(System.out::println);
	}


	public static void main(String[] args) {
		
	}

}
