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

//    private final HashMap<String, TrieNode prefixTrie;

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
        //stores first letter of each child TrieNode as key and unique suffixes as value
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
     * Function for building a compressed trie out of a given txt
     * @param words are a list of words from a given txt
     * @return the root of the TrieNode
     */
    private TrieNode buildTrie(List<String> words){
        //create root, label is null
        TrieNode root = new TrieNode("");

        //iterate through all words of the txt to build out the compressed trie
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

                //scenario 1: if children of current node does not even start with the letter the remaining word starts with,
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

                //if not a completely new word, find the common prefix length between remaining word and the child TrieNode
                int prefixLength = commonPrefix( remainingWord, child.label);

                //scenario 2: exact match i.e. both remaining word and the relevant node label have the same length as what they overlap in
                if(remainingWord.length() == prefixLength
                && child.label.length() == prefixLength ){
                    //set the node to terminal given entire remaining word is spelled out
                    child.isTerminal = true;
                    //exit out of this word, move onto next word
                    break;
                }

                //scenario 3: child label matches start of remaining word: go deeper
                if(prefixLength == (child.label).length()){
                    //update currentNode to child of currentNode to go deeper
                    currentNode = child;
                    //remove the prefix portion of the remaining word that already matched
                    remainingWord = remainingWord.substring(prefixLength);
                    //stops this iteration of while loop to look at rest of word
                    continue;
                }

                //scenario 4: split scenario i.e. when the remaining word and TrieNode label partially overlap
                //remove the prefix portion of the remaining word that already matched to a portion of the node
                remainingWord = remainingWord.substring(prefixLength);

                //split node label based on prefix and suffix
                String childPrefix = child.label.substring(0, prefixLength);
                String childSuffix = child.label.substring(prefixLength);

                //create a new TrieNode for the new prefix
                TrieNode newNode = new TrieNode(childPrefix);

                //if the entire remaining word exists within the new prefix, make that new node isTerminal to true
                if(remainingWord.isEmpty()){
                    newNode.isTerminal = true;
                }

                //create a new TrieNode for the new suffix
                TrieNode newChild = new TrieNode(childSuffix);
                //update the new suffix with the old node's terminal status and children
                newChild.isTerminal = child.isTerminal;
                newChild.children.putAll(child.children);

                //update the children of the new prefix node to include new suffix node
                newNode.children.put(newChild.label.charAt(0), newChild);

                //if there is more to the word i.e. entire word is not within the new prefix
                if(!remainingWord.isEmpty()){
                    //create a new TrieNode for the remainder of the word
                    TrieNode remainingWordNode = new TrieNode(remainingWord);
                    //update terminal status given end of the word
                    remainingWordNode.isTerminal = true;
                    //add to new prefix children
                    newNode.children.put(remainingWord.charAt(0), remainingWordNode);
                }
                //update currentNode's children to include the new prefix
                currentNode.children.put(newNode.label.charAt(0), newNode);
                //move onto next word
                break;
            }

        }

        //start of the compressed trie
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
            //as soon as the corresponding letters of both words' prefixes are not matching, return the letter number that it's on
            if(remainingWord.charAt(i) != label.charAt(i)){
                return i;
            }
        }

        //if everything of the common length matches, returns common prefix length
        return len;
    }

    /**
     * Returns a list of relevant words spelled with the given prefix.
     * @param prefix the portion of a word typed in already
     * @return List of words that complete the given prefix
     */
    public ArrayList<String> checkPrefixInTrie(String prefix){
        //list of words that complete the prefix given
        ArrayList<String> result = new ArrayList<>();
        //look for words within this trie, starting with the root of this trie
        TrieNode currentNode = this.prefixTrie;
        //letters to be removed after found node for it
        String remainingPrefix = prefix;

        //go deep into trie until all characters of remaining prefix found
        //try to find if a path in the trie exists that matches the prefix
        while(!remainingPrefix.isEmpty()){
            //find the first letter of root node that matches with prefix
            TrieNode child = currentNode.children.get(remainingPrefix.charAt(0));

            //if there are none, trie doesn't contain the prefix
            if (child == null){
                //return an empty array list
                return result;
            }

            //if a portion of prefix lies within relevant node
            if(remainingPrefix.startsWith(child.label)){
                //update current node to dig deeper
                currentNode = child;
                //remove the characters that already matched
                remainingPrefix = remainingPrefix.substring(child.label.length());
            }
            //if a portion of the child label is the prefix
            else if(child.label.startsWith(remainingPrefix)){
                //update current node to dig deeper
                currentNode = child;
                //DFS search from the updated currentNode
                findWordsDFS(currentNode, remainingPrefix, result);
                //return the final results of this search
                return result;
            }
            //though first character matched between relevant TrieNode and prefix, the label didn't
            else {
                //return the empty list
                return result;
            }
        }

        //now matched prefix up to the relevant node, now DFS search through the trie to create final list of words
        findWordsDFS(currentNode,prefix,result);

        //return all recommended words that start with the relevant prefix
        return result;
    }

    /**
     * Helper function to recursively DFS searches through the trie.
     *
     * Adds a word to result whenever current node marks end of a complete word,
     * then continues searching all children branches for longer completions
     * @param currentNode node whose label is most currently visited
     * @param word to be completed through the trie, path so far to reach current Node
     * @param result empty array list to be filled with words that complete the prefix
     * @return list of words that complete the prefix
     */
    public ArrayList<String> findWordsDFS(TrieNode currentNode, String word, ArrayList<String> result){


        //word is complete but there may be additional nodes below it that make additional words
        if(currentNode.isTerminal){
            //add the word build so far
            result.add(word);
        }

        //visit each child node and append child's label to current word until reaches leaf node
        for(char child : currentNode.children.keySet()){
            findWordsDFS(currentNode.children.get(child), word + currentNode.children.get(child).label, result);
        }

        //return final result list after all reachable branches searched
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
     * It simply returns the Trie root
     */
    public TrieNode getTrie() {
        return this.prefixTrie;
    }
}
