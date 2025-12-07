package com.wcupa.csc240.model;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wcupa.csc240.model.Temperature.TemperatureMax_Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DailySummary {

    private String date;
    private int totalIncidents;
    private Map<String, Long> perpsBySex;
    private Map<String, Long> vicsBySex;
    private Map<String, Long> perpsByAgeRange;
    private Map<String, Long> vicsByAgeRange;
    private TemperatureMax_Date temperatureData;

    // Required for Jackson
    public DailySummary() {}

    // The ONLY constructor you need for your new daily-summary flow
    public DailySummary(
            String date,
            int totalIncidents,
            Map<String, Long> perpsBySex,
            Map<String, Long> vicsBySex,
            Map<String, Long> perpsByAgeRange,
            Map<String, Long> vicsByAgeRange,
            TemperatureMax_Date temperatureData
    ) {
        this.date = date;
        this.totalIncidents = totalIncidents;
        this.perpsBySex = perpsBySex;
        this.vicsBySex = vicsBySex;
        this.perpsByAgeRange = perpsByAgeRange;
        this.vicsByAgeRange = vicsByAgeRange;
        this.temperatureData = temperatureData;
    }

    // Getters for JSON serialization
    public String getDate() { return date; }
    public int getTotalIncidents() { return totalIncidents; }
    public Map<String, Long> getPerpsBySex() { return perpsBySex; }
    public Map<String, Long> getVicsBySex() { return vicsBySex; }
    public Map<String, Long> getPerpsByAgeRange() { return perpsByAgeRange; }
    public Map<String, Long> getVicsByAgeRange() { return vicsByAgeRange; }
    public TemperatureMax_Date getTemperatureData() { return temperatureData; }
}
