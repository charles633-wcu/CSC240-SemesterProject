package classapi;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import classapi.models.DailySummary;
import classapi.models.Incident;
import classapi.models.Temperature.TemperatureMax_Date;

//our original version was just a data pipeline, after office hours, we read up on how OOP principals work on data  
//using OOP is powerful when you want to be able to test and reuse components across projects
public class ClassAPIServer {

    private static final ObjectMapper mapper = new ObjectMapper();
    
    static {
        //this will get the ClassAPI to allow "<18" as an age range entry (we asked for a former cs students help on this part)
        mapper.getFactory().setCharacterEscapes(new CharacterEscapes() {
            private final int[] esc = CharacterEscapes.standardAsciiEscapesForJSON();

            @Override
            public int[] getEscapeCodesForAscii() {
                esc['<'] = CharacterEscapes.ESCAPE_NONE;
                esc['>'] = CharacterEscapes.ESCAPE_NONE;
                esc['&'] = CharacterEscapes.ESCAPE_NONE;
                return esc;
            }

            @Override
            public SerializableString getEscapeSequence(int ch) {
                return new SerializedString(String.valueOf((char) ch));
            }
        });
    }

    public static void main(String args[]) throws Exception {
        execute();
    }


    public static void execute() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);

        // only one endpoint: GET /summary/{date}
        server.createContext("/summary/", exchange -> {
            String[] parts = exchange.getRequestURI().getPath().split("/");
            if (parts.length < 3) {
                sendText(exchange, 400, "Usage: /summary/{date}");
                return;
            }

            String date = parts[2];
                try {
                List<Incident> incidents = DataClient.fetchIncidents(date);
                TemperatureMax_Date temp = DataClient.fetchTemperature(date);

                DailySummary summary = new DailySummary(date, incidents, temp);
                byte[] json = mapper.writeValueAsBytes(summary);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, json.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(json); }

            } catch (Exception e) {
                sendText(exchange, 500, "Failed: " + e.getMessage());
            }
        });
        System.out.println("ClassAPI running on port 9080 ");
        System.out.println("   - http://localhost:9080/summary/{date}");
        server.start();
    }

    private static void sendText(com.sun.net.httpserver.HttpExchange exchange, int status, String message) throws java.io.IOException {
        exchange.sendResponseHeaders(status, message.length());
        try (OutputStream os = exchange.getResponseBody()) { os.write(message.getBytes()); }
    }
}
