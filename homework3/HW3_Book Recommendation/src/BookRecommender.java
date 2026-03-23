import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
     * Adds a book node if it doesn't already exist in graph
     * @param book_id of the book to add
     */
    public void addVertex(String book_id) {
        if (!this.coLikeGraph.containsKey(book_id)) {
            this.coLikeGraph.put(book_id, new HashMap<String, Integer>());
        }
    }

    /**
     * Removes a book node and removes all references to it from all its neighbors
     * @param book_id of the book to remove
     */
    public void removeVertex(String book_id) {
        //delete the book (outer key)
        this.coLikeGraph.remove(book_id);

        //delete any references to the book from neighboring books
        for(String book : this.coLikeGraph.keySet()) {
            //grab the hashmap for each outer key book (inner map)
            Map<String, Integer> neighbors = this.coLikeGraph.get(book);
            //if any inner key book is the book node to be deleted, remove it from the inner key
            neighbors.remove(book_id);
            }
        }

    public void addEdge(String book_id1, String book_id2) {

    }
    //main method
    public static void main(String[] args) {
    }
    }


