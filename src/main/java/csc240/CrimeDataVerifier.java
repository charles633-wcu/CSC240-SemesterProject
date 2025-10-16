package csc240;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CrimeDataVerifier {

    private static final String DB_URL = "jdbc:sqlite:crimeweather.db";

    public static void verify() {
        try {
            int summaryCount = readSummaryFile("crime_summary.txt");
            int dbCount = getDatabaseCount();

            System.out.println("Summary file count: " + summaryCount);
            System.out.println("Database count: " + dbCount);

            if (summaryCount == dbCount) {
                System.out.println("Verification successful");
            } else {
                System.out.println("Verification failed");
            }
        } catch (Exception e) {
            System.out.println("Verification failed: " + e.getMessage());
        }
    }
    private static int readSummaryFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Total Records Inserted:")) {
                    return Integer.parseInt(line.replaceAll("[^0-9]", ""));
                }
            }
        }
        return 0;
    }

    private static int getDatabaseCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM crimeNYC_Daily";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.getInt(1);
        }
    }

    
}
