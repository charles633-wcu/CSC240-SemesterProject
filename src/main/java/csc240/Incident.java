package csc240;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class Incident {
    
    private static final String USERNAME = "a5jn079zj7ccuxwyo5mxfn70l";
    private static final String PASSWORD = "540ul3dt0g36ddicw4mbsv38gcyh8jxwupk9qfgemvqbqppqlq";
    private static final String CREDENTIAL = Credentials.basic(USERNAME, PASSWORD);
    
    private static final OkHttpClient client = new OkHttpClient.Builder()
        .callTimeout(java.time.Duration.ofMinutes(2))
        .connectTimeout(java.time.Duration.ofSeconds(20))
        .readTimeout(java.time.Duration.ofSeconds(30))
        .build();
    private static final ObjectMapper mapper = new ObjectMapper();


    public String vic_sex;
    public boolean statistical_murder_flag;
    public String incident_key;
    public LocalDate occur_date; 
    
    public static List<DailyIncidentSummary> summarizeByDate(List<Incident> incidents) {
    Map<LocalDate, DailyIncidentSummary> map = new HashMap<>();

    for (Incident x : incidents) {
        if (x.occur_date == null) continue; // skip bad data

        DailyIncidentSummary summary = map.computeIfAbsent(
            x.occur_date,
            DailyIncidentSummary::new
        );

        summary.totalIncidents++;

        if ("M".equalsIgnoreCase(x.vic_sex)) {
            summary.vicSexCountMale++;
        } else if ("F".equalsIgnoreCase(x.vic_sex)) {
            summary.vicSexCountFemale++;
        }

        if (x.statistical_murder_flag) {
            summary.murderTrueCount++;
        } else {
            summary.murderFalseCount++;
        }

        if (x.incident_key != null) {
            summary.incidentKeys.add(x.incident_key);
        }
    }

    List<DailyIncidentSummary> list = new ArrayList<>(map.values());
    list.sort(Comparator.comparing((DailyIncidentSummary s) -> s.date).reversed());
    return list;
    }


    public static List<Incident> fetchIncidents() throws IOException{
        String json = fetchIncident();
        List<Incident> incidents = parseIncidents(json);
        incidents.sort(Comparator.comparing((Incident i) -> i.occur_date).reversed());
        boolean found = incidents.stream().anyMatch(i -> i.occur_date.equals(LocalDate.of(2017, 5, 17)));
        System.out.println("5/17/2017 found in API? " + found);

        return incidents;
    }

    public static String fetchIncident() throws IOException {
        String url = "https://data.cityofnewyork.us/resource/833y-fsy8.json?$limit=100000";
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", CREDENTIAL)
            .build();

        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return response.body().string();        
        }
    }

    public static List<Incident> parseIncidents(String json) throws IOException{
        List<Incident> incidents = new ArrayList<>();
        JsonNode root = mapper.readTree(json);
        
        if (root.isArray()) {
            int firstThree = 0;
                for (JsonNode item : root) {
                    Incident x = new Incident();
                        x.vic_sex = item.path("vic_sex").asText();
                        x.statistical_murder_flag = item.path("statistical_murder_flag").asBoolean(false);
                        x.incident_key = item.path("incident_key").asText();
                        String dateStr = item.get("occur_date").asText();
                        if(dateStr != null && dateStr.length() >= 10){
                            x.occur_date = LocalDate.parse(dateStr.substring(0,10));
                        }
      
                    if(firstThree < 3){
                        System.out.println("Incident " + (firstThree + 1));
                        System.out.println("Victim Sex: " + x.vic_sex);
                        System.out.println("Murder Flag: " + x.statistical_murder_flag);
                        System.out.println("Incident Key: " + x.incident_key);
                        System.out.println("Date: " + x.occur_date + "\n");
                    }

                    incidents.add(x);
                    firstThree++;
                }
                System.out.println("Total collected: " + incidents.size());
        }  
          
        return incidents;
    }
}
    
        
