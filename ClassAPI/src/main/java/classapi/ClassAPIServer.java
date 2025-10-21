package classapi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import classapi.models.DailySummary;

public class ClassAPIServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
        ObjectMapper mapper = new ObjectMapper();

        server.createContext("/combined/", exchange -> {
            String[] parts = exchange.getRequestURI().getPath().split("/");
            if (parts.length < 3) {
                sendText(exchange, 400, "Usage: /combined/{date}");
                return;
            }
            String date = parts[2];
            try {
                DailySummary summary = DataClient.fetchCombined(date);
                byte[] json = mapper.writeValueAsBytes(summary);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, json.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(json); }
            } catch (Exception e) {
                sendText(exchange, 500, "Error combining data: " + e.getMessage());
            }
        });

        server.start();
        System.out.println("ClassAPI running at http://localhost:8082/combined/{date} or (APISIX) http://localhost:9080/combined/{date{}");
    }

    private static void sendText(HttpExchange exchange, int code, String text) throws IOException {
        exchange.sendResponseHeaders(code, text.length());
        try (OutputStream os = exchange.getResponseBody()) { os.write(text.getBytes()); }
    }
}
