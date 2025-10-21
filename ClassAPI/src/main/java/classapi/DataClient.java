package classapi;

import com.fasterxml.jackson.databind.ObjectMapper;

import classapi.models.DailySummary;
import classapi.models.Incident;
import classapi.models.TemperatureMax;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataClient {
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String BASE_URL = "http://localhost:8081/data";

    public static Incident fetchIncident(String date) throws Exception {
        String url = BASE_URL + "/incidents/" + date;
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new RuntimeException("Failed to fetch incident data: " + response);
            return mapper.readValue(response.body().string(), Incident.class);
        }
    }

//gets temperature by date from DataAPI

    public static TemperatureMax fetchTemperature(String date) throws Exception {
        String url = BASE_URL + "/temperature/" + date;
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new RuntimeException("Failed to fetch temperature data: " + response);
            return mapper.readValue(response.body().string(), TemperatureMax.class);
        }
    }
// creates the merged daily summary by fetching the incident from the crime api and the temperature from the weather api

    public static DailySummary fetchCombined(String date) throws Exception {
        Incident i = fetchIncident(date);
        TemperatureMax t = fetchTemperature(date);
        DailySummary d = new DailySummary();
        d.incident = i;
        d.temperature = t;
        return d;
    }
}
