package csc240;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
 import okhttp3.Response;

public class TemperatureMax_Date {
    public String location;
    public String date;
    public double temperature_max;
    public double temperature_min;

    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static List<TemperatureMax_Date> fetchTemperature_Dates() throws IOException{
        String json = fetchTemperature_Date();
        return parseTemperature_Date(json);
    }   

    public static String fetchTemperature_Date() throws IOException {
        
        String url = "https://archive-api.open-meteo.com/v1/archive?latitude=40.7&longitude=-74&start_date=2006-01-01&end_date=2025-10-09&daily=temperature_2m_max&timezone=America/New_York";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            //rawjson for testing
            String apiresponse = response.body().string();
            //System.out.println(apiresponse);
            return apiresponse;
        }
        
    }

    public static List<TemperatureMax_Date> parseTemperature_Date(String json) throws IOException {
        List<TemperatureMax_Date> list = new ArrayList<>();
        JsonNode root = mapper.readTree(json);
        JsonNode daily = root.get("daily");

        if(daily != null && daily.has("temperature_2m_max") && daily.has("time")) {
            JsonNode tempsMax = daily.get("temperature_2m_max");
            JsonNode tempsMin = daily.has("temperature_2m_min") ? daily.get("temperature_2m_min") : null;
            JsonNode dates = daily.get("time");

            for(int i = 0; i < dates.size(); i++) {
                TemperatureMax_Date t = new TemperatureMax_Date();
                t.date = dates.get(i).asText();
                t.temperature_max = tempsMax.get(i).asDouble();
                t.temperature_max = ((t.temperature_max * 1.8) + 32);
                if(tempsMin != null) {
                    t.temperature_min = tempsMin.get(i).asDouble();
                }
                list.add(t);
            }
        }
        
        return list;
    }

    public static Map<String, Double> summarizeByDate(List<TemperatureMax_Date> temps) {
        Map<String, Double> map = new HashMap<>();

        for (TemperatureMax_Date t : temps) {
        // In your API, each date should appear only once, but we’ll still handle duplicates safely
        map.merge(t.date, t.temperature_max, (oldVal, newVal) -> (oldVal + newVal) / 2);
        }

        return map; 
    }
}
