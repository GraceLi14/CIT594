import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An inverted index maps each unique term to the list of document IDs
 * (indices) that contain that term.
 *
 * Example:
 *   Documents: ["binary search tree", "binary heap", "search algorithm"]
 *   Index:
 *     "binary"    -> [0, 1]
 *     "search"    -> [0, 2]
 *     "tree"      -> [0]
 *     "heap"      -> [1]
 *     "algorithm" -> [2]
 *
 * This allows O(1) lookup of all documents containing a given term instead
 * of scanning every document linearly on each query.
 *
 */
public class InvertedIndex {

    private Map<String, List<Integer>> index;
    private List<String> documents;

    public InvertedIndex() {
        this.index = new HashMap<>();
        this.documents = new ArrayList<>();
    }

    /**
     * Builds the inverted index from a list of documents.
     *
     * Each document is tokenized by whitespace. Tokens are lowercased and
     * stripped of non-alphanumeric characters. Each unique token is mapped
     * to the list of document indices in which it appears.
     *
     * @param docs the list of document strings to index
     * @return the constructed inverted index map (also stored internally)
     */
    public Map<String, List<Integer>> buildIndex(List<String> docs) {
        this.documents = new ArrayList<>(docs);
        this.index.clear();

        for (int i = 0; i < docs.size(); i++) {
            String[] tokens = docs.get(i).toLowerCase().split("\\s+");
            for (String token : tokens) {
                token = token.replaceAll("[^a-z0-9]", "");
                if (!token.isEmpty()) {
                    index.computeIfAbsent(token, k -> new ArrayList<>()).add(i);
                }
            }
        }
        return Collections.unmodifiableMap(index);
    }

    /**
     * Returns the list of document indices that contain the given term.
     *
     * @param term the search term (case-insensitive)
     * @return list of document indices, or an empty list if not found
     */
    public List<Integer> lookup(String term) {
        return index.getOrDefault(term.toLowerCase().trim(), Collections.emptyList());
    }

    /**
     * Returns the document stored at the given index.
     *
     * @param docIndex the document index
     * @return the document string
     * @throws IndexOutOfBoundsException if docIndex is out of range
     */
    public String getDocument(int docIndex) {
        if (docIndex < 0 || docIndex >= documents.size()) {
            throw new IndexOutOfBoundsException("Invalid document index: " + docIndex);
        }
        return documents.get(docIndex);
    }

    /** Returns the number of unique indexed terms. */
    public int termCount() {
        return index.size();
    }

    /** Returns the total number of indexed documents. */
    public int documentCount() {
        return documents.size();
    }
}
