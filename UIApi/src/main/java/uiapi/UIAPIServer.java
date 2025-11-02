package uiapi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UIAPIServer {

    private static final int PORT = 8083;
    private static final String CLASS_API_URL = "http://localhost:8082";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/ui/", UIAPIServer::handleHomePage);
        server.createContext("/ui/view", UIAPIServer::handleViewRequest);
        server.setExecutor(null);
        server.start();

        System.out.println("UI API running at http://localhost:" + PORT + "/ui/");
    }

    /** ---------------- HOME PAGE (search form) ---------------- */
    private static void handleHomePage(HttpExchange exchange) throws IOException {
        String html = Files.readString(Paths.get("src/main/resources/templates/search.html"));
        sendHtml(exchange, 200, html);
    }

    /** ---------------- VIEW PAGE (summary display) ---------------- */
    private static void handleViewRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.startsWith("date=")) {
            sendText(exchange, 400, "Usage: /ui/view?date=YYYY-MM-DD");
            return;
        }

        String date = query.substring("date=".length());
        String json = fetchFromClassAPI("/summary/" + date);

        Map<String, Object> summary = mapper.readValue(json, Map.class);
        String html = renderHtmlSummary(summary);
        sendHtml(exchange, 200, html);
    }

    /** ---------------- Helper methods ---------------- */
    private static String fetchFromClassAPI(String path) throws IOException {
        String url = CLASS_API_URL + path;
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("ClassAPI HTTP " + response.code() + " for " + url);
            return response.body().string();
        }
    }

    private static String renderHtmlSummary(Map<String, Object> summary) throws IOException {
        String template = Files.readString(Paths.get("src/main/resources/templates/summary.html"));

        String date = (String) summary.getOrDefault("date", "N/A");
        Map<String, Object> tempData = (Map<String, Object>) summary.get("temperatureData");
        Object temp = (tempData != null) ? tempData.get("temperature") : "N/A";
        int total = ((Number) summary.getOrDefault("totalIncidents", 0)).intValue();

        String perpsBySex = mapToHtmlList((Map<String, Object>) summary.get("perpsBySex"));
        String vicsBySex = mapToHtmlList((Map<String, Object>) summary.get("vicsBySex"));
        String perpsByAge = mapToHtmlList((Map<String, Object>) summary.get("perpsByAgeRange"));
        String vicsByAge = mapToHtmlList((Map<String, Object>) summary.get("vicsByAgeRange"));

        return template
                .replace("{{date}}", date)
                .replace("{{total}}", String.valueOf(total))
                .replace("{{temp}}", String.valueOf(temp))
                .replace("{{perpsBySex}}", perpsBySex)
                .replace("{{vicsBySex}}", vicsBySex)
                .replace("{{perpsByAge}}", perpsByAge)
                .replace("{{vicsByAge}}", vicsByAge);
    }

    private static String mapToHtmlList(Map<String, Object> map) {
        if (map == null || map.isEmpty()) return "<i>No data</i>";
        StringBuilder sb = new StringBuilder("<ul>");
        for (Map.Entry<String, Object> e : map.entrySet()) {
            sb.append("<li>").append(e.getKey()).append(": ").append(e.getValue()).append("</li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }

    private static void sendHtml(HttpExchange exchange, int code, String html) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        byte[] bytes = html.getBytes();
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendText(HttpExchange exchange, int code, String msg) throws IOException {
        byte[] bytes = msg.getBytes();
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
