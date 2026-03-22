import java.util.ArrayList;
import java.util.Map;

/**
 *
 */
public class BookRecommender {

    //adjacency graph

    //fields

    //outer key is a book, inner key is neighboring book and inner value is edge weight
    private HashMap<String, HashMap<String, Integer>> coLikeGraph;

    //constructor
    public BookRecommender(HashMap<String, HashMap<String, Integer>> coLikeGraph) {
        this.coLikeGraph = coLikeGraph;
    }


    //methods

    /**
     *
     * @param book_id
     */
    public void addVertex(String book_id) {
        if (!this.coLikeGraph.containsKey(book_id)) {
            coLikeGraph.put(book_id, new HashMap<String, Integer>());
        }
    }

    /**
     *
     * @param book_id
     */
    public void removeVertex(String book_id) {
        //delete the key
        if (this.coLikeGraph.containsKey(book_id)) {
            this.coLikeGraph.remove(book_id);
        }

        //delete any references to the key as a neighbor
        for(String book : this.coLikeGraph.keySet()) {
            HashMap<String, Integer> map = this.coLikeGraph.get(book);
            if(map.containsKey(book_id)) {
                map.remove(book_id);
                coLikeGraph.put(book_id, map);
            }
            }
        }
    }



    //main method
    public static void main(String[] args) {
    }

}
