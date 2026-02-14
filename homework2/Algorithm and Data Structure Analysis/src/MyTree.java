import java.util.ArrayList;

/**
 *
 * @param <T>
 */
public class MyTree <T extends Comparable<T>> {
    //fields

    private MyNode<T> root;

    //constructor
    public MyTree() {
        this.root = null;
    }

    //methods

    /**
     * Inserts the specified item into the binary search tree.
     * <p>
     * The tree maintains the BST ordering property:
     * - All values in the left subtree are less than the parent node.
     * - All values in the right subtree are greater than the parent node.
     * <p>
     * If the item already exists in the tree, no new node is created
     * and the existing node is returned.
     *
     * @param item the value to insert into the tree
     * @return the newly created node containing the item, or the
     * existing node if the item is already present
     * @throws IllegalArgumentException if item is null
     */

    public MyNode<T> insert(T item) {

        // If item is null, throw IllegalArgumentException
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }

        MyNode<T> insertNode = new MyNode<>(item);

        if (this.root == null) {
            this.root = insertNode;
            return insertNode;
        }

        MyNode<T> current = this.root;

        while (current != null) {

            if (item.compareTo(current.getItem()) < 0) {
                if (current.getLeft() == null) {
                    current.setLeft(insertNode);
                    insertNode.setParent(current);
                    return insertNode;
                }
                current = current.getLeft();

            } else if (item.compareTo(current.getItem()) > 0) {
                if (current.getRight() == null) {
                    current.setRight(insertNode);
                    insertNode.setParent(current);
                    return insertNode;
                }
                current = current.getRight();

            } else {
                return current;
            }
        }
        //unreachable in correct logic
        return null;
    }

    /**
     * Searches BST for the specified item.
     * The search is performed using standard BST ordering rules:
     * -If item smaller than current node, traverse left.
     * -If item larger than current node, traverse right.
     * -If item equal to current node, return that node.
     *
     * @param item the item to search for in the tree
     * @return the MyNode containing the item if found, null if the item is not present
     * @throws IllegalArgumentException if provided item is null
     */
    public MyNode contains(T item) {

        // If item is null, throw IllegalArgumentException
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }

        //Start traversal from root of the tree
        MyNode<T> current = this.root;

        //Traverse the tree until the item is found or a null pointer is reached
        while (current != null) {

            //Compare target item with current node's item, if target smaller, move to left subtree
            if (item.compareTo(current.getItem()) < 0) {
                current = current.getLeft();

                //Compare target item with current node's item, if target larger, move to right subtree
            } else if (item.compareTo(current.getItem()) > 0) {
                current = current.getRight();

                //If comparison==0 (implicit in else) item has been found
            } else {
                return current;
            }
        }
        //If traversal reaches null, the item is not in the tree so return null
        return null;
    }

    /**
     *
     * @param item
     * @return
     */
    public boolean remove(T item) {

        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }

        MyNode<T> current = this.contains(item);

        if (current == null) {
            return false;
        }


        //no children
        if(current.getLeft() == null &&  current.getRight() == null){
            if(current.getParent()  == null){
                this.root = null;
            } else if(current.getParent().getLeft() == current){
                current.getParent().setLeft(null);
            }
            else {
                current.getParent().setRight(null);
            }
            return true;
        }

        //one child

        if(current.getLeft() == null ^ current.getRight() == null){
            if(current.getParent()  == null){
                if(current.getRight() != null){
                    current.setRight(current.getRight());
                } else {
                    current.setLeft(current.getLeft());
                }
                return true;
            } else if(current.getParent().getLeft() == current){
                if(current.getLeft() != null){
                    current.getParent().setLeft(current.getLeft());
                    current.setLeft(null);
                } else{
                    current.getParent().setRight(current.getRight());
                    current.setRight(null);
                }
            } else if(current.getParent().getRight() == current){
                if(current.getLeft() != null){
                    current.getParent().setLeft(current.getLeft());
                    current.setLeft(null);
                }  else{
                    current.getParent().setRight(current.getRight());
                    current.setRight(null);
                }
            }
            return true;
        }




    }

    /**
     *
     *  Recursively performs an in-order traversal (left, node, right)
     * and appends each item to the provided StringBuilder in sorted order.
     * Used internally as a helper for toString().
     *
     * @param node the current node being visited
     * @param sb the StringBuilder accumulating the output
     */
    private void inOrderTraversal(MyNode<T> node, StringBuilder sb){
        //Base case: if current node is null, stop recursion
        if (node == null) {
            return;
        }

        //Recursively traverse left subtree
        inOrderTraversal(node.getLeft(), sb);

        //Visit current node and append its item and ", "
        sb.append(node.getItem());
        sb.append(", ");

        //Recursively traverse right subtree
        inOrderTraversal(node.getRight(), sb);
    }

    /**
     * Returns a string containing the items of the tree using in-order traversal (left, node, right).
     *
     * The returned string is formatted as: "item1, item2, item3"
     *
     * If the tree is empty, this method returns an empty string.
     *
     * @return a comma-separated string of in-order items
     */
    public String toString() {

        //If tree is empty, return empty string
        if (this.root == null) {
            return "";
        }

        //Initialize a StringBuilder for efficient string construction
        StringBuilder sb = new StringBuilder();

        //Perform recursive in-order traversal
        inOrderTraversal( this.root, sb);

        //Remove trailing comma and space
        if (sb.length() >= 2) {
            sb.setLength(sb.length() - 2);
        }

        //Return final formatted string
        return sb.toString();

    }

    /**
     * Returns the root node of the binary search tree.
     *
     * If the tree is empty, this method returns null.
     *
     * @return the root node of the tree, or null if the tree is empty
     */
    public MyNode getRoot() {
        // Simply return the root reference.
        // If the tree is empty, root will be null.
        return this.root;
    }
}
