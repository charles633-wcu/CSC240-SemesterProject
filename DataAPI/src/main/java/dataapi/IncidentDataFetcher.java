package dataapi;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;

public class IncidentDataFetcher {
    private static final String USERNAME = "a5jn079zj7ccuxwyo5mxfn70l";
    private static final String PASSWORD = "540ul3dt0g36ddicw4mbsv38gcyh8jxwupk9qfgemvqbqppqlq";
    private static final String CREDENTIAL = Credentials.basic(USERNAME, PASSWORD);

    private static final OkHttpClient client = new OkHttpClient.Builder()
        .callTimeout(java.time.Duration.ofMinutes(2))
        .connectTimeout(java.time.Duration.ofSeconds(20))
        .readTimeout(java.time.Duration.ofSeconds(30))
        .build();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Map<String, Object>> fetchIncidents() throws IOException {
        String url = "https://data.cityofnewyork.us/resource/833y-fsy8.json?$limit=100000";
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", CREDENTIAL)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String json = response.body().string();
            return parseIncidents(json);
        }
    }

    private static List<Map<String, Object>> parseIncidents(String json) throws IOException {
        List<Map<String, Object>> incidents = new ArrayList<>();
        JsonNode root = mapper.readTree(json);

        if (root.isArray()) {
            for (JsonNode item : root) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("vic_sex", item.path("vic_sex").asText());
                row.put("statistical_murder_flag", item.path("statistical_murder_flag").asBoolean(false));
                row.put("incident_key", item.path("incident_key").asText());
                String dateStr = item.path("occur_date").asText();
                if (dateStr != null && dateStr.length() >= 10) {
                    row.put("occur_date", LocalDate.parse(dateStr.substring(0, 10)).toString());
                }
                incidents.add(row);
            }
        }
        return incidents;
    }
}
