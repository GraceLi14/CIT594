import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyNodeTest {

    @Test
    void constructor_sets_children_and_parent_to_null() {
        MyNode<String> node = new MyNode<>("hi");
        assertEquals("hi", node.getItem());
        assertNull(node.getLeft());
        assertNull(node.getRight());
        assertNull(node.getParent());
    }

    @Test
    void setters_allow_null_and_overwrite_previous_values() {
        MyNode<Integer> parent = new MyNode<>(10);
        MyNode<Integer> left = new MyNode<>(5);

        parent.setLeft(left);
        assertSame(left, parent.getLeft());

        // overwrite
        MyNode<Integer> newLeft = new MyNode<>(3);
        parent.setLeft(newLeft);
        assertSame(newLeft, parent.getLeft());

        // set to null
        parent.setLeft(null);
        assertNull(parent.getLeft());
    }

    @Test
    void can_link_parent_child_both_directions_manually() {
        MyNode<Integer> parent = new MyNode<>(10);
        MyNode<Integer> child = new MyNode<>(5);

        parent.setLeft(child);
        child.setParent(parent);

        assertSame(child, parent.getLeft());
        assertSame(parent, child.getParent());
    }
}
