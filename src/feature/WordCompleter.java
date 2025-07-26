package feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * WordCompleter
 *
 * Provides word auto-completion functionality using a Trie (prefix tree) data structure.
 * 
 * Features:
 *  - Insert words into a Trie.
 *  - Retrieve all words that start with a given prefix.
 *  - List all words currently stored in the Trie.
 */
public class WordCompleter {

    /**
     * Represents a single node in the Trie.
     * Each node can store:
     *  - a map of children nodes (one for each character),
     *  - and a flag indicating whether it marks the end of a valid word.
     */
    public class TrieNode {
        // Map of child nodes (key = character, value = child node)
        Map<Character, TrieNode> children = new HashMap<>();
        // Marks the end of a word
        boolean isEndOfWord;

        public TrieNode() {
            this.isEndOfWord = false;
        }
    }

    /**
     * Trie implementation with insert and prefix-based search capabilities.
     */
    public class Trie {
        private final TrieNode root;

        public Trie() {
            root = new TrieNode();
        }

        /**
         * Inserts a word into the Trie.
         *
         * @param word the word to insert
         */
        public void insert(String word) {
            TrieNode current = root;
            // Traverse each character of the word
            for (char ch : word.toCharArray()) {
                // Create a child node if it doesn't exist
                current = current.children.computeIfAbsent(ch, c -> new TrieNode());
            }
            // Mark the last node as the end of a word
            current.isEndOfWord = true;
        }

        /**
         * Returns a list of all words starting with the specified prefix.
         *
         * @param prefix the prefix to search for
         * @return a list of words that start with the given prefix
         */
        public List<String> getWordsWithPrefix(String prefix) {
            List<String> results = new ArrayList<>();
            TrieNode current = root;

            // Navigate to the node representing the last character of the prefix
            for (char ch : prefix.toCharArray()) {
                if (!current.children.containsKey(ch)) {
                    // If the prefix does not exist, return an empty list
                    return results;
                }
                current = current.children.get(ch);
            }

            // Collect all words that extend this prefix
            findAllWords(current, prefix, results);
            return results;
        }

        /**
         * Helper method to recursively collect all words from the given node downward.
         *
         * @param node   the starting node
         * @param prefix the current prefix formed so far
         * @param results list to collect the words found
         */
        private void findAllWords(TrieNode node, String prefix, List<String> results) {
            // If this node marks the end of a word, add it to the results
            if (node.isEndOfWord) {
                results.add(prefix);
            }
            // Traverse each child and continue building words
            for (char ch : node.children.keySet()) {
                findAllWords(node.children.get(ch), prefix + ch, results);
            }
        }

        /**
         * Retrieves all words stored in the Trie.
         *
         * @return a list containing all stored words
         */
        public List<String> getAllWords() {
            List<String> results = new ArrayList<>();
            findAllWords(root, "", results);
            return results;
        }
    }

    // Main Trie instance used by WordCompleter
    private final Trie trie;

    /**
     * Initializes the WordCompleter with an empty Trie.
     */
    public WordCompleter() {
        trie = new Trie();
    }

    /**
     * Returns all words in the Trie that start with the given prefix.
     * Converts the prefix to lowercase for case-insensitive search.
     *
     * @param prefix the search prefix
     * @return a list of matching words
     */
    public List<String> complete(String prefix) {
        return trie.getWordsWithPrefix(prefix.toLowerCase());
    }

    /**
     * Inserts a set of words into the Trie.
     *
     * @param words the set of words to insert
     */
    public void insertWords(Set<String> words) {
    	 words.forEach(word -> trie.insert(word.toLowerCase()));
    }

    /**
     * Prints all words currently stored in the Trie to the console.
     */
    public void printAllWords() {
        trie.getAllWords().forEach(System.out::println);
    }
    
    public  List<String> getAllWords(){
    	    return trie.getAllWords();
        
    }

    /**
     * Example main method for testing purposes.
     */
    public static void main(String[] args) {
        WordCompleter completer = new WordCompleter();
        completer.insertWords(Set.of("cat", "car", "carbon", "dog", "door"));

        System.out.println("All words in Trie:");
        completer.printAllWords();

        System.out.println("\nWords starting with 'ca':");
        completer.complete("ca").forEach(System.out::println);
    }
}
