/**
 * A hash table that uses an array of BST buckets for collision handling.
 *
 * @param <T> the item type (must be Comparable for BST ordering)
 */
public class MyHashTable<T extends Comparable<T>>  {

    //fields

    //Array of BST buckets, buckets can be null or a MyTree
    private MyTree<T>[] body;
    //Total number of buckets
    private int capacity;
    //Number of elements stored
    private int size;

    //constructors
    /**
     * Default constructor that initializes capacity to 701 and creates bucket array.
     */
    public MyHashTable(){
        this.capacity = 701;
        this.body = new MyTree[capacity]; //allocates bucket array with capacity
        this.size = 0;

    }

    /**
     * Alternative constructor with custom capacity.
     * @param capacity number of buckets
     */
    public MyHashTable(int capacity){
        this.capacity = capacity;
        this.body = new MyTree[capacity]; //allocates bucket array with capacity
        this.size = 0;
    }

    //methods

    /**
     * Inserts the item into the hash table if not already present.
     * Uses a BST bucket for collisions and returns the node where the item is stored.
     *
     * @param item the item to insert
     * @return the node containing the item (new or existing)
     * @throws IllegalArgumentException if item is null
     */
    public MyNode<T> add(T item) {

        //Throws IllegalArgumentException if null item
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }

        //Computes bucket index for item with hashCode and modulo capacity.
        //Uses bitmask 0x7fffffff to clear sign bit and ensure non-negative value.
        int key = (item.hashCode() & 0x7fffffff) % this.capacity;

        //Create a MyTree (BST) for this bucket if empty
        if (this.body[key] == null) {
            this.body[key] = new MyTree<>();
        }

        //Inserts item into bucket BST, if already exists doesn't add a new node
        //Returns  newly created node containing the item, or the existing node if the item is already present
        return this.body[key].insert(item);

        //Increment size only if new node was added
        if(this.body[key].lastInsertWasNew()){
            this.size++;
        }

    }



    /**
     * Returns the node containing the specified item if it exists in the hash table, or null if not found.
     *
     * The bucket index is computed using hashCode() and constrained to [0, capacity - 1].
     * If the bucket is non-empty, the search uses contains() method from MyTree (BST) stored in that bucket.
     *
     * @param item the item to search for
     * @return the node containing the item, or null if absent
     * @throws IllegalArgumentException if item is null
     */
    public MyNode<T> contains(T item) {
        //Throws IllegalArgumentException is item not valid
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }

        //Computes bucket index for item with hashCode and modulo capacity.
        //Uses bitmask 0x7fffffff to clear sign bit and ensure non-negative value.
        int key = (item.hashCode() & 0x7fffffff) % this.capacity;



        //If bucket is empty, item is not present so return null
        if (this.body[key] == null){
            return null;
        }

        //Uses contains() from MyTree (BST) to find item stored in BST in the bucket
        return this.body[key].contains(item);

    }



}


