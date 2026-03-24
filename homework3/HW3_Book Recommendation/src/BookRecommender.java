import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 *
 */
public class BookRecommender {

    //adjacency graph

    //fields

    //outer key = book, inner key = neighboring book, inner value = edge weight
    private Map<String, Map<String, Integer>> coLikeGraph;

    //constructor
    public BookRecommender() {
        this.coLikeGraph = new HashMap<>();
    }


    //methods

    /**
     * Adds a book node if it doesn't already exist in graph
     * @param bookID of the book to add
     */
    public void addVertex(String bookID) {
        if (!this.coLikeGraph.containsKey(bookID)) {
            this.coLikeGraph.put(bookID, new HashMap<String, Integer>());
        }
    }

    /**
     * Removes a book node and removes all references to it from all its neighbors
     * @param bookID of the book to remove
     */
    public void removeVertex(String bookID) {
        //delete the book (outer key)
        this.coLikeGraph.remove(bookID);

        //delete any references to the book from neighboring books
        for(String book : this.coLikeGraph.keySet()) {
            //grab the hashmap for each outer key book (inner map)
            Map<String, Integer> neighbors = this.coLikeGraph.get(book);
            //if any inner key book is the book node to be deleted, remove it from the inner key
            neighbors.remove(bookID);
            }
        }

    /**
     * Adds undirected edge between two books.
     * If edge already exists, increment its weight by 1.
     * If not, create the edge and set starting weight as 1.
      * @param bookID1 the first book
     * @param bookID2 the second book
     */
    public void addEdge(String bookID1, String bookID2) {
        //not allow self-edge
        if (bookID1.equals(bookID2)) {
            return;
        }
        //add book ids as vertex to allow for more robust application of this method
        addVertex(bookID1);
        addVertex(bookID2);

        //increment book1->book2
        //get the neighbors of book 1
        Map<String, Integer> book1Neighbors = this.coLikeGraph.get(bookID1);
        //if book 1 already has book 2 as its neighbor, increment the weight by 1
        if(book1Neighbors.containsKey(bookID2)) {
            book1Neighbors.put(bookID2, book1Neighbors.get(bookID2) + 1);
        //if book 2 doesn't exist as a neighbor, make book 2 a neighbor and set weight as 1
        } else {
            book1Neighbors.put(bookID2, 1);
        }

        //increment book2->book1
        //get the neighbors of book 2
        Map<String, Integer> book2Neighbors = this.coLikeGraph.get(bookID2);
        //if book 2 already has book 1 as its neighbor, increment the weight by 1
        if(book2Neighbors.containsKey(bookID1)) {
            book2Neighbors.put(bookID1, book2Neighbors.get(bookID1) + 1);
        //if book 1 doesn't exist as a neighbor, make book 1 a neighbor and set weight as 1
        } else {
            book2Neighbors.put(bookID1, 1);
        }
    }

    /**
     * Reads csv file and converts it into co-like graph.
     * Each line in file should be in format: User_ID,Book_ID
     * @param fileName the name of the csv file
     */
    public void loadData(String fileName) {
        //to store all the books each user liked
        HashMap<String, ArrayList<String>> userAndBooks = new HashMap<>();

        try {
            //process the file
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            //read the line
            String line = br.readLine();
            //keep reading until the end of the file
            while (line != null) {
                //trim whitespaces
                line = line.trim();
                //after trimming whitespace, if line is not empty
                if(!line.isEmpty()) {
                    //split line by "," into an array and set it as data variable
                    String[] data = line.split(",");
                    //as long as data has the length of 2
                    if(data.length == 2) {
                        //set the first item as userID and get rid of whitespaces
                        String userID = data[0].trim();
                        //set the second item as bookID and get rid of whitespaces
                        String bookID = data[1].trim();
                        //create a new vertex/node for the bookID
                        addVertex(bookID);
                        //if userAndBooks doesn't contain the userID, add it with an empty ArrayList as value
                        if(!userAndBooks.containsKey(userID)) {
                            userAndBooks.put(userID, new ArrayList<>());
                        }
                        //add bookID to the userID's array list
                        userAndBooks.get(userID).add(bookID);
                    }
                }
                //read the next line
                line = br.readLine();
            }
            //close the buffered reader
            br.close();

            //iterate through all books per user to find combination pairs, i.e. coLikes
            for (String user : userAndBooks.keySet()) {
                //create an array list of books liked per user
                ArrayList<String> bookIDs = userAndBooks.get(user);
                //find the pairs of books liked
                for (int i = 0; i < bookIDs.size(); i++) {
                    for (int j = i+1; j < bookIDs.size(); j++) {
                        String bookID1 = bookIDs.get(i);
                        String bookID2 = bookIDs.get(j);
                        //for each combination pair add an edge
                        addEdge(bookID1, bookID2);
                    }
                }
            }
            //if file name wrong or can't be found
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
            //catch any problem while reading file
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the list of top 5 neighbors by highest edge weight of a book.
     * If the book does not exist in the graph or has no neighbors, method returns "NONE".
     *
     * Neighbors sorted by higher edge weight first then alphabetical book ID if weights tie
     * @param bookID the book you're finding the neighbors of
     * @return String of top 5 neighbors by edge weight separated by commas or "NONE" if no valid recommendations
     */
    public String nearestNeighbors(String bookID){
        //if bookID doesn't exist or has no neighbors, return string "NONE"
        if(!this.coLikeGraph.containsKey(bookID) || this.coLikeGraph.get(bookID).isEmpty()) {
            return "NONE";
        }

        //convert neighbor map into list of entries to be sorted
        List<Map.Entry<String, Integer>> neighbors = new ArrayList<Map.Entry<String, Integer>>(this.coLikeGraph.get(bookID).entrySet());

        //use Collections.sort method to sort neighbors by descending edge weight, then alphabetically by book ID
        Collections.sort(neighbors, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> i, Map.Entry<String, Integer> j) {
                //compare the book weights and sort by descending order
                if(!i.getValue().equals(j.getValue())) {
                    return j.getValue().compareTo(i.getValue());
                }
                //if the values are equal, sort alphabetically by bookID
                return i.getKey().compareTo(j.getKey());

            }

        });

        //placeholder for top results to be converted to a string
        String topResults = "";
        //must return up to 5 results
        int limit = Math.min(neighbors.size(), 5);

        //building the string of neighboring books separated by comma
        for(int entry = 0; entry < limit; entry++) {
            topResults += neighbors.get(entry).getKey();
            //unless entry is the last item, add a comma after it in the string
            if(entry != limit-1) {
                topResults += ",";
            }
        }
        //return the final string
        return topResults;
        }

    /**
     *
      * @param userID
     * @return
     */
    public String likeHistorynearestNeighbors(String[] likedBooks){
        for(String bookID : likedBooks) {
            if(!this.coLikeGraph.containsKey(bookID) || this.coLikeGraph.get(bookID).isEmpty()) {
                return "NONE";
            }

        }
        //for each book, get the inner map
        //create another hashmap where you iterate through each book's neighbors and
        // either add them to the new hashmap if it doesnt exist with the value being the weight
        // OR add new weight to existing bookid
        return "None";
    }

    }


    //main method
    public static void main(String[] args) {

    }



