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
        // Tests when the search prefix ends inside a compressed node label.
        // This matters because DFS must start with the full path, not just the remaining prefix.
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
}