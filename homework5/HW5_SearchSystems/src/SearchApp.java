import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * SearchApp — HTTP entry point for the ScaleSearch platform.
 *
 * Starts a server on port 8080 and routes requests to SearchService.
 *
 * Endpoints:
 *   GET /                          frontend/index.html
 *   GET /search.js                 frontend/search.js
 *   GET /search?q=<query>          full-text search results (JSON array)
 *   GET /autocomplete?q=<prefix>   autocomplete suggestions (JSON array)
 *   GET /spellcheck?q=<word>       spelling suggestions (JSON array)
 *   GET /history                   recent searches (JSON array)
 *
 * Usage:
 *   javac src/*.java -d out
 *   java -cp out SearchApp <data_file_path>
 *
 * Then open http://localhost:8080
 */
public class SearchApp {

    private static final int PORT = 8080;

    public static void main(String[] args){
        String dataFile = (args.length > 0) ? args[0] : "data/small_dataset.txt";

        System.out.println("=== ScaleSearch ===");
        System.out.println("Loading dataset: " + dataFile);

        List<String> words;
        try {
            words = DataLoader.loadWordList(dataFile);
        } catch (IOException e) {
            System.err.println("Could not load: " + dataFile);
            System.exit(1);
            return;
        }

        System.out.println("Loaded " + words.size() + " entries.");
        System.out.println("Initializing...");
        long t0 = System.currentTimeMillis();

        SearchService service = new SearchService(words, words);

        System.out.println("Ready in " + (System.currentTimeMillis() - t0) + " ms.");
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/", new StaticHandler("frontend/index.html", "text/html"));
            server.createContext("/search.js", new StaticHandler("frontend/search.js", "application/javascript"));
            server.createContext("/search", new SearchHandler(service));
            server.createContext("/autocomplete", new AutocompleteHandler(service));
            server.createContext("/spellcheck", new SpellCheckHandler(service));
            server.createContext("/history", new HistoryHandler(service));
            server.start();
            
            System.out.println("Listening at http://localhost:" + PORT);
        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: Could not start HTTP server on port " + PORT);
            System.err.println("Details: " + e.getMessage());
        }

    }

    // -----------------------------------------------------------------------
    // Handlers
    // -----------------------------------------------------------------------

    static class StaticHandler implements HttpHandler {
        private final String path;
        private final String contentType;

        StaticHandler(String path, String contentType) {
            this.path = path;
            this.contentType = contentType;
        }

        @Override
        public void handle(HttpExchange ex) throws IOException {
            Path file = Paths.get(path);
            if (!Files.exists(file)) {
                send(ex, 404, "text/plain", "Not found: " + path);
                return;
            }
            byte[] bytes = Files.readAllBytes(file);
            ex.getResponseHeaders().set("Content-Type", contentType);
            ex.sendResponseHeaders(200, bytes.length);
            try (OutputStream out = ex.getResponseBody()) {
                out.write(bytes);
            }
        }
    }

    static class SearchHandler implements HttpHandler {
        private final SearchService service;

        SearchHandler(SearchService service) { this.service = service; }

        @Override
        public void handle(HttpExchange ex) throws IOException {
            cors(ex);
            if (preflight(ex)) return;
            String query = param(ex.getRequestURI(), "q");
            List<String> results = service.search(query);
            List<String> limited = results.subList(0, Math.min(50, results.size()));
            send(ex, 200, "application/json", json(limited));
        }
    }

    static class AutocompleteHandler implements HttpHandler {
        private final SearchService service;

        AutocompleteHandler(SearchService service) { this.service = service; }

        @Override
        public void handle(HttpExchange ex) throws IOException {
            cors(ex);
            if (preflight(ex)) return;
            String prefix = param(ex.getRequestURI(), "q");
            List<String> results = service.autocomplete(prefix, 10); // calls autocomplete with limit 10
            send(ex, 200, "application/json", json(results));
        }
    }

    static class SpellCheckHandler implements HttpHandler {
        private final SearchService service;

        SpellCheckHandler(SearchService service) { this.service = service; }

        @Override
        public void handle(HttpExchange ex) throws IOException {
            cors(ex);
            if (preflight(ex)) return;
            String word = param(ex.getRequestURI(), "q");
            List<String> results = service.spellCheck(word, 5);
            send(ex, 200, "application/json", json(results));
        }
    }

    static class HistoryHandler implements HttpHandler {
        private final SearchService service;

        HistoryHandler(SearchService service) { this.service = service; }

        @Override
        public void handle(HttpExchange ex) throws IOException {
            cors(ex);
            if (preflight(ex)) return;
            List<String> history = service.getRecentSearches();
            send(ex, 200, "application/json", json(history));
        }
    }

    // -----------------------------------------------------------------------
    // Utilities
    // -----------------------------------------------------------------------

    private static String param(URI uri, String key) {
        String raw = uri.getRawQuery();
        if (raw == null) return "";
        for (String part : raw.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                return kv[1].replace("+", " ");
            }
        }
        return "";
    }

    private static void cors(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
    }

    private static boolean preflight(HttpExchange ex) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    private static void send(HttpExchange ex, int code, String type, String body)
            throws IOException {
        byte[] bytes = body.getBytes();
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static String json(List<String> items) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            sb.append("\"").append(items.get(i).replace("\\", "\\\\").replace("\"", "\\\"")).append("\"");
            if (i < items.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
