public class MyTree <T extends Comparable<T>> {
    //fields

    private MyNode<T> root;

    //constructor
    public MyTree() {
        this.root = null;
    }

    //methods

    /**
     *
     * @param item
     * @return
     */
    public MyNode insert(T item){

        // If item is null, throw IllegalArgumentException
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }

        MyNode<T> node = this.root;


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
}
