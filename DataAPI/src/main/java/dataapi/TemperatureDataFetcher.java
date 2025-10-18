package dataapi;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

public class TemperatureDataFetcher {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Map<String, Object>> fetchTemperatureDates() throws IOException {
        String url = "https://archive-api.open-meteo.com/v1/archive?latitude=40.7&longitude=-74&start_date=2006-01-01&end_date=2025-10-09&daily=temperature_2m_max&timezone=America/New_York";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return parseTemperatureDates(response.body().string());
        }
    }

    private static List<Map<String, Object>> parseTemperatureDates(String json) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        JsonNode root = mapper.readTree(json);
        JsonNode daily = root.get("daily");

        if (daily != null && daily.has("temperature_2m_max") && daily.has("time")) {
            JsonNode tempsMax = daily.get("temperature_2m_max");
            JsonNode dates = daily.get("time");

            for (int i = 0; i < dates.size(); i++) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("date", dates.get(i).asText());
                double fahrenheit = tempsMax.get(i).asDouble() * 1.8 + 32;
                row.put("temperature_max", fahrenheit);
                list.add(row);
            }
        }
        return list;
    }
}
