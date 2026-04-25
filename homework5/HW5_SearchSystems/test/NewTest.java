import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

public class NewTest {

    @Test
    public void testSplitWhenNewWordIsPrefixOfExistingWord() {
        // Tests the split case where the inserted word ends inside an existing compressed node.
        // This matters because the prefix node must be marked terminal.
        AutocompleteService service = new AutocompleteService(Arrays.asList("apple", "app"));

        List<String> results = service.finish("app", 10);

        assertTrue(results.contains("app"));
        assertTrue(results.contains("apple"));
    }

    @Test
    public void testSplitPreservesOldChildTerminalStatusAndChildren() {
        // Tests that splitting a compressed node does not lose the old node's terminal status or children.
        // This matters because words already in the trie should still be found after a split.
        AutocompleteService service = new AutocompleteService(Arrays.asList("apple", "application", "app"));

        List<String> results = service.finish("appl", 10);

        assertTrue(results.contains("apple"));
        assertTrue(results.contains("application"));
    }

    @Test
    public void testPartialPrefixInsideCompressedNodeReturnsFullWord() {
        // This matters because if the prefix ends inside a compressed node like "index",
        // DFS still needs to start with "index", not just the typed prefix "in".
        AutocompleteService service = new AutocompleteService(Arrays.asList("index"));

        List<String> results = service.finish("in", 10);

        assertEquals(Arrays.asList("index"), results);
    }

    @Test
    public void testCommonPrefixStopsAtFirstMismatch() {
        // Tests that partially similar words do not get incorrectly grouped after a mismatch.
        // This matters because commonPrefix should only count continuous matching characters from the start.
        AutocompleteService service = new AutocompleteService(Arrays.asList("abc", "axc"));

        List<String> results = service.finish("ab", 10);

        assertEquals(Arrays.asList("abc"), results);
        assertFalse(results.contains("axc"));
    }

    @Test
    public void testNoMatchAfterFirstCharacterReturnsEmpty() {
        // Tests when the first character matches a branch, but the rest of the prefix does not.
        // This matters because autocomplete should not return words from a branch unless the full prefix matches.
        AutocompleteService service = new AutocompleteService(
                Arrays.asList("apple", "application")
        );

        List<String> results = service.finish("az", 10);

        assertTrue(results.isEmpty());
    }

    @Test
    public void testCommonLettersInMiddleDoNotCountAsPrefixMatch() {
        // Tests that words sharing letters later in the word are not treated as sharing a prefix.
        // This matters because commonPrefix should stop at the first mismatch, not count later matching letters.
        AutocompleteService service = new AutocompleteService(
                Arrays.asList("cat", "mat")
        );

        List<String> results = service.finish("ca", 10);

        assertTrue(results.contains("cat"));
        assertFalse(results.contains("mat"));
    }

    @Test
    public void testSplitPreservesLongerPhrasesReachable() {
        // Tests that splitting a compressed trie node preserves longer phrase completions.
        // This matters because autocomplete must work for multi-word product names, not just single words.
        AutocompleteService service = new AutocompleteService(
                Arrays.asList("cat food bowl", "cat food mat", "cat")
        );

        List<String> results = service.finish("cat food", 10);

        assertTrue(results.contains("cat food bowl"));
        assertTrue(results.contains("cat food mat"));
        assertFalse(results.contains("cat"));
    }

    @Test
    public void testPartialOverlapCreatesSeparateBranches() {
        // Tests two words that share only part of a compressed node before diverging.
        // This matters because the trie must split into separate branches after the shared prefix.
        AutocompleteService service = new AutocompleteService(
                Arrays.asList("flower", "flowchart")
        );

        List<String> results = service.finish("flow", 10);

        assertTrue(results.contains("flower"));
        assertTrue(results.contains("flowchart"));
    }

}