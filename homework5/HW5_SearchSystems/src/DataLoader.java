import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for loading datasets from disk.
 *
 * Datasets are plain text files with one entry per line.
 * Empty lines and leading/trailing whitespace are ignored.
 *
 */
public class DataLoader {

    /**
     * Loads a word list from the specified file.
     *
     * Each non-empty line becomes one entry in the returned list.
     * All entries are lowercased and trimmed.
     *
     * @param filePath path to the dataset file
     * @return list of words/phrases loaded from the file
     * @throws IOException if the file cannot be read
     */
    public static List<String> loadWordList(String filePath) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty()) {
                    words.add(line);
                }
            }
        }
        return words;
    }

    /**
     * Loads documents (one per line) for use with the InvertedIndex.
     *
     * This is an alias for loadWordList() — both use the same file format.
     *
     * @param filePath path to the dataset file
     * @return list of document strings
     * @throws IOException if the file cannot be read
     */
    public static List<String> loadDocuments(String filePath) throws IOException {
        return loadWordList(filePath);
    }
}
