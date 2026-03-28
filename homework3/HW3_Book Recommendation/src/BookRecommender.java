import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 *
 */
public class BookRecommender {

    //fields

    //for CoLikeGraph, outer key = book, inner key = neighboring book, inner value = edge weight
    private Map<String, Map<String, Integer>> coLikeGraph;
    //for User-Based Graph, user vertexes mapped to liked books vertexes
    private Map<String, Set<String>> userToBooks;
    //for User-Based Graph, book vertexes mapped to users vertexes who liked the books
    private Map<String, Set<String>> bookToUsers;

    //constructor
    public BookRecommender() {
        this.coLikeGraph = new HashMap<>();
        this.userToBooks = new HashMap<>();
        this.bookToUsers = new HashMap<>();
    }


    //methods

    /**
     * Adds a book node if it doesn't already exist in graph
     *
     * @param bookID of the book to add
     */
    public void addVertex(String bookID) {
        if (!this.coLikeGraph.containsKey(bookID)) {
            this.coLikeGraph.put(bookID, new HashMap<String, Integer>());
        }
    }

    /**
     * Removes a book node and removes all references to it from all its neighbors
     *
     * @param bookID of the book to remove
     */
    public void removeVertex(String bookID) {
        //delete the book (outer key)
        this.coLikeGraph.remove(bookID);

        //delete any references to the book from neighboring books
        for (String book : this.coLikeGraph.keySet()) {
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
     *
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
        if (book1Neighbors.containsKey(bookID2)) {
            book1Neighbors.put(bookID2, book1Neighbors.get(bookID2) + 1);
            //if book 2 doesn't exist as a neighbor, make book 2 a neighbor and set weight as 1
        } else {
            book1Neighbors.put(bookID2, 1);
        }

        //increment book2->book1
        //get the neighbors of book 2
        Map<String, Integer> book2Neighbors = this.coLikeGraph.get(bookID2);
        //if book 2 already has book 1 as its neighbor, increment the weight by 1
        if (book2Neighbors.containsKey(bookID1)) {
            book2Neighbors.put(bookID1, book2Neighbors.get(bookID1) + 1);
            //if book 1 doesn't exist as a neighbor, make book 1 a neighbor and set weight as 1
        } else {
            book2Neighbors.put(bookID1, 1);
        }
    }

    /**
     * Reads csv file and converts it into a HashMap where
     * User_ID is the key and a unique set of Book_IDs liked by the user is the value.
     * Each line in file should be in format: User_ID,Book_ID
     *
     * @param fileName the name of the csv file
     */
    public Map<String, Set<String>> readCSV(String fileName) {
        //to store all the books each user liked
        Map<String, Set<String>> userToBooks = new HashMap<>();

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
                if (!line.isEmpty()) {
                    //split line by "," into an array and set it as data variable
                    String[] data = line.split(",");
                    //as long as data has the length of 2
                    if (data.length == 2) {
                        //set the first item as userID and get rid of whitespaces
                        String userID = data[0].trim();
                        //set the second item as bookID and get rid of whitespaces
                        String bookID = data[1].trim();

                        //if userToBooks doesn't contain the userID, add it with an empty HashSet as value
                        if (!userToBooks.containsKey(userID)) {
                            userToBooks.put(userID, new HashSet<String>());
                        }
                        //add bookID to the userID's HashSet of liked books
                        userToBooks.get(userID).add(bookID);
                    }
                }
                //read the next line
                line = br.readLine();
            }
            //close the buffered reader
            br.close();

            //if file name wrong or can't be found
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
            //catch any problem while reading file
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return userToBooks;
    }

    /**
     * Reads the csv file and uses the data to build the CoLikeGraph.
     * In the co-like graph each vertex is a unique book and
     * each undirected edge exists between books if at least one user liked both.
     * The edge weight signifies how many users liked both books.
     *
     * @param fileName the name of CSV file containing lines of User_ID, Book_ID
     */
    public void buildCoLikeGraph(String fileName) {
        //map each user to the unique books they liked
        Map<String, Set<String>> userToBooks = this.readCSV(fileName);

        //iterate through each user
        for (String userID : userToBooks.keySet()) {
            //create an Array List of the books each user liked
            ArrayList<String> bookIDs = new ArrayList<>(userToBooks.get(userID));
            //iterate through each book combination liked for each user
            for (int i = 0; i < bookIDs.size(); i++) {
                //create an new vertex for the book if it doesn't already exist in the coLikeGraph
                addVertex(bookIDs.get(i));
                for (int j = i + 1; j < bookIDs.size(); j++) {
                    //create an edge for this book to every later book liked by the user
                    addEdge(bookIDs.get(i), bookIDs.get(j));
                }
            }
        }
    }

    /**
     * Returns the list of top 5 neighbors by highest edge weight of a book.
     * If the book does not exist in the graph or has no neighbors, method returns "NONE".
     * <p>
     * Neighbors sorted by higher edge weight first then alphabetical book ID if weights tie
     *
     * @param bookID the book you're finding the neighbors of
     * @return String of top 5 neighbors by edge weight separated by commas or "NONE" if no valid recommendations
     */
    public String nearestNeighbors(String bookID) {
        //if bookID doesn't exist or has no neighbors, return string "NONE"
        if (!this.coLikeGraph.containsKey(bookID) || this.coLikeGraph.get(bookID).isEmpty()) {
            return "NONE";
        }

        //convert neighbor map into list of entries to be sorted
        List<Map.Entry<String, Integer>> neighbors = new ArrayList<Map.Entry<String, Integer>>(this.coLikeGraph.get(bookID).entrySet());

        //use Collections.sort method to sort neighbors by descending edge weight, then alphabetically by book ID
        Collections.sort(neighbors, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> i, Map.Entry<String, Integer> j) {
                //compare the book weights and sort by descending order
                if (!i.getValue().equals(j.getValue())) {
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
        for (int entry = 0; entry < limit; entry++) {
            topResults += neighbors.get(entry).getKey();
            //unless entry is the last item, add a comma after it in the string
            if (entry != limit - 1) {
                topResults += ",";
            }
        }
        //return the final string
        return topResults;
    }

    /**
     * Returns up to 5 books based on cumulative neighbor
     * weights of the books in the user's like history
     * <p>
     * Books already in input history are excluded.
     * If there are no valid recommendations, return "NONE".
     *
     * @param likedBooks the books in user's like history
     * @return String up to 5 recommended bookIDs or "NONE" if no valid recommendations exist
     */
    public String likeHistoryNearestNeighbors(String[] likedBooks) {
        //if given an empty list, return string "NONE"
        if (likedBooks.length == 0) return "NONE";

        // Store input books in a set so they can be excluded from recommendations
        Set<String> inputBooks = new HashSet<String>();
        for (String bookID : likedBooks) {
            inputBooks.add(bookID);
        }

        //to aggregate all liked books' neighboring books and their weights
        Map<String, Integer> allBooksNeighbors = new HashMap<>();

        //loop through all books liked historically
        for (String bookID : likedBooks) {
            //if book doesn't exist in coLikeGraph or has no neighbors, skip
            if (!this.coLikeGraph.containsKey(bookID) || this.coLikeGraph.get(bookID).isEmpty()) {
                continue;
            }
            //get inner map of neighboring books and edge weights
            Map<String, Integer> bookNeighbors = this.coLikeGraph.get(bookID);

            //loop through all neighboring books
            for (String book : bookNeighbors.keySet()) {
                //skip books already in like history
                if (inputBooks.contains(book)) {
                    continue;
                }
                //if neighboring book doesn't currently exist in the aggregate map, add it and its edge weight to the map
                if (!allBooksNeighbors.containsKey(book)) {
                    allBooksNeighbors.put(book, bookNeighbors.get(book));
                    //if it already exists, add its edge weight to the existing edge weight in the aggregate map
                } else {
                    allBooksNeighbors.put(book, allBooksNeighbors.get(book) + bookNeighbors.get(book));
                }

            }
        }

        // If there are no valid recommendations, return "NONE"
        if (allBooksNeighbors.isEmpty()) {
            return "NONE";
        }

        //convert into a sortable list
        List<Map.Entry<String, Integer>> aggregateHistoryNeighbors = new ArrayList<Map.Entry<String, Integer>>(allBooksNeighbors.entrySet());


        //use Collections.sort method to sort neighbors by descending cumulative edge weight, then alphabetically by book ID
        Collections.sort(aggregateHistoryNeighbors, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> i, Map.Entry<String, Integer> j) {
                //compare the book weights and sort by descending order
                if (!i.getValue().equals(j.getValue())) {
                    return j.getValue().compareTo(i.getValue());
                }
                //if the values are equal, sort alphabetically by bookID
                return i.getKey().compareTo(j.getKey());

            }
        });

        //placeholder for top results to be converted to a string
        String topResults = "";
        //must return up to 5 results
        int limit = Math.min(aggregateHistoryNeighbors.size(), 5);

        //building the string of neighboring books separated by comma
        for (int entry = 0; entry < limit; entry++) {
            topResults += aggregateHistoryNeighbors.get(entry).getKey();
            //unless entry is the last item, add a comma after it in the string
            if (entry != limit - 1) {
                topResults += ",";
            }
        }
        //return the final string
        return topResults;
    }

    /**
     * Builds user-based bipartite graph from given csv file.
     * In this graph, userToBooks maps each user to the set of books liked by that user
     * and booktoUsers maps each book to the set of users who liked that book.
     *
     * @param fileName the name of the CSV file containing lines in format User_ID, Book_ID
     */
    public void buildUserBasedGraph(String fileName) {
        //set bookToUsers map to build from scratch
        this.bookToUsers = new HashMap<String, Set<String>>();
        //use readCSV method to use CSV to build map from users to books they liked
        this.userToBooks = this.readCSV(fileName);

        //go through each user in the userToBooks map
        for (String userID : userToBooks.keySet()) {
            //create an arraylist with user's set of liked books
            ArrayList<String> books = new ArrayList<String>(userToBooks.get(userID));
            //for each book liked by this user
            for (String bookID : books) {
                //if book not already in bookToUser map, add it with an empty set as value
                if (!this.bookToUsers.containsKey(bookID)) {
                    this.bookToUsers.put(bookID, new HashSet<>());
                }
                //add this user to the set of users who liked the book
                this.bookToUsers.get(bookID).add(userID);
            }
        }
    }

    /**
     * Recommends up to 5 books for a given user using user-based collaborative filtering.
     * This method finds users who share at least one liked book with the target user,
     * computes Jaccard score for these users and keeps the top 5.
     * Then, scores these users books using score B and returns top 5 books.
     * @param userID the target user ID
     * @return a comma-separated list of up to 5 recommended book IDs, or "NONE" if
     * no recommendations can be made.
     */
    public String tasteTwins(String userID) {
        //if target user does not exist, return NONE
        if (!this.userToBooks.containsKey(userID)) {
            return "NONE";
        }

        //store all booked liked by target user
        Set<String> targetBooks = this.userToBooks.get(userID);

        //store all users who have at least one book in common with target user
        Set<String> similarUsers = new HashSet<>();

        //for each book liked by target user
        for (String book : this.userToBooks.get(userID)) {
            //look at all users who liked that same book
            for (String similarUser : this.bookToUsers.get(book)) {
                //do not include target user
                if (!similarUser.equals(userID)) {
                    //add similar user to similarUsers HashSet
                    similarUsers.add(similarUser);
                }
            }
        }

        //if there's no overlapping users, return NONE
        if (similarUsers.isEmpty()) {
            return "NONE";
        }

        //map each similar user to their Jaccard score
        HashMap<String, Double> userJaccardScore = new HashMap<>();

        //compute Jaccard similarity for each similar user
        for (String similarUser : similarUsers) {
            //get books each similar user likes
            Set<String> otherBooks = this.userToBooks.get(similarUser);
            //placeholder to count number of common books liked
            int intersection = 0;
            //for each overlapping book, increment intersection
            for (String targetBook : targetBooks) {
                if (otherBooks.contains(targetBook)) {
                    intersection++;
                }
            }
            //count total unique books liked by either user
            int union = targetBooks.size() + otherBooks.size() - intersection;
            //calculate Jaccard score
            double jaccardScore = (double) intersection / union;
            //place Jaccard score as value for similarUser in hash map
            userJaccardScore.put(similarUser, jaccardScore);
        }

        //create a list from userJaccardScore to sort
        List<Map.Entry<String, Double>> sortedUserJaccard = new ArrayList<>(userJaccardScore.entrySet());

        //sort users by descending Jaccard score, then alphabetically by user ID
        Collections.sort(sortedUserJaccard, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                //sort descending order
                if (!o1.getValue().equals(o2.getValue())) {
                    return o2.getValue().compareTo(o1.getValue());
                }
                //sort alphabetically if score same
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        //placeholder for top results to be converted to an ArrayList
        ArrayList<String> topResults = new ArrayList<>();
        //must return up to 5 results
        int limit = Math.min(sortedUserJaccard.size(), 5);

        //for each of the up to 5 results, add user to ArrayList
        for (int entry = 0; entry < limit; entry++) {
            topResults.add(sortedUserJaccard.get(entry).getKey());
        }

        //to store each candidate book and how many top twins liked it
        Map<String, Integer> candidateBooks = new HashMap<>();

        //loop through each top taste twin
        for (String user : topResults) {
            //go through each book that twin liked
            for (String bookID : this.userToBooks.get(user)) {
                //consider only if book not already liked by target user
                if (!targetBooks.contains(bookID)) {
                    //if book is new, start count at 1
                    if (!candidateBooks.containsKey(bookID)) {
                        candidateBooks.put(bookID, 1);
                        //otherwise increment existing count
                    } else {
                        candidateBooks.put(bookID, candidateBooks.get(bookID) + 1);
                    }
                }
            }
        }

        //if no candidate books exist, return NONE
        if (candidateBooks.isEmpty()) {
            return "NONE";
        }

        //store each candidate book and its final score B
        Map<String, Double> bookBScores = new HashMap<>();
        //go through each candidate book to calculate score B
        for (String bookID : candidateBooks.keySet()) {
            //number of top twins who liked this book
            int tasteTwinsB = candidateBooks.get(bookID);
            //total number of users who liked this book
            int overallUsersB = this.bookToUsers.get(bookID).size();
            //score b
            double scoreB = (double) tasteTwinsB / overallUsersB;
            //put scoreB as value of respective book
            bookBScores.put(bookID, scoreB);
        }

        //convert score b map into list to be sorted
        List<Map.Entry<String, Double>> bookBScoresSorted = new ArrayList<Map.Entry<String, Double>>(bookBScores.entrySet());

        //sort in descending order, then alphabetical by book ID
        Collections.sort(bookBScoresSorted, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> i, Map.Entry<String, Double> j) {
                //compare the scores and sort by descending order
                if (!i.getValue().equals(j.getValue())) {
                    return j.getValue().compareTo(i.getValue());
                }
                //if the values are equal, sort alphabetically by bookID
                return i.getKey().compareTo(j.getKey());

            }

        });

        //placeholder for top results to be converted to a string
        String topCandidateBooks = "";

        //must return up to 5 results
        limit = Math.min(bookBScoresSorted.size(), 5);

        //building the string of top B score books separated by comma
        for (int entry = 0; entry < limit; entry++) {
            topCandidateBooks += bookBScoresSorted.get(entry).getKey();
            //unless entry is the last item, add a comma after it in the string
            if (entry != limit - 1) {
                topCandidateBooks += ",";
            }
        }
        //return the final string
        return topCandidateBooks;
    }

    /**
     *
     * @param sourceBookID
     * @param targetBookID
     * @return
     */
    public String genreHopper(String sourceBookID, String targetBookID) {
        ArrayList<Integer> edgeWeights = new ArrayList<>();
        for (String bookID : this.coLikeGraph.keySet()) {
            Map<String, Integer> bookNeighbors = this.coLikeGraph.get(bookID);
            for (String book : bookNeighbors.keySet()) {
                if (bookID.compareTo(book) < 0) {
                    edgeWeights.add(bookNeighbors.get(book));
                }
            }
        }

        if (edgeWeights.isEmpty()) {
            return "NONE";
        }

        edgeWeights.sort(Comparator.naturalOrder());

        int medianWeight = edgeWeights.get(edgeWeights.size() / 2);

        HashMap<String, Set<String>> filteredBookToBooks = new HashMap<>();

        for (String bookID : this.coLikeGraph.keySet()) {
            Map<String, Integer> bookNeighbors = this.coLikeGraph.get(bookID);
            if(!filteredBookToBooks.containsKey(bookID)) {
                filteredBookToBooks.put(bookID, new HashSet<>());
            }
            for (String book : bookNeighbors.keySet()) {
                if (bookNeighbors.get(book) >= medianWeight) {
                    if(!filteredBookToBooks.containsKey(book)) {
                        filteredBookToBooks.put(book, new HashSet<>());
                    }
                    filteredBookToBooks.get(bookID).add(book);
                    filteredBookToBooks.get(book).add(bookID);
                }
            }

        }

        if (!filteredBookToBooks.containsKey(sourceBookID) || !filteredBookToBooks.containsKey(targetBookID)) {
            return "NONE";
        }

        //tracks which books are already visited
        Set<String> visited = new HashSet<>();
        //tracks for each book, where it came from
        Map<String,String> previous =  new HashMap<>();
        //queue to enable BFS
        Deque<String> queue = new ArrayDeque<>();


        if(sourceBookID.equals(targetBookID)) {
            return sourceBookID;
        }
        visited.add(sourceBookID);
        queue.offer(sourceBookID);

        while(!queue.isEmpty()) {
            String currentBook = queue.poll();

            for (String neighbor : filteredBookToBooks.get(currentBook)) {
                previous.put(neighbor, currentBook);
                visited.add(neighbor);
                queue.offer(neighbor);
                    }

                }

        return "NONE";

            }
    //main method
    public static void main(String[] args) {

    }
        }








