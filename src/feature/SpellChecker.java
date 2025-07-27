package feature;

import java.util.*;

/**
 * Utility class for computing the Damerau–Levenshtein distance
 * between two strings, where adjacent transpositions count as one edit.
 */
class EditDistance {
    public static int compute(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1,    // deletion
                             dp[i][j - 1] + 1),   // insertion
                             dp[i - 1][j - 1] + cost // substitution
                );
                // adjacent transposition
                if (i > 1 && j > 1
                 && s1.charAt(i - 1) == s2.charAt(j - 2)
                 && s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j],
                                dp[i - 2][j - 2] + 1);
                }
            }
        }
        return dp[m][n];
    }
}

/** Node of a BK‑Tree, storing one word and children keyed by edit distance. */
class BKTreeNode {
    final String word;
    final Map<Integer,BKTreeNode> children = new HashMap<>();
    BKTreeNode(String w) { word = w.toLowerCase(); }
}

/** BK‑Tree for approximate string matching under edit distance. */
class BKTree {
    BKTreeNode root;

    /** Add a word to the tree (null-safe). */
    public void add(String word) {
        if (word == null) return;
        String w = word.toLowerCase();
        if (root == null) {
            root = new BKTreeNode(w);
            return;
        }
        BKTreeNode cur = root;
        while (true) {
            int d = EditDistance.compute(w, cur.word);
            if (d == 0) return;  // already present
            BKTreeNode child = cur.children.get(d);
            if (child != null) {
                cur = child;
            } else {
                cur.children.put(d, new BKTreeNode(w));
                return;
            }
        }
    }

    /**
     * Return all words within maxDistance of query (unsorted).
     * Caller may sort & limit.
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
            for (int dist = d - maxDistance; dist <= d + maxDistance; dist++) {
                if (dist < 0) continue;
                BKTreeNode child = node.children.get(dist);
                if (child != null) {
                    stack.push(child);
                }
            }
        }
        return result;
    }
}

/** Simple Trie node for prefix storage. */
class TrieNode {
    final Map<Character,TrieNode> children = new HashMap<>();
    boolean isWord = false;
}

/**
 * Trie with support for exact-match lookup via fuzzySearch(distance=0).
 */
class TrieWithFuzzySearch {
    private final TrieNode root = new TrieNode();

    /** Insert a word into the Trie (null-safe). */
    public void insert(String word) {
        if (word == null) return;
        String w = word.toLowerCase();
        if (w.isEmpty()) {
            root.isWord = true;
            return;
        }
        TrieNode cur = root;
        for (char c : w.toCharArray()) {
            cur = cur.children.computeIfAbsent(c, k -> new TrieNode());
        }
        cur.isWord = true;
    }

    /** Holder for an exact-match suggestion (distance always 0). */
    public static class Suggestion {
        public final String word;
        public final int distance;
        public Suggestion(String w, int d) { word = w; distance = d; }
    }

    // DFS to collect all words under a given node
    private void dfs(TrieNode node, StringBuilder path, List<String> out) {
        if (node.isWord) {
            out.add(path.toString());
        }
        for (var e : node.children.entrySet()) {
            path.append(e.getKey());
            dfs(e.getValue(), path, out);
            path.deleteCharAt(path.length() - 1);
        }
    }

    /**
     * Fuzzy-search with maxDistance=0 to get exact matches only.
     */
    public List<Suggestion> fuzzySearch(String prefix, int maxDistance) {
        if (prefix == null || maxDistance != 0) {
            return Collections.emptyList();
        }
        TrieNode cur = root;
        for (char c : prefix.toLowerCase().toCharArray()) {
            cur = cur.children.get(c);
            if (cur == null) {
                return Collections.emptyList();
            }
        }
        List<String> words = new ArrayList<>();
        dfs(cur, new StringBuilder(prefix.toLowerCase()), words);
        List<Suggestion> out = new ArrayList<>(words.size());
        for (String w : words) {
            out.add(new Suggestion(w, 0));
        }
        return out;
    }
}

/**
 * Main SpellChecker: combines Trie (exact) + BKTree (fuzzy).
 * Implements a 2‑phase sort: by edit distance, then by “root substring” match.
 */
public class SpellChecker {
    private final TrieWithFuzzySearch trie = new TrieWithFuzzySearch();
    private final BKTree             bk   = new BKTree();

    /**
     * Bulk-insert a collection of words into both Trie and BK‑Tree.
     */
    public void insertWords(Collection<String> words) {
        if (words == null) return;
        for (String w : words) {
            trie.insert(w);
            bk.add(w);
        }
    }

    /**
     * Spell-check a word:
     * 1) If maxDistance==0, do exact-only via Trie.
     * 2) Else, get all BK‑Tree matches ≤maxDistance.
     * 3) Sort by edit distance ascending.
     * 4) Promote candidates containing the typo’s “root” substring:
     *    root = word without its last character.
     * 5) Truncate to maxSuggestions.
     */
    public List<String> check(String word, int maxDistance, int maxSuggestions) {
        if (word == null) {
            return Collections.emptyList();
        }
        String q = word.toLowerCase();

        // exact-only mode
        if (maxDistance == 0) {
            for (var s : trie.fuzzySearch(q, 0)) {
                if (s.word.equals(q)) {
                    return List.of(q);
                }
            }
            return Collections.emptyList();
        }

        // fuzzy via BK‑Tree
        List<String> candidates = bk.search(q, maxDistance);
        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        // 1) sort by edit distance
        candidates.sort(Comparator.comparingInt(w -> EditDistance.compute(q, w)));

        // 2) define root substring (all but last char)
        String root = q.length() > 1 ? q.substring(0, q.length() - 1) : q;

        // 3) bucketize
        List<String> bucket1 = new ArrayList<>();
        List<String> bucket2 = new ArrayList<>();
        for (String cand : candidates) {
            if (cand.contains(root)) {
                bucket1.add(cand);
            } else {
                bucket2.add(cand);
            }
        }

        // 4) merge & truncate
        List<String> merged = new ArrayList<>(bucket1);
        merged.addAll(bucket2);
        return merged.subList(0, Math.min(maxSuggestions, merged.size()));
    }
}
