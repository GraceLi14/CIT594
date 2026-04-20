import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Provides spelling suggestions for a given query term.
 *
 * Uses Levenshtein edit distance to find words in the dictionary that are
 * similar to the input. Suggestions are returned sorted by edit distance
 * (closest match first).
 *
 * Edit distance is computed lazily — only when suggest() is called — so
 * construction is cheap regardless of dictionary size.
 *
 * Typical use: show a "Did you mean X?" prompt when a search returns no results.
 */
public class SpellChecker {

    private final List<String> dictionary;

    /**
     * Constructs a SpellChecker backed by the given word list.
     *
     * @param dictionary the set of known correct words
     */
    /**
     * Constructs a SpellChecker backed by the given word list.
     *
     * @param dictionary the set of known correct words
     */
    public SpellChecker(List<String> dictionary) {
        HashSet<String> temp = new HashSet<>(dictionary.size() * 8);

        for (String phrase : dictionary) {
            if (phrase != null) {
                phrase = phrase.toLowerCase().trim();
                temp.add(phrase);
                String[] words = phrase.split(" ");
                for (String word : words) {
                    temp.add(word.trim());
                }
            }
        }
        this.dictionary = new ArrayList<>(temp);
    }

    /**
     * Returns up to maxSuggestions words from the dictionary that are within
     * edit distance 2 of the given word, sorted closest-first.
     *
     * @param word           the (possibly misspelled) input word
     * @param maxSuggestions the maximum number of suggestions to return
     * @return a list of close matches, or an empty list if none found
     */
    public List<String> suggest(String word, int maxSuggestions) {
        if (word == null || word.isBlank()) return new ArrayList<>();

        String normalized = word.toLowerCase().trim();
        List<String> candidates = new ArrayList<>();

        for (String candidate : dictionary) {
            // Skip words whose length differs by more than the max edit distance
            if (Math.abs(candidate.length() - normalized.length()) > 2) continue;
            if (editDistance(normalized, candidate) <= 2) {
                candidates.add(candidate);
            }
        }

        candidates.sort(Comparator.comparingInt(s -> editDistance(normalized, s)));
        return candidates.subList(0, Math.min(maxSuggestions, candidates.size()));
    }

    /**
     * Computes the Levenshtein edit distance between two strings.
     *
     * Edit distance is the minimum number of single-character insertions,
     * deletions, or substitutions needed to transform one string into the other.
     *
     * Time complexity: O(m * n) where m and n are the string lengths.
     *
     * @param a first string
     * @param b second string
     * @return the edit distance between a and b
     */
    public int editDistance(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                               Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[m][n];
    }
}
