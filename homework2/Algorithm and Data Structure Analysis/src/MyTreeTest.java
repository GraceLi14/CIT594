import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyTreeTest {

    // ---------- Helpers ----------
    // Validates parent pointers are consistent for a simple subtree.
    private void assertParentLink(MyNode<Integer> parent, MyNode<Integer> child, boolean isLeft) {
        assertNotNull(parent);
        assertNotNull(child);
        assertSame(parent, child.getParent());
        if (isLeft) assertSame(child, parent.getLeft());
        else assertSame(child, parent.getRight());
    }

    // ---------- Empty tree cases ----------
    @Test
    void new_tree_root_null_and_toString_empty() {
        MyTree<Integer> t = new MyTree<>();
        assertNull(t.getRoot());
        assertEquals("", t.toString());
    }

    @Test
    void contains_on_empty_returns_null() {
        MyTree<Integer> t = new MyTree<>();
        assertNull(t.contains(1));
    }

    @Test
    void remove_on_empty_returns_false() {
        MyTree<Integer> t = new MyTree<>();
        assertFalse(t.remove(1));
    }

    // ---------- Null argument cases ----------
    @Test
    void insert_null_throws() {
        MyTree<Integer> t = new MyTree<>();
        assertThrows(IllegalArgumentException.class, () -> t.insert(null));
    }

    @Test
    void contains_null_throws() {
        MyTree<Integer> t = new MyTree<>();
        assertThrows(IllegalArgumentException.class, () -> t.contains(null));
    }

    @Test
    void remove_null_throws() {
        MyTree<Integer> t = new MyTree<>();
        assertThrows(IllegalArgumentException.class, () -> t.remove(null));
    }

    // ---------- Insert / duplicate / ordering ----------
    @Test
    void insert_root_sets_parent_null() {
        MyTree<Integer> t = new MyTree<>();
        MyNode<Integer> root = t.insert(10);
        assertSame(root, t.getRoot());
        assertNull(root.getParent());
    }

    @Test
    void insert_multiple_establishes_parent_pointers() {
        MyTree<Integer> t = new MyTree<>();
        t.insert(10);
        t.insert(5);
        t.insert(15);

        MyNode<Integer> root = t.getRoot();
        assertEquals(10, root.getItem());
        assertParentLink(root, root.getLeft(), true);
        assertParentLink(root, root.getRight(), false);
    }

    @Test
    void insert_duplicate_returns_existing_node_tree_unchanged() {
        MyTree<Integer> t = new MyTree<>();
        MyNode<Integer> first = t.insert(7);
        MyNode<Integer> second = t.insert(7);
        assertSame(first, second);
        assertEquals("7", t.toString());
    }

    @Test
    void toString_single_item_no_trailing_comma() {
        MyTree<Integer> t = new MyTree<>();
        t.insert(1);
        assertEquals("1", t.toString());
    }

    @Test
    void toString_inorder_sorted_for_unbalanced_tree() {
        MyTree<Integer> t = new MyTree<>();
        // Make it unbalanced (like a linked list)
        t.insert(1);
        t.insert(2);
        t.insert(3);
        t.insert(4);
        assertEquals("1, 2, 3, 4", t.toString());
    }

    // ---------- Remove: leaf ----------
    @Test
    void remove_leaf_left_child() {
        MyTree<Integer> t = new MyTree<>();
        t.insert(10);
        t.insert(5);
        t.insert(15);

        assertTrue(t.remove(5));
        assertNull(t.contains(5));
        assertEquals("10, 15", t.toString());
        assertNull(t.getRoot().getLeft());
    }

    @Test
    void remove_leaf_right_child() {
        MyTree<Integer> t = new MyTree<>();
        t.insert(10);
        t.insert(5);
        t.insert(15);

        assertTrue(t.remove(15));
        assertNull(t.contains(15));
        assertEquals("5, 10", t.toString());
        assertNull(t.getRoot().getRight());
    }

    // ---------- Remove: one child ----------
    @Test
    void remove_node_with_one_left_child_non_root() {
        //      10
        //     /
        //    5
        //   /
        //  2
        MyTree<Integer> t = new MyTree<>();
        t.insert(10);
        t.insert(5);
        t.insert(2);

        assertTrue(t.remove(5));
        assertNull(t.contains(5));

        // 2 should be directly under 10 now
        assertEquals(2, t.getRoot().getLeft().getItem());
        assertSame(t.getRoot(), t.getRoot().getLeft().getParent());

        assertEquals("2, 10", t.toString());
    }

    @Test
    void remove_node_with_one_right_child_non_root() {
        //      10
        //     /
        //    5
        //     \
        //      7
        MyTree<Integer> t = new MyTree<>();
        t.insert(10);
        t.insert(5);
        t.insert(7);

        assertTrue(t.remove(5));
        assertNull(t.contains(5));

        // 7 should be directly under 10 now (as left child)
        assertEquals(7, t.getRoot().getLeft().getItem());
        assertSame(t.getRoot(), t.getRoot().getLeft().getParent());

        assertEquals("7, 10", t.toString());
    }

    @Test
    void remove_root_with_one_child_left() {
        MyTree<Integer> t = new MyTree<>();
        t.insert(10);
        t.insert(5);

        assertTrue(t.remove(10));
        assertEquals(5, t.getRoot().getItem());
        assertNull(t.getRoot().getParent());
        assertEquals("5", t.toString());
    }

    @Test
    void remove_root_with_one_child_right() {
        MyTree<Integer> t = new MyTree<>();
        t.insert(10);
        t.insert(15);

        assertTrue(t.remove(10));
        assertEquals(15, t.getRoot().getItem());
        assertNull(t.getRoot().getParent());
        assertEquals("15", t.toString());
    }

    // ---------- Remove: two children (successor is direct right child) ----------
    @Test
    void remove_two_children_successor_is_direct_right_child() {
        //        10
        //      5    15
        //          /  \
        //        12   20
        //
        // Remove 15: successor is 20? No, successor is 20 if no left; but here left exists (12),
        // so successor is 12 (NOT direct). Let's make direct successor case:
        //
        //        10
        //      5    15
        //            \
        //            20
        // Remove 15: successor is direct right child 20.

        MyTree<Integer> t = new MyTree<>();
        t.insert(10);
        t.insert(5);
        t.insert(15);
        t.insert(20);

        assertTrue(t.remove(15));
        assertNull(t.contains(15));
        assertEquals("5, 10, 20", t.toString());

        // Ensure 20 is right child of 10
        assertEquals(20, t.getRoot().getRight().getItem());
        assertSame(t.getRoot(), t.getRoot().getRight().getParent());
    }

    // ---------- Remove: two children (successor is deeper leftmost) ----------
    @Test
    void remove_two_children_successor_is_leftmost_of_right_subtree() {
        //        10
        //      5    15
        //          /  \
        //        12   20
        //          \
        //          13
        //
        // Remove 15: successor should be 20? Actually successor is 20 if 12 had no left.
        // But successor is leftmost in right subtree: right subtree root is 20 if 15.right=20.
        // Let's use:
        //        10
        //      5    15
        //          /  \
        //        12   20
        // Remove 10 would use successor 12 (leftmost of right subtree).
        //
        MyTree<Integer> t = new MyTree<>();
        t.insert(10);
        t.insert(5);
        t.insert(15);
        t.insert(12);
        t.insert(20);

        assertTrue(t.remove(10));
        assertNull(t.contains(10));
        assertEquals("5, 12, 15, 20", t.toString());

        // Root should now be 12 (successor) and have no parent
        assertEquals(12, t.getRoot().getItem());
        assertNull(t.getRoot().getParent());
    }

    @Test
    void remove_two_children_with_successor_having_right_child() {
        //        10
        //      5    15
        //          /
        //        12
        //          \
        //          13
        //
        // Remove 10: successor is 12, and successor has right child 13
        MyTree<Integer> t = new MyTree<>();
        t.insert(10);
        t.insert(5);
        t.insert(15);
        t.insert(12);
        t.insert(13);

        assertTrue(t.remove(10));
        assertNull(t.contains(10));
        assertEquals("5, 12, 13, 15", t.toString());

        // Root should be 12, and 13 should still exist
        assertEquals(12, t.getRoot().getItem());
        assertNotNull(t.contains(13));
    }

    // ---------- Remove: repeated removals / stability ----------
    @Test
    void remove_same_item_twice_second_time_false() {
        MyTree<Integer> t = new MyTree<>();
        t.insert(1);
        assertTrue(t.remove(1));
        assertFalse(t.remove(1));
        assertNull(t.getRoot());
        assertEquals("", t.toString());
    }

    @Test
    void remove_many_items_tree_remains_valid_inorder() {
        MyTree<Integer> t = new MyTree<>();
        int[] vals = {10, 5, 15, 2, 7, 12, 20, 6, 8};
        for (int v : vals) t.insert(v);

        assertTrue(t.remove(2));   // leaf
        assertTrue(t.remove(7));   // two children (6 and 8)
        assertTrue(t.remove(15));  // two children (12 and 20)
        assertFalse(t.remove(999));

        // Ensure still sorted inorder with removed items gone
        assertEquals("5, 6, 8, 10, 12, 20", t.toString());
    }
}
