
package dataapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

//this creates a nested json array that the dataapi serves to classapi, 
//which means the Person Objects are required to be constructed by ClassAPI before sending to UIAPI

public class IncidentDataFetcher {

    public static void serveCombinedIncidents(HttpExchange exchange, ObjectMapper mapper) {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        List<Map<String, Object>> incidents = new ArrayList<>();

        // Adjust these column names to match your actual schema
        String sql = """
            SELECT occur_date, boro, vic_sex, vic_age_group, perp_sex, perp_age_group
            FROM incidents;
        """;

        try (Connection conn = DriverManager.getConnection(Config.INCIDENTS_DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> incident = new LinkedHashMap<>();
                incident.put("occur_date", rs.getString("occur_date"));

                // Nest borough inside location object
                Map<String, Object> location = new LinkedHashMap<>();
                location.put("name", rs.getString("boro"));
                location.put("area", 0.0); // or a lookup 
                incident.put("location", location);



                Map<String, Object> victim = new LinkedHashMap<>();
                victim.put("sex", rs.getString("vic_sex"));
                victim.put("ageRange", rs.getString("vic_age_group"));
                victim.put("date", rs.getString("occur_date"));
                incident.put("victim", victim);

                Map<String, Object> perpetrator = new LinkedHashMap<>();
                perpetrator.put("sex", rs.getString("perp_sex"));
                perpetrator.put("ageRange", rs.getString("perp_age_group"));
                victim.put("date", rs.getString("occur_date"));
                incident.put("perpetrator", perpetrator);

                incidents.add(incident);
            }

            byte[] json = mapper.writeValueAsBytes(incidents);
            sendJson(exchange, json);

        } catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
            sendText(exchange, 500, "Database query failed: " + e.getMessage());
        } catch (Exception e) {
            sendText(exchange, 500, "Error: " + e.getMessage());
        }
    }

    private static void sendJson(HttpExchange exchange, byte[] json) throws Exception {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.length);
        try (var os = exchange.getResponseBody()) { os.write(json); }
    }

    private static void sendText(HttpExchange exchange, int code, String msg) {
        try {
            byte[] data = msg.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(code, data.length);
            try (var os = exchange.getResponseBody()) { os.write(data); }
        } catch (Exception ignored) {}
    }
}
