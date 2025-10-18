package csc240;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyIncidentSummary {
    public LocalDate date;
    public int totalIncidents;
    public int vicSexCountMale;
    public int vicSexCountFemale;
    public int murderTrueCount;
    public int murderFalseCount;
    public List<String> incidentKeys = new ArrayList<>();

    public DailyIncidentSummary(LocalDate date) {
        this.date = date;
    }
}
