package com.wcupa.csc240.api.classapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcupa.csc240.api.data.DataService;
import com.wcupa.csc240.model.DailySummary;
import com.wcupa.csc240.model.Incident;
import com.wcupa.csc240.model.Temperature.TemperatureMax_Date;

public class ClassService {

    private final DataService dataService = new DataService();
    private final ObjectMapper mapper = new ObjectMapper();

    public String getDailySummaryAll() {
        try {
            List<Incident> incidents = dataService.getIncidentObjects();
            List<TemperatureMax_Date> temps = dataService.getTemperatureObjects();

            // collect all dates
            Set<String> allDates = new HashSet<>();
            for (Incident i : incidents) allDates.add(i.getOccurDate());
            for (TemperatureMax_Date t : temps) allDates.add(t.getDate());

            // map for fast temp lookup
            Map<String, TemperatureMax_Date> tempMap =
                temps.stream().collect(Collectors.toMap(TemperatureMax_Date::getDate, t -> t));

            // group incidents by date
            Map<String, List<Incident>> incByDate =
                incidents.stream().collect(Collectors.groupingBy(Incident::getOccurDate));

            List<DailySummary> summaries = new ArrayList<>();

            for (String date : allDates) {
                summaries.add(buildSummaryFor(date, incByDate.get(date), tempMap.get(date)));
            }

            summaries.sort(Comparator.comparing(DailySummary::getDate));
            return mapper.writeValueAsString(summaries);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Failed to build all summaries\"}";
        }
    }

    //daily summary is a aggregate of all incidents and subfields in a given date + temperature
    public String getDailySummaryForDate(String date) {
        try {
            List<Incident> incidents = dataService.getIncidentObjects();
            List<TemperatureMax_Date> temps = dataService.getTemperatureObjects();

            // filter incidents
            List<Incident> todays = incidents.stream()
                .filter(i -> date.equals(i.getOccurDate()))
                .toList();

            // find temp
            TemperatureMax_Date t = temps.stream()
                .filter(tmp -> date.equals(tmp.getDate()))
                .findFirst()
                .orElse(null);

            DailySummary summary = buildSummaryFor(date, todays, t);
            return mapper.writeValueAsString(summary);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Failed to build summary for date\"}";
        }
    }

    public String getCombinedAll() {
        try {
            List<Incident> incidents = dataService.getIncidentObjects();
            List<TemperatureMax_Date> temps = dataService.getTemperatureObjects();

            Map<String, TemperatureMax_Date> tempMap =
                temps.stream().collect(Collectors.toMap(TemperatureMax_Date::getDate, t -> t));

            List<Map<String,Object>> combined = new ArrayList<>();

            for (Incident inc : incidents) {
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("incident", inc);
                row.put("temperature", tempMap.get(inc.getOccurDate()));
                combined.add(row);
            }

            return mapper.writeValueAsString(combined);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Failed to build combined all\"}";
        }
    }

        //combined is a list of all incident details on a given date
    public String getCombinedForDate(String date) {
        try {
            List<Incident> incidents = dataService.getIncidentObjects();
            List<TemperatureMax_Date> temps = dataService.getTemperatureObjects();

            TemperatureMax_Date t = temps.stream()
                .filter(tmp -> date.equals(tmp.getDate()))
                .findFirst()
                .orElse(null);

            List<Map<String,Object>> combined = new ArrayList<>();

            for (Incident inc : incidents) {
                if (!date.equals(inc.getOccurDate())) continue;

                Map<String,Object> row = new LinkedHashMap<>();
                row.put("incident", inc);
                row.put("temperature", t);
                combined.add(row);
            }

            return mapper.writeValueAsString(combined);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Failed to build combined for date\"}";
        }
    }

    private DailySummary buildSummaryFor(String date,
                                         List<Incident> todaysIncidents,
                                         TemperatureMax_Date temp) {

        if (todaysIncidents == null) todaysIncidents = Collections.emptyList();

        Map<String, Long> perpsBySex = todaysIncidents.stream()
            .map(i -> i.getPerpetrator().getSex())
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        Map<String, Long> perpsByAge = todaysIncidents.stream()
            .map(i -> i.getPerpetrator().getAgeRange())
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(a -> a, Collectors.counting()));

        Map<String, Long> vicsBySex = todaysIncidents.stream()
            .map(i -> i.getVictim().getSex())
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        Map<String, Long> vicsByAge = todaysIncidents.stream()
            .map(i -> i.getVictim().getAgeRange())
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(a -> a, Collectors.counting()));

        return new DailySummary(
                date,
                todaysIncidents.size(),
                perpsBySex,
                vicsBySex,
                perpsByAge,
                vicsByAge,
                temp
        );
    }
}
