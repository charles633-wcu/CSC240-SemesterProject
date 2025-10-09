package csc240;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;




/**
 * Hello world!
 *
 */
public class WIKIapodApp{
  private static final String DB_URL = "jdbc:sqlite:apodwiki.db";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        createTable();

        // Fetch APOD data
        Apod apod = Apod.fetchApod();

        // Extract a celestial object (simple example: "Mars" if mentioned)
        String keyString = extractkeyString(apod.explanation);

        //  Fetch Wikipedia summary
        WikiSummary wiki = keyString != null ? WikiSummary.fetchWikiSummary(keyString) : null;

        // Store into SQLite
        storeData(apod, keyString, wiki);

        System.out.println("Extracted object: " + keyString);
        if (wiki != null) System.out.println("Wiki extract: " + wiki.extract);


        System.out.println("Done! Stored APOD info for: " + apod.title);
    }

    private static String extractkeyString(String explanation) {
        // Simple example: look for "Mars", "Jupiter", "Saturn"
        String[] words = explanation.split("\\s+");
        
        for (String w : words) {
            if (w.matches("[A-Z][a-zA-Z]+") && !w.equals("Ten") && !w.equals("Today")) {
                return w; // first likely celestial object
            }
        }
        return null;
    }

    private static void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS apod_wiki (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    image_url TEXT,
                    title TEXT,
                    object_name TEXT,
                    wiki_summary TEXT
                )
                """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    private static void storeData(Apod apod, String object, WikiSummary wiki) throws SQLException {
        String sql = "INSERT INTO apod_wiki (image_url, title, object_name, wiki_summary) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, apod.url);
            ps.setString(2, apod.title);
            ps.setString(3, object);
            ps.setString(4, wiki != null ? wiki.extract : null);
            ps.executeUpdate();
        }
    }
}

