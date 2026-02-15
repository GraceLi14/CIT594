
/**
 * An unbalanced Binary Search Tree storing items of type T.
 * Used as the bucket data structure for the hash table.
 *
 * @param <T> the item type (must be Comparable for BST ordering)
 */
public class MyTree <T extends Comparable<T>> {

    //fields
    private MyNode<T> root;
    private int size;

    //constructor
    public MyTree() {
        this.root = null;
        this.size = 0;
    }

    //getters

    /**
     * Returns the root node of the binary search tree.
     *
     * If the tree is empty, this method returns null.
     *
     * @return the root node of the tree, or null if the tree is empty
     */
    public MyNode<T> getRoot() {
        // Simply return the root reference.
        // If the tree is empty, root will be null.
        return this.root;
    }

    /**
     * Returns the number of items currently stored in this tree.
     *
     * @return the number of nodes in the tree
     */

    public int size() {
        return this.size;
    }

    //methods

    /**
     * Inserts the specified item into the BST if not already present.
     * Maintains standard BST ordering rules.
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

        //Create a node with item in it
        MyNode<T> insertNode = new MyNode<>(item);

        //Case 1: Tree is empty so new node becomes root
        if (this.root == null) {
            this.root = insertNode;
            this.size++;
            return insertNode;
        }

        //Start traversal from root
        MyNode<T> current = this.root;

        //Traverse tree until insertion point or duplicate is found
        while (current != null) {
            //Compare item to current node's value
            int compareItems = item.compareTo(current.getItem());

            //Case 2: Item is smaller so go left
            if (compareItems < 0) {
                //If left child is empty, insert there
                if (current.getLeft() == null) {
                    current.setLeft(insertNode); //Attach as left child
                    insertNode.setParent(current); //Set parent pointer
                    this.size++; //Increment tree size
                    return insertNode; //Return new node
                }
                //Otherwise continue traversal to left subtree
                current = current.getLeft();

            //Case 3: Item is larger so go right
            } else if (compareItems > 0) {
                //If right child is empty, insert there
                if (current.getRight() == null) {
                    current.setRight(insertNode); //Attach as right child
                    insertNode.setParent(current); //Set parent pointer
                    this.size++; //Increment tree size
                    return insertNode; //Return new node
                }
                //Otherwise continue traversl to right subtree
                current = current.getRight();

            //Case 4; Duplicate item found so no insertion
            } else {
                return current; //Return existing node without changing size
            }
        }
        //Unreachable in correct logic
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
    public MyNode<T> contains(T item) {

        // If item is null, throw IllegalArgumentException
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }

        //Start traversal from root of the tree
        MyNode<T> current = this.root;

        //Traverse the tree until the item is found or a null pointer is reached
        while (current != null) {
            int compareItems = item.compareTo(current.getItem());

            //Compare target item with current node's item, if target smaller, move to left subtree
            if (compareItems < 0) {
                current = current.getLeft();

                //Compare target item with current node's item, if target larger, move to right subtree
            } else if (compareItems > 0) {
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
     * Removes item from this MyTree if it exists.
     *
     * Leaf: detach from parent (or clear root).
     * One child: replace node with its child
     * Two children: replace node with its in-order successor (leftmost of right subtree)
     * and rewire pointers to remove the successor from its old position.
     *
     * @param item the item to remove
     * @return true if the item was found and removed; false otherwise
     * @throws IllegalArgumentException if item is null
     */
    public boolean remove(T item) {
        //if item invalid, throw IllegalArgumentException error
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }

        //Perform a single search to locate node within tree that contains the item
        MyNode<T> current = this.contains(item);

        //If not found, nothing to remove and return false
        if (current == null) {
            return false;
        }


        //Case 1: current has no children i.e. Leaf Node
        if(current.getLeft() == null &&  current.getRight() == null){
            //Identify parent
            MyNode<T> parent = current.getParent();

            //If parent is null, means current is the root leaf
            if(parent  == null){
                //remove root
                this.root = null;
            //If current is left child
            } else if(parent.getLeft() == current){
                parent.setLeft(null);
            }
            //Or else current must be right child
            else {
                parent.setRight(null);
            }

            //Detach current completely from parent and overall tree
            current.setParent(null);

            //Decrement size after deletion
            this.size--;

            //Since item is removed, return true
            return true;

        }

        //Case 2: current has one child

        //Exactly one child left or right so XOR condition
        if(current.getLeft() == null ^ current.getRight() == null){

            //Identify non-null child
            MyNode<T> child = (current.getLeft() != null) ? current.getLeft() : current.getRight();
            //Identify parent
            MyNode<T> parent = current.getParent();

            //If removing root, child becomes new root
            if(parent  == null){
                this.root = child;
                //New root has no parent
                child.setParent(null);
                }
            //If current is left child of its parent
            else if(parent.getLeft() == current){
                //Update parent's left pointer to current's child
                parent.setLeft(child);
                //Update child's parent pointer to parent
                child.setParent(parent);

                }
            //Or else current is right child of its parent
            else {
                    //Update parent's right pointer to current's child
                    parent.setRight(child);
                    //Update child's parent pointer to parent
                    child.setParent(parent);
                }

            //Fully detach current
            current.setParent(null);
            current.setLeft(null);
            current.setRight(null);

            //Decrement size after deletion
            this.size--;

            //Since item is removed, return true
            return true;


            }

        //Case 3: current has two children

        //Current has both left and right children
        if(current.getRight() != null && current.getLeft() != null){

            //Step 1: Find in-order successor, leftmost of right subtree
            MyNode<T> succ = current.getRight();
            //Traverse left until null
            while(succ.getLeft() != null){
                succ = succ.getLeft();
            }

            //Store successor's parent
            MyNode<T> succParent = succ.getParent();

            //Step 2: Detach successor from original location, take over current's right subtree
            // Only necessary if successor is NOT direct right child
            if(succParent != current){
                //Replace successor in its original parent with successor's right child
                succParent.setLeft(succ.getRight());

                //If successor had right child, update child's parent pointer
                if(succ.getRight() != null ){
                    succ.getRight().setParent(succParent);
                }

                //Successor now takes over current's right subtree
                succ.setRight(current.getRight());
                //Update right subtree's parent pointer
                current.getRight().setParent(succ);

            }

            //Step 3: Successor takes over current's left subtree
            succ.setLeft(current.getLeft());

            //Update left subtree's parent pointer
            current.getLeft().setParent(succ);


            //Step 4: Replace current with successor
            //Identify current's parent
            MyNode<T> parent = current.getParent();

            //If removing root
            if (parent == null) {
                //set new root as successor
                this.root = succ;
                //set successor parent as null
                succ.setParent(null);
            }
            //If current was left child
            else if (parent.getLeft() == current) {
                //Set successor parent as current's parent
                succ.setParent(parent);
                //Set left of parent as successor
                parent.setLeft(succ);
            }
            //If current was right child
            else{
                //Set successor parent as current's parent
                succ.setParent(parent);
                //Set left of parent as successor
                parent.setRight(succ);
            }

            //Fully detach current
            current.setParent(null);
            current.setLeft(null);
            current.setRight(null);

            //Decrement size after deletion
            this.size--;

            //Since item removed, return true
            return true;
        }

        //Should never reach here
        return false;
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

}
