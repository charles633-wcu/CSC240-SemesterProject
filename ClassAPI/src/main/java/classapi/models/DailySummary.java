package classapi.models;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import classapi.models.Temperature.TemperatureMax_Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DailySummary {

    private String date;
    private int totalIncidents;
    private Map<String, Long> perpsBySex;
    private Map<String, Long> vicsBySex;
    private Map<String, Long> perpsByAgeRange;
    private Map<String, Long> vicsByAgeRange;
    private TemperatureMax_Date temperatureData;

    // Optional: you can ignore this field in JSON to keep responses smaller
    @JsonIgnore
    private List<Incident> incidents; // "has-a incidents" relationship

    //  no-arg constructor (Jackson requires this)
    public DailySummary() {}

    //  Constructor used internally or by Jackson when all properties are present
    @JsonCreator
    public DailySummary(
            @JsonProperty("date") String date,
            @JsonProperty("incidents") List<Incident> incidents,
            @JsonProperty("temperatureData") TemperatureMax_Date temperatureData) {
        this.date = date;
        this.incidents = incidents;
        this.temperatureData = temperatureData;
        computeAggregates();
    }

    private void computeAggregates() {
        if (incidents == null || incidents.isEmpty()) return;

        totalIncidents = incidents.size();

        // group perpetrators that day by sex
        perpsBySex = incidents.stream()
                .filter(i -> i.getPerpetrator() != null && i.getPerpetrator().getSex() != null)
                .collect(Collectors.groupingBy(i -> i.getPerpetrator().getSex(), Collectors.counting()));

        // group victims that day by sex
        vicsBySex = incidents.stream()
                .filter(i -> i.getVictim() != null && i.getVictim().getSex() != null)
                .collect(Collectors.groupingBy(i -> i.getVictim().getSex(), Collectors.counting()));

        // group perpetrators t hat day by age range
        perpsByAgeRange = incidents.stream()
                .filter(i -> i.getPerpetrator() != null && i.getPerpetrator().getAgeRange() != null)
                .collect(Collectors.groupingBy(i -> i.getPerpetrator().getAgeRange(), Collectors.counting()));

        // group victims that day by age range
        vicsByAgeRange = incidents.stream()
                .filter(i -> i.getVictim() != null && i.getVictim().getAgeRange() != null)
                .collect(Collectors.groupingBy(i -> i.getVictim().getAgeRange(), Collectors.counting()));
    }

    // Getters (used automatically by Jackson for serialization)
    public String getDate() { return date; }
    public int getTotalIncidents() { return totalIncidents; }
    public Map<String, Long> getPerpsBySex() { return perpsBySex; }
    public Map<String, Long> getVicsBySex() { return vicsBySex; }
    public Map<String, Long> getPerpsByAgeRange() { return perpsByAgeRange; }
    public Map<String, Long> getVicsByAgeRange() { return vicsByAgeRange; }
    public TemperatureMax_Date getTemperatureData() { return temperatureData; }

    // getter for debugging, ignored in json
    @JsonIgnore
    public List<Incident> getIncidents() { return incidents; }

    // for debugging
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Date: ").append(date).append("\n");
        sb.append("Total Incidents: ").append(totalIncidents).append("\n\n");

        sb.append("Perpetrators by Sex:\n");
        if (perpsBySex != null) perpsBySex.forEach((k, v) -> sb.append("  ").append(k).append(": ").append(v).append("\n"));

        sb.append("\nVictims by Sex:\n");
        if (vicsBySex != null) vicsBySex.forEach((k, v) -> sb.append("  ").append(k).append(": ").append(v).append("\n"));

        sb.append("\nPerpetrators by Age Range:\n");
        if (perpsByAgeRange != null) perpsByAgeRange.forEach((k, v) -> sb.append("  ").append(k).append(": ").append(v).append("\n"));

        sb.append("\nVictims by Age Range:\n");
        if (vicsByAgeRange != null) vicsByAgeRange.forEach((k, v) -> sb.append("  ").append(k).append(": ").append(v).append("\n"));

        sb.append("\nDaily Maximum Temperature: ");
        sb.append(temperatureData != null ? temperatureData.getTemperature() + "°F" : "N/A");

        return sb.toString();
    }
}
