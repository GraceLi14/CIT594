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

    //getters

    /**
     * Returns the number of stored items in the hash table
     * @return The integer count of items stored in the hash table
     */
    public int size(){
        return this.size;
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

        int before = this.body[key].size();

        //Inserts item into bucket BST, if already exists doesn't add a new node
        //Returns  newly created node containing the item, or the existing node if the item is already present
        MyNode<T> insertNode = this.body[key].insert(item);

        //Increment size only if new node was added
        if (body[key].size() > before) {
            this.size++;
        }

        //Returns either new node inserted or existing node
        return insertNode;

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

    /**
     * Removes the specified item from the hash table if present.
     *
     * The appropriate bucket is determined using hashCode() and modulo capacity.
     * If the bucket exists, removal is delegated to the BST stored in that bucket.
     *
     * @param item the item to remove
     * @return true if the item was found and removed; false otherwise
     * @throws IllegalArgumentException if item is null
     */
    public boolean remove(T item) {
        //Throws IllegalArgumentException if item is null
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }

        //Computes bucket index for item with hashCode and modulo capacity.
        //Uses bitmask 0x7fffffff to clear sign bit and ensure non-negative value.
        int key = (item.hashCode() & 0x7fffffff) % this.capacity;

        // If the bucket is empty, the item cannot be present
        if (this.body[key] == null) {
            return false;
        }

        //Attempt to remove item from relevant bucket using MyTree (BST) remove() method
        boolean removed = this.body[key].remove(item);

        //If removal attempt failed (item not found), return false
        //To prevent a NullPointerException error
        if(!removed){
            return false;
        }
        //If removal attempt was successful, decrement size
        this.size--;

        //If bucket with MyTree (BST) became empty, reset bucket to null
        if(this.body[key].size() == 0){
            this.body[key] = null;
        }

        //Return true given removal was successful
        return true;
    }

    /**
     * Checks if the hashtable is empty or not i.e. has no elements.
     * @return true if the hash table contains no items; false otherwise
     */
    public boolean isEmpty(){
        return this.size == 0;
    }

    /**
     * Removes all elements from the hash table.
     *
     * Each bucket is set to null, effectively removing all BSTs stored in the table.
     * The overall size counter is reset to zero.
     */
    public void clear(){
        //Iterate through each bucket in the hash table
        for(int i = 0; i < this.body.length; i++){
            //For any bucket that contains MyTree (BST), remove the reference
            if(this.body[i] != null){
                this.body[i] = null;
            }
        }

        //Reset total size of stored elements to zero
        this.size = 0;

    }

    public static void main(String[] args) {
        //Create new MyHashTable
        MyHashTable<String> myHashTable = new MyHashTable<>();

        //Insert various types of Strings and print results
        System.out.println(myHashTable.add("Grace"));
        System.out.println(myHashTable.add("Grace"));
        System.out.println(myHashTable.add("United Explorer!"));
        System.out.println(myHashTable.add("AaAa"));
        System.out.println(myHashTable.add("BBAa"));
        System.out.println(myHashTable.add("AaBB"));
        System.out.println(myHashTable.add("Wow!"));
        System.out.println(myHashTable.add("1820395"));
        System.out.println(myHashTable.add("*&$3abcd"));
        System.out.println(myHashTable.add("Aa"));
        System.out.println(myHashTable.add("BB"));

        //Print size()
        System.out.println("Size: " + myHashTable.size());

        //Print contain() on an existing item
        System.out.println("Contains Grace: " + myHashTable.contains("Grace"));
        //Print contain() on a missing item
        System.out.println("Contains Meep: " + myHashTable.contains("Meep"));

        //Insert an item that already exists and print the result
        System.out.println("Add duplicate Grace: " + myHashTable.add("Grace"));

        //Remove an item and print the result
        System.out.println("Remove BBAa: " + myHashTable.remove("BBAa"));

        //Remove the same item again and print the result
        System.out.println("Remove BBAa again: " + myHashTable.remove("BBAa"));

    }



}


