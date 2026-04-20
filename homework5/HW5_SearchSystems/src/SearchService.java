import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * SearchService is the central coordinator for the ScaleSearch platform.
 *
 * It wires together four sub-services and exposes a unified API used by
 * the HTTP layer (SearchApp) and by tests.
 *
 * Sub-services:
 *   - AutocompleteService  prefix-based word suggestions as the user types
 *   - InvertedIndex        full-text document search
 *   - SpellChecker         "did you mean?" suggestions for unrecognized terms
 *   - SearchHistoryService recent query history per session
 *
 * All sub-services are initialized once in the constructor.
 */
public class SearchService {

    private final AutocompleteService autocompleteService;
    private final InvertedIndex invertedIndex;
    private final SpellChecker spellChecker;
    private final SearchHistoryService historyService;

    /**
     * Initializes all sub-services.
     *
     * @param wordList  words used for autocomplete and spell checking
     * @param documents documents indexed for full-text search
     */
    public SearchService(List<String> wordList, List<String> documents) {
        this.autocompleteService = new AutocompleteService(wordList);
        this.invertedIndex = new InvertedIndex();
        this.invertedIndex.buildIndex(documents);
        this.spellChecker = new SpellChecker(wordList);
        this.historyService = new SearchHistoryService(20);
    }

    /**
     * Searches for documents containing the given query terms.
     *
     * Tokens are extracted from the query, the inverted index is consulted
     * for each, and the union of matching documents is returned.
     * The query is also recorded in search history.
     *
     * @param query one or more search terms
     * @return list of matching document strings, in order of first occurrence
     */
    public List<String> search(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        historyService.record(query.trim());

        String[] tokens = query.toLowerCase().split("\\s+");
        Set<Integer> matchingIds = new LinkedHashSet<>();
        for (String token : tokens) {
            matchingIds.addAll(invertedIndex.lookup(token));
        }

        List<String> results = new ArrayList<>();
        for (int id : matchingIds) {
            results.add(invertedIndex.getDocument(id));
        }
        return results;
    }

    /**
     * Returns autocomplete suggestions for the given prefix.
     *
     * @param prefix     the prefix typed by the user
     * @param maxResults maximum number of suggestions to return
     * @return ordered list of suggestions
     */
    public List<String> autocomplete(String prefix, int maxResults) {
        if (prefix == null || prefix.isBlank()) {
            return Collections.emptyList();
        }
        return autocompleteService.finish(prefix.toLowerCase().trim(), maxResults);
    }

    /**
     * Returns spelling suggestions for a word not found in the index.
     *
     * Intended for use in "Did you mean X?" prompts shown when search
     * returns no results.
     *
     * @param word           the word to find alternatives for
     * @param maxSuggestions maximum number of spelling suggestions
     * @return list of close matches from the dictionary
     */
    public List<String> spellCheck(String word, int maxSuggestions) {
        if (word == null || word.isBlank()) {
            return Collections.emptyList();
        }
        return spellChecker.suggest(word.toLowerCase().trim(), maxSuggestions);
    }

    /**
     * Returns recent search queries, most recent first.
     *
     * @return list of recent query strings
     */
    public List<String> getRecentSearches() {
        return historyService.getRecent();
    }

    /** Returns the number of unique terms indexed for full-text search. */
    public int indexedTermCount() {
        return invertedIndex.termCount();
    }
}
