package feature;

import java.util.*;

/**
 * Utility for computing the Damerau–Levenshtein edit distance
 * between two strings, where adjacent transpositions count as one edit.
 */
class EditDistance {
    public static int compute(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m+1][n+1];

        // initialize base cases
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        // fill dp table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i-1) == s2.charAt(j-1)) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i-1][j] + 1,    // deletion
                             dp[i][j-1] + 1),   // insertion
                             dp[i-1][j-1] + cost // substitution
                );
                // adjacent transposition
                if (i > 1 && j > 1
                 && s1.charAt(i-1) == s2.charAt(j-2)
                 && s1.charAt(i-2) == s2.charAt(j-1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i-2][j-2] + 1);
                }
            }
        }
        return dp[m][n];
    }
}

/**
 * Node in a BK-Tree, storing one word and a map from edit distances
 * to child nodes.
 */
class BKTreeNode {
    final String word;
    final Map<Integer, BKTreeNode> children = new HashMap<>();

    BKTreeNode(String w) {
        this.word = w.toLowerCase();
    }
}

/**
 * BK-Tree for fuzzy (approximate) string matching.  Stores words
 * in a metric space under edit distance.
 */
class BKTree {
    private BKTreeNode root;

    /**
     * Add a word to the BK-Tree (null-safe, lowercased).
     */
    public void add(String word) {
        if (word == null) return;
        String w = word.toLowerCase();
        if (root == null) {
            root = new BKTreeNode(w);
            return;
        }
        BKTreeNode node = root;
        while (true) {
            int dist = EditDistance.compute(w, node.word);
            if (dist == 0) return;  // already present
            BKTreeNode child = node.children.get(dist);
            if (child != null) {
                node = child;
            } else {
                node.children.put(dist, new BKTreeNode(w));
                return;
            }
        }
    }

    /**
     * Search for all words within maxDistance of the query.
     * The result is unsorted; caller may sort & truncate.
     */
    public List<String> search(String query, int maxDistance) {
        if (query == null || root == null) {
            return Collections.emptyList();
        }
        query = query.toLowerCase();
        List<String> result = new ArrayList<>();
        Deque<BKTreeNode> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            BKTreeNode node = stack.pop();
            int d = EditDistance.compute(query, node.word);
            if (d <= maxDistance) {
                result.add(node.word);
            }
            // explore children within [d-maxDistance, d+maxDistance]
            for (int key = d - maxDistance; key <= d + maxDistance; key++) {
                if (key < 0) continue;
                BKTreeNode child = node.children.get(key);
                if (child != null) {
                    stack.push(child);
                }
            }
        }
        return result;
    }
}

/**
 * Simple Trie node for prefix storage.
 */
class TrieNode {
    final Map<Character, TrieNode> children = new HashMap<>();
    boolean isWord = false;
}

/**
 * Trie with support for exact lookup (via fuzzySearch with distance=0).
 */
class TrieWithFuzzySearch {
    private final TrieNode root = new TrieNode();

    /**
     * Insert a word into the Trie (null-safe, lowercased).
     */
    public void insert(String word) {
        if (word == null) return;
        String w = word.toLowerCase();
        if (w.isEmpty()) {
            root.isWord = true;
            return;
        }
        TrieNode node = root;
        for (char c : w.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isWord = true;
    }

    /**
     * Fuzzy-search for exact words only (distance=0).  Used internally
     * by SpellChecker.check for exact-match mode.
     */
    public static class Suggestion {
        public final String word;
        public final int distance;
        public Suggestion(String w, int d) {
            word = w;
            distance = d;
        }
    }

    private void dfsExact(TrieNode node, StringBuilder path, List<String> out) {
        if (node.isWord) {
            out.add(path.toString());
        }
        for (var e : node.children.entrySet()) {
            path.append(e.getKey());
            dfsExact(e.getValue(), path, out);
            path.deleteCharAt(path.length() - 1);
        }
    }

    /**
     * Perform a fuzzySearch with maxDistance=0 to retrieve exact matches.
     */
    public List<Suggestion> fuzzySearch(String prefix, int maxDistance) {
        if (prefix == null || maxDistance != 0) {
            return Collections.emptyList();
        }
        TrieNode node = root;
        for (char c : prefix.toLowerCase().toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                return Collections.emptyList();
            }
        }
        List<String> words = new ArrayList<>();
        dfsExact(node, new StringBuilder(prefix.toLowerCase()), words);
        List<Suggestion> out = new ArrayList<>();
        for (String w : words) {
            out.add(new Suggestion(w, 0));
        }
        return out;
    }
}

/**
 * SpellChecker integrates a Trie (for exact-only mode) and a BK-Tree
 * (for fuzzy-only mode) to implement spellCheck().
 */
public class SpellChecker {
    private final TrieWithFuzzySearch trie = new TrieWithFuzzySearch();
    private final BKTree            bk   = new BKTree();

    /**
     * Load a collection of words into both the Trie and the BK-Tree.
     */
    public void insertWords(Collection<String> words) {
        if (words == null) return;
        for (String w : words) {
            trie.insert(w);
            bk.add(w);
        }
    }

    /**
     * Build or extend dictionary from a List.
     */
    public void buildDictionary(List<String> dict) {
        insertWords(dict);
    }

    /**
     * Perform spell-check:
     *  - If maxDistance==0: only exact matches via Trie
     *  - Else: fuzzy matches via BK-Tree, sorted and truncated
     *
     * @param word           the input (possibly misspelled)
     * @param maxDistance    maximum edit distance allowed
     * @param maxSuggestions maximum number of suggestions
     * @return list of suggestions (possibly empty)
     */
    public List<String> check(String word, int maxDistance, int maxSuggestions) {
        if (word == null) {
            return Collections.emptyList();
        }
        String q = word.toLowerCase();

        // exact‐only mode
        if (maxDistance == 0) {
            for (var s : trie.fuzzySearch(q, 0)) {
                if (s.word.equals(q)) {
                    return List.of(q);
                }
            }
            return Collections.emptyList();
        }

        // fuzzy mode via BK-Tree
        List<String> candidates = bk.search(q, maxDistance);
        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        // sort by actual edit distance, then truncate
        candidates.sort(Comparator.comparingInt(w -> EditDistance.compute(q, w)));
        return candidates.subList(0, Math.min(maxSuggestions, candidates.size()));
    }
}
