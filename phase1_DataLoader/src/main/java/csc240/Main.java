package csc240;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        execute();
    }

    public static void execute() throws IOException{
        System.out.println("...Phase 1 Data Loader...");

        List<Map<String, Object>> incidents = IncidentFetcher.fetchIncidents();

        // incidents database
        try {
            System.out.println("\n[INCIDENTS] Fetching and storing data...");
            DB_Creator_Incidents.ensureTable();
            DB_Creator_Incidents.clearTable();
            DB_Creator_Incidents.insertRows(incidents);
            System.out.println("[INCIDENTS] Completed. Inserted " + incidents.size() + " rows.");
        } catch (Exception e) {
            System.out.println("[INCIDENTS] error: " + e.getMessage());
        }

        try {
            System.out.println("\n[VICTIMS] populating victim database...");
            DB_Creator_Victims.ensureTable();
            DB_Creator_Victims.clearTable();
            DB_Creator_Victims.insertRows(incidents);
            System.out.println("[VICTIMS] Completed.");
        } catch (Exception e) {
            System.out.println("[VICTIMS] error: " + e.getMessage());
        }

        try {
            System.out.println("\n[Perpetrators] populating perpetrator database...");
            DB_Creator_Perps.ensureTable();
            DB_Creator_Perps.clearTable();
            DB_Creator_Perps.insertRows(incidents);
            System.out.println("[Perpetrators] Completed.");
        } catch (Exception e) {
            System.out.println("[PERPS] error: " + e.getMessage());
        }


        // temperature database
        try {
            System.out.println("\n[TEMPERATURE] Fetching and storing data...");
            List<Map<String, Object>> temps = TemperatureFetcher.fetchTemperatures();
            DB_Creator_MaxTemp.ensureTable();
            DB_Creator_MaxTemp.clearTable();
            DB_Creator_MaxTemp.insertRows(temps);
            System.out.println("[TEMPERATURE] Completed. Inserted " + temps.size() + " rows.");
        } catch (IOException e) {
            System.out.println("[TEMPERATURE] fetchTemperatures() failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[TEMPERATURE] error: " + e.getMessage());
        }

        IncidentDataVerifier.verify(DB_Creator_Incidents.DB_URL);
        TemperatureDataVerifier.verify(DB_Creator_MaxTemp.DB_URL);

        System.out.println("\n Phase 1 Data Loader completed" + "\n");
    }
}
