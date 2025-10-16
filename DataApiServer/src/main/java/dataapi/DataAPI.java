package dataapi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class DataAPI {
                                            
    private static final String DB_URL = "jdbc:sqlite:C:/Users/czw53/CSC240-SemesterProject/crimeweather.db";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Basic test route
        server.createContext("/test", exchange -> sendJson(exchange, Map.of("message", "Data API server is running!")));

        // Crime data endpoints
        server.createContext("/crimes", new GetAllCrimesHandler());

        // Temperature data endpoints
        server.createContext("/temperatures", new GetAllTemperaturesHandler());
        server.createContext("/temperatures/", new GetTemperatureByDateHandler());

        //  Fallback route (404)
        server.createContext("/", exchange -> sendJson(exchange, Map.of("error", "Endpoint not found"), 404));

        server.setExecutor(null);
        server.start();
        System.out.println("Data API Server running at http://localhost:8080");
    }

    // -------------------- HANDLERS --------------------

    //Get crimes
    static class GetAllCrimesHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            List<Map<String, Object>> results = queryTable("SELECT * FROM crimeNYC_Daily LIMIT 50");
            sendJson(exchange, results);
        }
    }

    //Gets temperatueres
    static class GetAllTemperaturesHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            List<Map<String, Object>> results = queryTable("SELECT * FROM TemperatureMax_Date LIMIT 50");
            sendJson(exchange, results);
        }
    }

    // 🔹 Get temperature by date
    static class GetTemperatureByDateHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String path = exchange.getRequestURI().getPath();
            String dateStr = path.substring(path.lastIndexOf('/') + 1);

            if (dateStr.isEmpty()) {
                sendJson(exchange, Map.of("error", "Missing date"), 400);
                return;
            }

            String sql = "SELECT * FROM TemperatureMax_Date WHERE date = ?";
            List<Map<String, Object>> results = queryWithParam(sql, dateStr);
            if (results.isEmpty())
                sendJson(exchange, Map.of("error", "Temperature data not found"), 404);
            else
                sendJson(exchange, results.get(0));
        }
    }

    //data base helpers

    private static List<Map<String, Object>> queryTable(String sql) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private static List<Map<String, Object>> queryWithParam(String sql, String param) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= colCount; i++) {
                        row.put(meta.getColumnName(i), rs.getObject(i));
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // json response helpers

    private static void sendJson(HttpExchange exchange, Object data) throws IOException {
        sendJson(exchange, data, 200);
    }

    private static void sendJson(HttpExchange exchange, Object data, int status) throws IOException {
        byte[] response = mapper.writeValueAsBytes(data);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(status, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
}
