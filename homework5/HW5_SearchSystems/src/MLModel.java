import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ranks autocomplete suggestions for a given query.
 *
 * NOTE: The machine learning ranking model has not been implemented yet.
 * Our team is still training it on user interaction data. For now, this
 * class returns suggestions sorted alphabetically as a placeholder.
 *
 * A trained model that personalizes results by query context and usage
 * frequency will be integrated in a future sprint.
 * 
 * Do not modify this class.
 */
public class MLModel {

    /**
     * Ranks the given list of completions for the provided query.
     *
     * @param completions the raw list of candidate completions
     * @param query       the user's current input (reserved for future use)
     * @return the completions in ranked order
     */
    public List<String> rank(List<String> completions, String query) {
        List<String> ranked = new ArrayList<>(completions);
        Collections.sort(ranked);
        return ranked;
    }
}
