package dataapi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * DataAPIServer
 * Serves raw JSON from existing SQLite databases.
 * Returns an Incident JSON array with nested attributs that aids Object Oriented Programming
 * ClassAPI now has to create objects of perpetrators and victims (which are child classes of person.java)
 * This allows computations to be done on real objects instead of simply extracting data from a flat table
 * It's more scalable and also more easily malleable (such as creating a new attribute *totalVicMinor* in ClassAPI for example) for
 * updated features without having to wrangle data every time
 */

public class DataAPIServer {
    
    public static void execute() throws Exception {

        String port = Config.PORT + "";
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite driver loaded successfully.");
        } 
        catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }

        System.out.println("Starting DataAPI...");
        System.out.println("Working dir: " + System.getProperty("user.dir"));
        System.out.println("Project root: " + Config.PROJECT_ROOT);
        System.out.println("Incidents DB URL: " + Config.INCIDENTS_DB_URL);

        HttpServer server = HttpServer.create(new InetSocketAddress(Config.PORT), 0);
        ObjectMapper mapper = new ObjectMapper();

        //endpoints 
        // endpoint GET /data/incidents (joined + nested JSON)
        server.createContext("/data/incidents", exchange ->
                IncidentDataFetcher.serveCombinedIncidents(exchange, mapper));

        // endpoint GET /data/perpetrators
        server.createContext("/data/perpetrators", exchange ->
                serveAllRowsAsJson(exchange, Config.PERPETRATORS_DB_URL, "perpetrators", mapper));

        // endpoint GET /data/victims
        server.createContext("/data/victims", exchange ->
                serveAllRowsAsJson(exchange, Config.VICTIMS_DB_URL, "victims", mapper));

        // endpoint GET /data/temperature
        server.createContext("/data/temperature", exchange ->
                serveAllRowsAsJson(exchange, Config.TEMPERATURE_DB_URL, "TEMPERATURE_RAW", mapper));

        server.start();

        System.out.println("DataAPI running on port " + Config.PORT + " or 9080 (APISIX)");
        System.out.println("   - http://localhost:" + System.getenv().getOrDefault("PORT", port) + "/data/incidents");
        System.out.println("   - http://localhost:" + System.getenv().getOrDefault("PORT", port) + "/data/perpetrators");
        System.out.println("   - http://localhost:" + System.getenv().getOrDefault("PORT", port) + "/data/victims");
        System.out.println("   - http://localhost:" + System.getenv().getOrDefault("PORT", port) + "/data/temperature");


      //  System.out.println("   - http://localhost:" + Config.PORT + "/data/perpetrators");
       // System.out.println("   - http://localhost:" + Config.PORT + "/data/victims");
       // System.out.println("   - http://localhost:" + Config.PORT + "/data/temperature");
    }

    // returns all rows from the given database table as json for the dataapi endpoints
    private static void serveAllRowsAsJson(HttpExchange exchange, String dbUrl, String table, ObjectMapper mapper)
            throws IOException {

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + ";")) {

            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    row.put(md.getColumnName(i), rs.getObject(i));
                }
                rows.add(row);
            }

        } catch (SQLException e) {
            System.err.println("Query failed for " + table + ": " + e.getMessage());
            sendText(exchange, 500, "Database query failed: " + e.getMessage());
            return;
        }

        byte[] json = mapper.writeValueAsBytes(rows);
        sendJson(exchange, json);
    }

    // sends json to class api
    private static void sendJson(HttpExchange exchange, byte[] json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json);
        }
    }

    // sends text for error management
    private static void sendText(HttpExchange exchange, int code, String msg) throws IOException {
        byte[] data = msg.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(code, data.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(data);
        }
    }
}
