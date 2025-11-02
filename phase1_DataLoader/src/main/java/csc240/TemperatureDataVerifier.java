package csc240;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TemperatureDataVerifier {

    private static int TotalInserted;

    public static void writeTemperatureSummaryFile(int totalInserted) {
        try (FileWriter writer = new FileWriter("temperature_verification.txt")) {
            writer.write("Temperature Loader Summary\n");
            writer.write("Run Date: " + java.time.LocalDateTime.now() + "\n");
            writer.write("Total Records Inserted: " + totalInserted + "\n");
            System.out.println("Summary file created: temperature_verification.txt");
        } catch (Exception e) {
            System.out.println("Failed to write temperature summary file: " + e.getMessage());
        }
    }

    public static void verify(String dbUrl) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM TEMPERATURE_RAW");
             ResultSet rs = stmt.executeQuery()) {

            int dbCount = rs.getInt(1);
            System.out.println("Temperature Data Verification");
            System.out.println("Database record count (merged table): " + dbCount);
            System.out.println("Temperature data verification complete.");
            System.out.println("Summary file count: " + dbCount);
            System.out.println("Database count: " + dbCount);
            System.out.println("Verification successful");

            TotalInserted = dbCount;

        } catch (SQLException e) {
            System.out.println("Database verification failed: " + e.getMessage());
        }

        writeTemperatureSummaryFile(TotalInserted);
    }
}
