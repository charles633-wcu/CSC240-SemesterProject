package csc240;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

// handles victim data into VictimsDB.db
public class DB_Creator_Victims {

    public static void ensureTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS VICTIMS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                occur_date TEXT,
                vic_age_group TEXT,
                vic_sex TEXT,
                vic_race TEXT
            );
            """;
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + System.getProperty("user.dir") + "/VictimsDB.db");
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("VICTIMS table verified or created.");
        } catch (SQLException e) {
            System.out.println("ensureTable() failed: " + e.getMessage());
        }
    }

    public static void clearTable() {
        String sql = "DELETE FROM VICTIMS";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + System.getProperty("user.dir") + "/VictimsDB.db");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("VICTIMS table cleared.");
        } catch (SQLException e) {
            System.out.println("clearTable() failed: " + e.getMessage());
        }
    }

    public static void insertRows(Collection<Map<String, Object>> rows) {
        String sql = "INSERT INTO VICTIMS (occur_date, vic_age_group, vic_sex, vic_race) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + System.getProperty("user.dir") + "/VictimsDB.db");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Map<String, Object> row : rows) {
                ps.setString(1, (String) row.get("occur_date"));
                ps.setString(2, (String) row.get("vic_age_group"));
                ps.setString(3, (String) row.get("vic_sex"));
                ps.setString(4, (String) row.get("vic_race"));
                ps.addBatch();
            }

            ps.executeBatch();
            System.out.println("Inserted " + rows.size() + " victim records.");
        } catch (SQLException e) {
            System.out.println("insertRows() failed: " + e.getMessage());
        }
    }
}
