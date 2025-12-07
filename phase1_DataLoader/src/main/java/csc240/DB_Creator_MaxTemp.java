package csc240;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

//creates max temp database by date
//ensureTable and other methods seperated into multiple classes to keep type safety and clarity
public class DB_Creator_MaxTemp {
   
    public static final String DB_URL = "jdbc:sqlite:TemperatureDB.db";

    //ensures table created if not already exist
    public static void ensureTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS TEMPERATURE_RAW (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT,
                temperature_max DOUBLE
            );
            """;
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + System.getProperty("user.dir") + "/TemperatureDB.db");
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("TEMPERATURE_RAW table verified or created.");
        } catch (SQLException e) {
            System.out.println("ensureTable() failed: " + e.getMessage());
        }
    }

    //clears existing data in case of new data
    public static void clearTable() {
        String sql = "DELETE FROM TEMPERATURE_RAW";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + System.getProperty("user.dir") + "/TemperatureDB.db");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("TEMPERATURE_RAW table cleared.");
        } catch (SQLException e) {
            System.out.println("clearTable() failed: " + e.getMessage());
        }
    }

    //inserts all fetched rows into table
    public static void insertRows(Collection<Map<String, Object>> rows) {
        String sql = "INSERT INTO TEMPERATURE_RAW (date, temperature_max) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + System.getProperty("user.dir") + "/TemperatureDB.db");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Map<String, Object> row : rows) {
                ps.setString(1, (String) row.get("date"));
                ps.setDouble(2, (Double) row.get("temperature_max"));
                ps.addBatch();
            }

            ps.executeBatch();
            System.out.println("Inserted " + rows.size() + " temperature records.");
        } catch (SQLException e) {
            System.out.println("insertRows() failed: " + e.getMessage());
        }
    }
}
