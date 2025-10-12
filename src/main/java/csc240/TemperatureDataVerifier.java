package csc240;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TemperatureDataVerifier {

    private static final String DB_URL = "jdbc:sqlite:crimeweather.db";

    public static void verify() {
        try {
            int dbCount = getDatabaseCount();

            System.out.println("Temperature Data Verification");
            System.out.println("Database record count (merged table): " + dbCount);
            System.out.println("Temperature data verification complete.");

        } 
        catch (SQLException e) {
            System.out.println("Database verification failed: " + e.getMessage());
            e.printStackTrace();
        } 
        catch (Exception e) {
            System.out.println("Unexpected error during temperature verification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int getDatabaseCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM crimeNYC_Daily";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.getInt(1);
        }
    }

    public static void writeTemperatureSummaryFile(int totalInserted) {
        try (java.io.FileWriter writer = new java.io.FileWriter("temperature_summary.txt")) {
            writer.write("Temperature Loader Summary\n");
            writer.write("Run Date: " + java.time.LocalDateTime.now() + "\n");
            writer.write("Total Records Inserted: " + totalInserted + "\n");
            System.out.println("Summary file created: temperature_summary.txt");
        } 
        catch (Exception e) {
        System.out.println("Failed to write temperature summary file: " + e.getMessage());
        }
    }
    
}
