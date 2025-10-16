package csc240;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NYC_Crime_Temperature {
    
    private static final String DB_URL = "jdbc:sqlite:crimeweather.db";

    public static void main(String[] args) throws Exception  {
        execute();
        //WeatherReport.fetchJsonString();
        //TemperatureMax_Date
    }

    private static void execute() throws Exception{
        try {
            createTable();
            List<Incident> incidents = Incident.fetchIncidents();
            List<DailyIncidentSummary> summaries = Incident.summarizeByDate(incidents);
            List<TemperatureMax_Date> temperatures = TemperatureMax_Date.fetchTemperature_Dates();

            storeData(summaries, temperatures);

            boolean debug = false;
            if(debug){
                for (int i = 0; i < 5 && i < summaries.size(); i++) {
                    System.out.println(summaries.get(i));
                }
            }
           


            // List<TemperatureMax_Date> temps = TemperatureMax_Date.fetchTemperature_Dates();
            // for(int i = 0; i < 5 && i < temps.size(); i++){
            //     TemperatureMax_Date temp = temps.get(i);
            //     System.out.println(temp.date + ": " + temp.temperature_max);
            // }
            //printresults();
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }       
    } 

    private static void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS crimeNYC_Daily (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    occur_date TEXT UNIQUE,
                    total_incidents INTEGER,
                    vic_sex_countMale INTEGER,
                    vic_sex_countFemale INTEGER,
                    murder_true_count INTEGER,
                    murder_false_count INTEGER,
                    daily_high_fahrenheit DOUBLE,
                    incident_keys TEXT
                )
                """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        }
        catch (SQLException e) {
            System.out.println("SQLException caught " + e.getMessage());
        }
    }

    private static void writeSummaryFile(int totalInserted) {
        try (java.io.FileWriter writer = new java.io.FileWriter("crime_summary.txt")) {
            writer.write("Crime Loader Summary\n");
            writer.write("Run Date: " + java.time.LocalDateTime.now() + "\n");
            writer.write("Total Records Inserted: " + totalInserted + "\n");
            System.out.println("Summary file created: crime_summary.txt");
        } 
        catch (Exception e) {
            System.out.println("Failed to write summary file: " + e.getMessage());
        }
    }


    private static void storeData(List<DailyIncidentSummary> summaries, List<TemperatureMax_Date> temperatures) throws SQLException {
        Map<String, Double> tempMap = new HashMap<>();
        for (TemperatureMax_Date t : temperatures) {
            tempMap.put(t.date, t.temperature_max);
        }

        String sql = "INSERT INTO crimeNYC_Daily (occur_date, total_incidents, vic_sex_countMale, vic_sex_countFemale, murder_true_count, murder_false_count, daily_high_fahrenheit, incident_keys) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement ps = conn.prepareStatement(sql)) {

            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM crimeNYC_Daily")) {
                deleteStmt.executeUpdate();
            }

            for (DailyIncidentSummary summary: summaries) {
                ps.setString(1, summary.date.toString());
                ps.setInt(2, summary.totalIncidents);
                ps.setInt(3, summary.vicSexCountMale);
                ps.setInt(4, summary.vicSexCountFemale);
                ps.setInt(5, summary.murderTrueCount);
                ps.setInt(6, summary.murderFalseCount);
                Double temp = tempMap.get(summary.date.toString());
                ps.setDouble(7, temp);
                ps.setString(8, String.join(",", summary.incidentKeys));
                ps.addBatch();
            }
            
            ps.executeBatch();

            Map<String, Double> tempSummaries = TemperatureMax_Date.summarizeByDate(temperatures);
            TemperatureDataVerifier.writeTemperatureSummaryFile(tempSummaries.size());
        
            writeSummaryFile(summaries.size()); 
            TemperatureDataVerifier.verify();
            CrimeDataVerifier.verify();
        }
    }
}
