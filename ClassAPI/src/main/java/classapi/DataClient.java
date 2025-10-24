package classapi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import classapi.models.Incident;
import classapi.models.Temperature.TemperatureMax_Date;

public class DataClient {

    private static final String DATA_API_URL = "http://localhost:8081/data";
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // gets incidents and filters by date
    public static List<Incident> fetchIncidents(String date) throws IOException {
        // get all incidents from dataapi
        String urlString = DATA_API_URL + "/incidents";  // no /date
        System.out.println("Fetching all incidents from: " + urlString);

        String json = httpGet(urlString);

        // deserialize full 
        List<Incident> allIncidents = Arrays.asList(mapper.readValue(json, Incident[].class));

        // filter by date 
        List<Incident> filtered = allIncidents.stream()
            .filter(i -> date.equals(i.getOccurDate()))   // assumes Incident.getOccurDate() returns date
            .collect(Collectors.toList());

        System.out.println("→ Found " + filtered.size() + " incidents for " + date);
        return filtered;
    }


    //get temperature by date
    public static TemperatureMax_Date fetchTemperature(String date) throws IOException {
        String urlString = DATA_API_URL + "/temperature";  
        String json = httpGet(urlString);

        List<TemperatureMax_Date> allTemps =
            Arrays.asList(mapper.readValue(json, TemperatureMax_Date[].class));

        return allTemps.stream()
            .filter(t -> date.equals(t.getDate()))
            .findFirst()
            .orElse(null);
    }


    // helsp make get requests
    private static String httpGet(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP " + responseCode + " from " + urlString);
        }

        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
