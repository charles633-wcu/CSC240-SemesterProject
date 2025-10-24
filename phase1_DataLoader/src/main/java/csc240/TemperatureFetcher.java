package csc240;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// fetches temperature data from Open-Meteo and returns date→temperature list
public class TemperatureFetcher {

    // creates timeout so long API call doesn’t close early
    private static final OkHttpClient client = new OkHttpClient.Builder()
        .callTimeout(java.time.Duration.ofMinutes(2))
        .connectTimeout(java.time.Duration.ofSeconds(20))
        .readTimeout(java.time.Duration.ofSeconds(30))
        .build();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Map<String, Object>> fetchTemperatures() throws IOException {
        String url =
            "https://archive-api.open-meteo.com/v1/archive?"
          + "latitude=40.7&longitude=-74"
          + "&start_date=2006-01-01&end_date=2025-10-09"
          + "&daily=temperature_2m_max&timezone=America/New_York";

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String json = response.body().string();
            return parseTemperatures(json);
        }
    }

    private static List<Map<String, Object>> parseTemperatures(String json) throws IOException {
        List<Map<String, Object>> temps = new ArrayList<>();
        JsonNode root = mapper.readTree(json);
        JsonNode daily = root.path("daily");
        JsonNode times = daily.path("time");
        JsonNode maxTemps = daily.path("temperature_2m_max");

        if (times.isArray() && maxTemps.isArray()) {
            for (int i = 0; i < times.size(); i++) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("date", times.get(i).asText());
                row.put("temperature_max", maxTemps.get(i).asDouble());
                temps.add(row);
            }
        }

        return temps;
    }
}
