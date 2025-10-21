package dataapi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class DataAPIServer {


    public static void main(String[] args) throws Exception {

        System.out.println("Starting DataAPI...");
        DBHelperClass.ensureTable();
        DBHelperClass.clearTable();

    // Populate database
        System.out.println("Populating database with incident + temperature data...");
        List<Map<String, Object>> incidents = IncidentDataFetcher.fetchIncidents();
        List<Map<String, Object>> temps = TemperatureDataFetcher.fetchTemperatureDates();
    
    //merge datasets
        Map<String, Map<String, Object>> daily = aggregateData(incidents, temps);
        DBHelperClass.insertDailyRows(daily.values());
        System.out.println("Inserted " + daily.size() + " records.");

        IncidentDataVerifier.verify(Config.DB_URL);
        TemperatureDataVerifier.verify(Config.DB_URL);

        // Launch server
        startServer();
    }

//server and endpoints
    private static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        ObjectMapper mapper = new ObjectMapper();

//endpoint 1: GET /data/incidents , a full table
        server.createContext("/data/incidents_temp_date", (HttpExchange exchange) -> {
            List<Map<String, Object>> rows = queryAll();
            byte[] json = mapper.writeValueAsBytes(rows);
            sendJson(exchange, json);
        });

//  endpoint 2: GET /data/incidents/{date} 
        server.createContext("/data/incidents/", (HttpExchange exchange) -> {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 4) {
                sendText(exchange, 400, "Usage: /data/incidents/{date}");
                return;
            }
            String date = parts[3];
            Map<String, Object> row = queryOne(date);
            if (row == null) {
                sendText(exchange, 404, "No record for date " + date);
            } else {
                byte[] json = mapper.writeValueAsBytes(row);
                sendJson(exchange, json);
            }
        });

//  endpoint 3 GET /data/temperature/{date}
        server.createContext("/data/temperature/", (HttpExchange exchange) -> {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 4) {
                sendText(exchange, 400, "Usage: /data/temperature/{date}");
                return;
            }
            String date = parts[3];
            Map<String, Object> row = queryOne(date);
            if (row == null) {
                sendText(exchange, 404, "No record for date " + date);
            } else {
                Map<String, Object> temp = new LinkedHashMap<>();
                temp.put("date", row.get("occur_date"));
                temp.put("daily_high_fahrenheit", row.get("daily_high_fahrenheit"));
                byte[] json = mapper.writeValueAsBytes(temp);
                sendJson(exchange, json);
            }
        });

        server.start();
        System.out.println("DataAPI running at:");
        System.out.println("   - http://localhost:8081/data/incidents_temp_date or http://localhost:9080/data/incidents_temp_date");
        System.out.println("   - http://localhost:8081/data/incidents/{date}");
        System.out.println("   - http://localhost:8081/data/temperature/{date}");
    }


    
//helpers

    private static List<Map<String, Object>> queryAll() {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(Config.DB_URL);
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT * FROM DAILY_CRIME_TEMPERATURE ORDER BY occur_date ASC")) {
            while (r.next()) {
                rows.add(extractRow(r));
            }
        } catch (SQLException e) {
            System.err.println("queryAll() failed: " + e.getMessage());
        }
        return rows;
    }

    private static Map<String, Object> queryOne(String date) {
        try (Connection c = DriverManager.getConnection(Config.DB_URL);
             PreparedStatement ps = c.prepareStatement("SELECT * FROM DAILY_CRIME_TEMPERATURE WHERE occur_date = ?")) {
            ps.setString(1, date);
            try (ResultSet r = ps.executeQuery()) {
                if (r.next()) return extractRow(r);
            }
        } catch (SQLException e) {
            System.err.println("queryOne() failed: " + e.getMessage());
        }
        return null;
    }

    private static Map<String, Object> extractRow(ResultSet r) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("occur_date", r.getString("occur_date"));
        row.put("total_incidents", r.getInt("total_incidents"));
        row.put("vic_sex_countMale", r.getInt("vic_sex_countMale"));
        row.put("vic_sex_countFemale", r.getInt("vic_sex_countFemale"));
        row.put("murder_true_count", r.getInt("murder_true_count"));
        row.put("murder_false_count", r.getInt("murder_false_count"));
        row.put("daily_high_fahrenheit", r.getDouble("daily_high_fahrenheit"));
        return row;
    }

    private static void sendJson(HttpExchange exchange, byte[] json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.length);
        exchange.getResponseBody().write(json);
        exchange.close();
    }

    private static void sendText(HttpExchange exchange, int code, String msg) throws IOException {
        byte[] data = msg.getBytes();
        exchange.sendResponseHeaders(code, data.length);
        exchange.getResponseBody().write(data);
        exchange.close();
    }

// merge logic from phase 1
    private static Map<String, Map<String, Object>> aggregateData(
            List<Map<String, Object>> incidents, List<Map<String, Object>> temps) {

        Map<String, Map<String, Object>> daily = new HashMap<>();

        // aggregate incident data
        for (Map<String, Object> row : incidents) {
            String d = (String) row.get("occur_date");
            if (d == null) continue;
            Map<String, Object> agg = daily.computeIfAbsent(d, k -> {
                Map<String, Object> m = new HashMap<>();
                m.put("occur_date", d);
                m.put("total_incidents", 0);
                m.put("vic_sex_countMale", 0);
                m.put("vic_sex_countFemale", 0);
                m.put("murder_true_count", 0);
                m.put("murder_false_count", 0);
                return m;
            });
            agg.put("total_incidents", (int) agg.get("total_incidents") + 1);
            String sex = (String) row.get("vic_sex");
            if ("M".equalsIgnoreCase(sex))
                agg.put("vic_sex_countMale", (int) agg.get("vic_sex_countMale") + 1);
            else if ("F".equalsIgnoreCase(sex))
                agg.put("vic_sex_countFemale", (int) agg.get("vic_sex_countFemale") + 1);
            boolean murder = (Boolean) row.getOrDefault("statistical_murder_flag", false);
            if (murder)
                agg.put("murder_true_count", (int) agg.get("murder_true_count") + 1);
            else
                agg.put("murder_false_count", (int) agg.get("murder_false_count") + 1);
        }

        // merge temperature data
        Map<String, Double> tempMap = new HashMap<>();
        for (Map<String, Object> t : temps)
            tempMap.put((String) t.get("date"), (Double) t.get("temperature_max"));

        for (var entry : daily.values()) {
            String date = (String) entry.get("occur_date");
            entry.put("daily_high_fahrenheit", tempMap.getOrDefault(date, Double.NaN));
        }

        return daily;
    }
}
