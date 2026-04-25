import java.util.*;

/**
 * AutocompleteService returns ranked word completions for a given prefix.
 *
 * When a user types a partial query (e.g. "comp"), this service returns
 * words that begin with that prefix (e.g. "compute", "compiler", ...).
 *
 * To make repeated lookups fast, a prefix index is built once at startup
 * that maps every possible prefix to the list of words sharing that prefix.
 * After initialization, each call to finish() is a single map lookup.
 *
 * Results are passed through MLModel for ranking before being returned.
 */
public class AutocompleteService {

//    private final HashMap<String, List<String>> prefixIndex;

    private final MLModel mlModel;
    private final TrieNode prefixTrie;

    public AutocompleteService(List<String> words) {
        this.mlModel = new MLModel();
        this.prefixTrie = buildTrie(words);
    }

    /**
     * Represents a trieNode in a Compressed Trie
     */
    static class TrieNode{
        //string segment stored at this node
        String label;
        //whether at end of a complete word/phrase
        boolean isTerminal;
        //stores prefix and its subsequent unique suffixes
        HashMap<Character, TrieNode> children;

        //constructor
        public TrieNode(String label){
            this.label = label;
            //default is not at end of a word
            this.isTerminal = false;
            this.children = new HashMap<>();
        }
    }

    /**
     *
     * @param words
     * @return
     */
    private TrieNode buildTrie(List<String> words){
        //create root, label is null
        TrieNode root = new TrieNode("");

        for(String word : words){
            //start with root as currentNode
            TrieNode currentNode = root;

            //start with the entire current word as remainingWord
            String remainingWord = word;

            //go through the entire word/phrase till the end
            while(!remainingWord.isEmpty()){

                //first letter of remaining word
                char firstChar = remainingWord.charAt(0);
                //get the currentNode's child with the common first letter as the remaining word
                TrieNode child = currentNode.children.get(firstChar);

                //if children of current node does not even start with the letter the remaining word starts with,
                // create a new trieNode for it

                if(child == null){
                    //create a new TrieNode for the remaining word given there's no existing prefix for it
                    TrieNode newNode = new TrieNode(remainingWord);
                    //marks this new TrieNode as a terminal node given it spells out the remainder of the word
                    newNode.isTerminal = true;
                    //add remaining word to children of current Node
                    currentNode.children.put(remainingWord.charAt(0), newNode);
                    //exit out of this word, move onto next word
                    break;
                }

                int prefixLength = commonPrefix( remainingWord, child.label);

                //exact match
                if(remainingWord.length() == prefixLength
                && child.label.length() == prefixLength ){
                    child.isTerminal = true;
                    break;
                }

                //child label matches start of remaining word: go deeper
                if(prefixLength == (child.label).length()){
                    currentNode = child;
                    remainingWord = remainingWord.substring(prefixLength);
                    continue;
                }
                //split scenario

                remainingWord = remainingWord.substring(prefixLength);
                String childPrefix = child.label.substring(0, prefixLength);
                String childSuffix = child.label.substring(prefixLength);

                TrieNode newNode = new TrieNode(childPrefix);

                if(remainingWord.isEmpty()){
                    newNode.isTerminal = true;
                }

                TrieNode newChild = new TrieNode(childSuffix);
                newChild.isTerminal = child.isTerminal;
                newChild.children.putAll(child.children);

                newNode.children.put(newChild.label.charAt(0), newChild);

                if(!remainingWord.isEmpty()){
                    TrieNode remainingWordNode = new TrieNode(remainingWord);
                    remainingWordNode.isTerminal = true;
                    newNode.children.put(remainingWord.charAt(0), remainingWordNode);
                }

                currentNode.children.put(newNode.label.charAt(0), newNode);
                break;
            }

        }

        return root;

    }

    /**
     * Helper function to count number of common prefix letters
     * @param remainingWord rest of the word being examined for matching currently
     * @param label string of letters stored in the trieNode
     * @return number of continuous common prefix letters both remainingWord and label shares
     */
    private int commonPrefix(String remainingWord, String label){
        //placeholder for commonPrefix count
        int commonPrefix = 0;
        //should only compare prefix up to the greatest common length between the two words
        int len = Math.min(remainingWord.length(), label.length());

        //iterate through each character of both words from beginning to common length end
        for(int i = 0; i < len; i++){
            //if each letter of both words' prefixes are matching, keep incrementing commonPrefix count
            if(remainingWord.charAt(i) != label.charAt(i)){
                return i;
            }
        }

        //returns final common prefix length
        return len;
    }

    /**
     *
     * @param prefix
     * @return
     */
    public ArrayList<String> checkPrefixInTrie(String prefix){
        //list of words that complete the prefix given
        ArrayList<String> result = new ArrayList<>();
        //look for words within this trie
        TrieNode currentNode = this.prefixTrie;
        //letters to be removed after found node for it
        String remainingPrefix = prefix;

        while(!remainingPrefix.isEmpty()){
            TrieNode child = currentNode.children.get(remainingPrefix.charAt(0));

            if (child == null){
                return result;
            }

            if(remainingPrefix.startsWith(child.label)){
                currentNode = child;
                remainingPrefix = remainingPrefix.substring(child.label.length());
            }
            else if(child.label.startsWith(remainingPrefix)){
                currentNode = child;
                findWordsDFS(currentNode, remainingPrefix, result);
                return result;
            }
            else {
                return result;
            }
        }
        //try to find if a path in the trie exists that matches the prefix


        findWordsDFS(currentNode,prefix,result);

        return result;
    }

    /**
     * Helper function.
     * @param currentNode
     * @param word
     * @param result
     * @return
     */
    public ArrayList<String> findWordsDFS(TrieNode currentNode, String word, ArrayList<String> result){


        //word is complete but there may be additional nodes below it that make additional words
        if(currentNode.isTerminal){
            result.add(word);
        }

        for(char child : currentNode.children.keySet()){
            findWordsDFS(currentNode.children.get(child), word + currentNode.children.get(child).label, result);
        }

        return result;
    }


    /**
     * Returns up to maxResults completions for the given prefix, ranked by
     * the ML model.
     *
     * @param prefix     the prefix typed by the user
     * @param maxResults maximum number of suggestions to return
     * @return ordered list of completions
     */
    public List<String> finish(String prefix, int maxResults) {
        List<String> completions = checkPrefixInTrie(prefix);
        List<String> ranked = order(prefix, completions);
        return ranked.subList(0, Math.min(maxResults, ranked.size()));
    }

    /**
     * Ranks completions using the ML model.
     *
     * @param query       the user's current input
     * @param completions the unranked list of completions
     * @return ranked list of completions
     */
    public List<String> order(String query, List<String> completions) {
        return mlModel.rank(completions, query);
    }


    /**
     * This function is used for testing purposes.
     * 
     * It simply returns the prefix index
     */
    public TrieNode getTrie() {
        return this.prefixTrie;
    }
}
