package csc240;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

// creates database of perps
public class DB_Creator_Perps {

    public static void ensureTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS PERPETRATORS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                occur_date TEXT,
                perp_age_group TEXT,
                perp_sex TEXT,
                perp_race TEXT
            );
            """;
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + System.getProperty("user.dir") + "/PerpetratorsDB.db");
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("PERPETRATORS table verified or created.");
        } catch (SQLException e) {
            System.out.println("ensureTable() failed: " + e.getMessage());
        }
    }

    public static void clearTable() {
        String sql = "DELETE FROM PERPETRATORS";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + System.getProperty("user.dir") + "/PerpetratorsDB.db");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("PERPETRATORS table cleared.");
        } catch (SQLException e) {
            System.out.println("clearTable() failed: " + e.getMessage());
        }
    }

    public static void insertRows(Collection<Map<String, Object>> rows) {
        String sql = "INSERT INTO PERPETRATORS (occur_date, perp_age_group, perp_sex, perp_race) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + System.getProperty("user.dir") + "/PerpetratorsDB.db");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Map<String, Object> row : rows) {
                ps.setString(1, (String) row.get("occur_date"));
                ps.setString(2, (String) row.get("perp_age_group"));
                ps.setString(3, (String) row.get("perp_sex"));
                ps.setString(4, (String) row.get("perp_race"));
                ps.addBatch();
            }

            ps.executeBatch();
            System.out.println("Inserted " + rows.size() + " perpetrator records.");
        } catch (SQLException e) {
            System.out.println("insertRows() failed: " + e.getMessage());
        }
    }
}
