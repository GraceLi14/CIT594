import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tracks recent user search queries.
 *
 * Maintains a fixed-capacity history of the most recent unique queries,
 * with the most recent query first. Re-submitting an existing query moves
 * it to the front rather than creating a duplicate.
 *
 * Used by the frontend to populate the "Recent searches" panel shown when
 * the search bar is focused but empty.
 */
public class SearchHistoryService {

    private final int maxSize;
    private final Deque<String> history;
    private final Set<String> seen;

    public SearchHistoryService(int maxSize) {
        this.maxSize = maxSize;
        this.history = new ArrayDeque<>();
        this.seen = new HashSet<>();
    }

    /**
     * Records a query in the history.
     *
     * If the query is already present, it is moved to the front. If the
     * history is at capacity and the query is new, the oldest entry is evicted.
     *
     * @param query the query string to record (ignored if null or blank)
     */
    public void record(String query) {
        if (query == null || query.isBlank()) return;
        query = query.trim();

        if (seen.contains(query)) {
            history.remove(query);
        } else {
            if (history.size() >= maxSize) {
                String evicted = history.removeLast();
                seen.remove(evicted);
            }
            seen.add(query);
        }
        history.addFirst(query);
    }

    /**
     * Returns recent queries, most recent first.
     *
     * @return a snapshot of the current history
     */
    public List<String> getRecent() {
        return new ArrayList<>(history);
    }

    /**
     * Clears all recorded history.
     */
    public void clear() {
        history.clear();
        seen.clear();
    }

    /** Returns the number of queries currently in history. */
    public int size() {
        return history.size();
    }
}
