package dataapi;
import dataapi.Config;

import java.sql.*;
import java.util.*;

public class DBHelperClass {

    // Create the table if not exists
    public static void ensureTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS DAILY_CRIME_TEMPERATURE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                occur_date TEXT,
                total_incidents INTEGER,
                vic_sex_countMale INTEGER,
                vic_sex_countFemale INTEGER,
                murder_true_count INTEGER,
                murder_false_count INTEGER,
                daily_high_fahrenheit DOUBLE
            );
            """;
        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table verified or created.");
        } catch (SQLException e) {
            System.out.println("ensureTable() failed: " + e.getMessage());
        }
    }

    // Clear data
    public static void clearTable() {
        String sql = "DELETE FROM DAILY_CRIME_TEMPERATURE";
        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Table cleared.");
        } catch (SQLException e) {
            System.out.println("clearTable() failed: " + e.getMessage());
        }
    }

    // Insert rows
    public static void insertDailyRows(Collection<Map<String,Object>> rows) {
        String sql = """
            INSERT INTO DAILY_CRIME_TEMPERATURE
            (occur_date, total_incidents, vic_sex_countMale, vic_sex_countFemale,
             murder_true_count, murder_false_count, daily_high_fahrenheit)
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;
        try (Connection conn = DriverManager.getConnection(Config.DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Map<String, Object> row : rows) {
                ps.setString(1, (String) row.get("occur_date"));
                ps.setInt(2, (int) row.get("total_incidents"));
                ps.setInt(3, (int) row.get("vic_sex_countMale"));
                ps.setInt(4, (int) row.get("vic_sex_countFemale"));
                ps.setInt(5, (int) row.get("murder_true_count"));
                ps.setInt(6, (int) row.get("murder_false_count"));
                ps.setObject(7, row.get("daily_high_fahrenheit"));
                ps.addBatch();
            }

            ps.executeBatch();
            System.out.println("Inserted " + rows.size() + " records.");

        } catch (SQLException e) {
            System.out.println("insertDailyRows() failed: " + e.getMessage());
        }
    }
}
