/**
 * Represents a single node in a binary search tree.
 *
 * Each node stores:
 * - A data item of generic type T
 * - A reference to its left child
 * - A reference to its right child
 * - A reference to its parent node
 *
 * @param <T> the type of data stored in the node
 */
public class MyNode<T>{

    //fields

    //Data stored in this node
    private T item;
    //reference to left child
    private MyNode<T> left;
    //reference to right child
    private MyNode<T> right;
    //reference to parent node (null if node is the root)
    private MyNode<T> parent;

    //constructor

    /**
     * Constructs a new node containing the specified item.
     * The left, right, and parent references are initialized to null.
     *
     * @param item the data to store in this node
     */
    public MyNode(T item) {
        this.item = item;
        this.left = null;
        this.right = null;
        this.parent = null;
    }

    //getters

    /**
     * Returns the item stored in this node.
     *
     * @return the data contained in this node
     */
    public T getItem(){
        return this.item;
    }

    /**
     * Returns the left child of this node.
     *
     * @return the left child node, or null if none exists
     */
    public MyNode<T> getLeft(){
        return this.left;
    }

    /**
     * Returns the right child of this node.
     *
     * @return the right child node, or null if none exists
     */
    public MyNode<T> getRight(){
        return this.right;
    }

    /**
     * Returns parent of this node.
     * @return
     */
    public MyNode<T> getParent(){
        return this.parent;
    }

    //setters

    /**
     * Sets the left child of this node.
     * Note: This method does not automatically update the child's parent reference.
     *
     * @param input the node to set as the left child
     */
    public void setLeft(MyNode<T> input){
        this.left = input;
    }

    /**
     * Sets the right child of this node.
     * Note: This method does not automatically update the child's parent reference.
     *
     * @param input the node to set as the right child
     */
    public void setRight(MyNode<T> input){
        this.right = input;
    }

    /**
     * Sets the parent of this node.
     *
     * @param input the node to set as the parent
     */
    public void setParent(MyNode<T> input){
        this.parent = input;
    }

}
