package com.wcupa.csc240.api.data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcupa.csc240.model.Incident;
import com.wcupa.csc240.model.Temperature.TemperatureMax_Date;

public class DataService {

    private final DataRepo repo = new DataRepo();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Incident> getIncidentObjects() {
        List<Map<String,Object>> rows = repo.getIncidents();
        try {
            return rows.stream()
                .map(DataMapper::mapIncident)
                .collect(Collectors.toList());
        } catch (NullPointerException | IllegalArgumentException e) {
            // Log the error or handle it as needed
            return List.of(); // Return an empty list instead of null
        }
    }

    public String getIncidentsJson() {
        try {
            List<Incident> incidents = getIncidentObjects();
            return mapper.writeValueAsString(incidents);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"failed to get incidents\"}";
        }
    }

    public String getTemperatureJson() {
        try {
            List<TemperatureMax_Date> temps = getTemperatureObjects();
            return mapper.writeValueAsString(temps);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"failed to get temperatures\"}";
        }
    }

    public List<TemperatureMax_Date> getTemperatureObjects() {
        List<Map<String,Object>> rows = repo.getTemperature();
        try {
            return rows.stream()
                .map(DataMapper::mapTemp)
                .collect(Collectors.toList());
        } catch (NullPointerException | IllegalArgumentException e) {
            // Log the error or handle it as needed
            return List.of(); // Return an empty list instead of null
        }
    }

    public TemperatureMax_Date getTemperatureByDate(String date){
        try {
            return getTemperatureObjects().stream()
                .filter(t -> date.equals(t.getDate()))
                .findFirst()
                .orElse(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Handle specific exceptions as needed
            e.printStackTrace();
            return null;
        }
    }
}
