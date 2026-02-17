import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyHashTableTest {

    // Always-colliding comparable item
    private static class Collide implements Comparable<Collide> {
        private final int v;
        Collide(int v) { this.v = v; }

        @Override
        public int hashCode() { return 1; } // constant -> collisions
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Collide)) return false;
            return this.v == ((Collide) o).v;
        }
        @Override
        public int compareTo(Collide other) {
            return Integer.compare(this.v, other.v);
        }
    }

    // Negative hashCode item
    private static class NegHash implements Comparable<NegHash> {
        private final int v;
        NegHash(int v) { this.v = v; }

        @Override
        public int hashCode() { return -123456; }
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof NegHash)) return false;
            return this.v == ((NegHash) o).v;
        }
        @Override
        public int compareTo(NegHash other) {
            return Integer.compare(this.v, other.v);
        }
    }

    // Edge case: equals says "equal" but compareTo says "different"
    // This violates the normal Comparable contract, but it's a GREAT autograder-style stress test.
    private static class WeirdComparable implements Comparable<WeirdComparable> {
        private final int v;
        WeirdComparable(int v) { this.v = v; }

        @Override
        public int hashCode() { return 42; }

        @Override
        public boolean equals(Object o) {
            // Everything is "equal" (bad equals)
            return o instanceof WeirdComparable;
        }

        @Override
        public int compareTo(WeirdComparable other) {
            // Orders by v normally
            return Integer.compare(this.v, other.v);
        }
    }

    @Test
    void new_table_empty() {
        MyHashTable<String> ht = new MyHashTable<>();
        assertTrue(ht.isEmpty());
        assertEquals(0, ht.size());
        assertNull(ht.contains("nope"));
        assertFalse(ht.remove("nope"));
    }

    @Test
    void constructor_capacity_one_works() {
        MyHashTable<String> ht = new MyHashTable<>(1);
        assertTrue(ht.isEmpty());
        ht.add("A");
        ht.add("B");
        assertEquals(2, ht.size());
        assertNotNull(ht.contains("A"));
        assertNotNull(ht.contains("B"));
    }

    @Test
    void null_arguments_throw() {
        MyHashTable<String> ht = new MyHashTable<>();
        assertThrows(IllegalArgumentException.class, () -> ht.add(null));
        assertThrows(IllegalArgumentException.class, () -> ht.contains(null));
        assertThrows(IllegalArgumentException.class, () -> ht.remove(null));
    }

    @Test
    void add_duplicate_does_not_increase_size() {
        MyHashTable<String> ht = new MyHashTable<>();
        ht.add("Grace");
        ht.add("Grace");
        assertEquals(1, ht.size());
    }

    @Test
    void remove_existing_then_contains_null_and_size_updates() {
        MyHashTable<String> ht = new MyHashTable<>();
        ht.add("A");
        ht.add("B");
        assertEquals(2, ht.size());

        assertTrue(ht.remove("A"));
        assertEquals(1, ht.size());
        assertNull(ht.contains("A"));
        assertNotNull(ht.contains("B"));
    }

    @Test
    void remove_missing_returns_false_size_unchanged() {
        MyHashTable<String> ht = new MyHashTable<>();
        ht.add("A");
        assertFalse(ht.remove("Z"));
        assertEquals(1, ht.size());
    }

    @Test
    void remove_same_item_twice_second_false() {
        MyHashTable<String> ht = new MyHashTable<>();
        ht.add("X");
        assertTrue(ht.remove("X"));
        assertFalse(ht.remove("X"));
        assertEquals(0, ht.size());
        assertTrue(ht.isEmpty());
    }

    @Test
    void clear_on_empty_safe_and_idempotent() {
        MyHashTable<String> ht = new MyHashTable<>();
        ht.clear();
        ht.clear();
        assertEquals(0, ht.size());
        assertTrue(ht.isEmpty());
    }

    @Test
    void clear_then_reuse_table() {
        MyHashTable<String> ht = new MyHashTable<>();
        ht.add("A");
        ht.add("B");
        ht.clear();

        assertEquals(0, ht.size());
        assertTrue(ht.isEmpty());
        assertNull(ht.contains("A"));

        // reuse after clear
        ht.add("C");
        assertEquals(1, ht.size());
        assertNotNull(ht.contains("C"));
    }

    // ---------- Collision-heavy tests ----------
    @Test
    void forced_collisions_with_constant_hashcode() {
        MyHashTable<Collide> ht = new MyHashTable<>(7);

        ht.add(new Collide(5));
        ht.add(new Collide(1));
        ht.add(new Collide(9));
        ht.add(new Collide(3));

        assertEquals(4, ht.size());

        assertNotNull(ht.contains(new Collide(5)));
        assertNotNull(ht.contains(new Collide(1)));
        assertNotNull(ht.contains(new Collide(9)));
        assertNotNull(ht.contains(new Collide(3)));

        // remove one, others remain
        assertTrue(ht.remove(new Collide(1)));
        assertEquals(3, ht.size());
        assertNull(ht.contains(new Collide(1)));
        assertNotNull(ht.contains(new Collide(3)));
    }

    @Test
    void collision_bucket_duplicate_should_not_increase_size() {
        MyHashTable<Collide> ht = new MyHashTable<>(3);

        ht.add(new Collide(7));
        ht.add(new Collide(7));
        ht.add(new Collide(7));

        assertEquals(1, ht.size());
        assertNotNull(ht.contains(new Collide(7)));
    }

    @Test
    void remove_last_item_in_collision_bucket_then_bucket_behaves_empty() {
        MyHashTable<Collide> ht = new MyHashTable<>(1);
        ht.add(new Collide(1));

        assertTrue(ht.remove(new Collide(1)));
        assertEquals(0, ht.size());

        // should behave like empty now
        assertNull(ht.contains(new Collide(1)));
        assertFalse(ht.remove(new Collide(1)));
    }

    // ---------- Negative hashCode ----------
    @Test
    void negative_hashcode_items_work() {
        MyHashTable<NegHash> ht = new MyHashTable<>(5);

        ht.add(new NegHash(1));
        ht.add(new NegHash(2));
        assertEquals(2, ht.size());

        assertNotNull(ht.contains(new NegHash(1)));
        assertNotNull(ht.contains(new NegHash(2)));

        assertTrue(ht.remove(new NegHash(1)));
        assertEquals(1, ht.size());
        assertNull(ht.contains(new NegHash(1)));
        assertNotNull(ht.contains(new NegHash(2)));
    }

    // ---------- Weird equals/compareTo contract test ----------
    // Not required by the spec, but catches a lot of subtle implementations.
    @Test
    void weird_equals_compareTo_contract_stress_test() {
        MyHashTable<WeirdComparable> ht = new MyHashTable<>(1);

        // Even though equals() says all are equal, compareTo orders them.
        // A correct BST-based set will treat duplicates based on compareTo == 0.
        ht.add(new WeirdComparable(1));
        ht.add(new WeirdComparable(2));
        ht.add(new WeirdComparable(3));

        // Depending on implementation, size could be 3 (compareTo differs),
        // which is consistent with "ordered using compareTo".
        assertEquals(3, ht.size());

        assertNotNull(ht.contains(new WeirdComparable(1)));
        assertNotNull(ht.contains(new WeirdComparable(2)));
        assertNotNull(ht.contains(new WeirdComparable(3)));

        assertTrue(ht.remove(new WeirdComparable(2)));
        assertEquals(2, ht.size());
    }

    // ---------- Real-world known Java String collision pair ----------
    @Test
    void known_string_hash_collision_pair_in_same_bucket() {
        // "AaAa" and "BBAa" are a known String.hashCode collision pair in Java.
        // Even if not in same bucket with capacity 701, forcing capacity 1 guarantees same bucket.
        MyHashTable<String> ht = new MyHashTable<>(1);

        ht.add("AaAa");
        ht.add("BBAa");

        assertEquals(2, ht.size());
        assertNotNull(ht.contains("AaAa"));
        assertNotNull(ht.contains("BBAa"));

        assertTrue(ht.remove("AaAa"));
        assertEquals(1, ht.size());
        assertNull(ht.contains("AaAa"));
        assertNotNull(ht.contains("BBAa"));
    }

    // ---------- Longer operation sequence ----------
    @Test
    void long_sequence_add_remove_mixed_keeps_size_consistent() {
        MyHashTable<String> ht = new MyHashTable<>(1);

        ht.add("A");
        ht.add("B");
        ht.add("C");
        assertEquals(3, ht.size());

        ht.remove("B");
        assertEquals(2, ht.size());

        ht.add("B");
        assertEquals(3, ht.size());

        ht.remove("A");
        ht.remove("C");
        assertEquals(1, ht.size());

        assertNotNull(ht.contains("B"));
        assertNull(ht.contains("A"));
        assertNull(ht.contains("C"));
    }
}
