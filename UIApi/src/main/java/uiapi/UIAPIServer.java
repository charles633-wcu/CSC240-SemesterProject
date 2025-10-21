package uiapi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class UIAPIServer {
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8083), 0);

        // Homepage with input form
        server.createContext("/", exchange -> {
            String html = """
                <html>
                    <head><title>NYC Incident & Temperature Viewer</title></head>
                    <body style='font-family:sans-serif'>
                        <h1>NYC Incident & Temperature Viewer</h1>
                        <form method='get' action='/ui/view'>
                            <label>Enter date (YYYY-MM-DD): </label>
                            <input type='text' name='date' required/>
                            <input type='submit' value='Search'/>
                        </form>
                    </body>
                </html>
            """;
            sendHTML(exchange, html);
        });

        // /view?date=2022-07-04
        server.createContext("/ui/view", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            if (query == null || !query.contains("date=")) {
                sendHTML(exchange, "<p>Missing ?date=YYYY-MM-DD</p>");
                return;
            }

            String date = query.split("=")[1];
            String url = System.getenv("CLASS_API_URL") + "/combined/" + date;
            System.out.println("Fetching from ClassAPI: " + url);

            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    sendHTML(exchange, "<p>Failed to fetch data for " + date + "</p>");
                    return;
                }

                // Parse as generic JSON
                JsonNode json = mapper.readTree(response.body().string());
                JsonNode inc = json.path("incident");
                JsonNode temp = json.path("temperature");

                String html = String.format("""
                    <html>
                        <body style='font-family:sans-serif'>
                            <h2>Summary for %s</h2>
                            <p>Total incidents: %s</p>
                            <p>Male victims: %s | Female victims: %s</p>
                            <p>Murder true: %s | false: %s</p>
                            <p>High Temp: %.1f°F</p>
                            <a href='/'>Back</a>
                        </body>
                    </html>
                    """,
                    inc.path("occur_date").asText("Unknown"),
                    inc.path("total_incidents").asInt(0),
                    inc.path("vic_sex_countMale").asInt(0),
                    inc.path("vic_sex_countFemale").asInt(0),
                    inc.path("murder_true_count").asInt(0),
                    inc.path("murder_false_count").asInt(0),
                    temp.path("daily_high_fahrenheit").asDouble(Double.NaN)
                );

                sendHTML(exchange, html);
            } catch (Exception e) {
                sendHTML(exchange, "<p>Error fetching data: " + e.getMessage() + "</p>");
            }
        });

        server.start();
        System.out.println("UIAPI running at http://localhost:8083/ or http://localhost:9080/ui/dashboard");
        String s = System.getenv("CLASS_API_URL");
        System.out.println(s);
    }

    private static void sendHTML(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes();
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
