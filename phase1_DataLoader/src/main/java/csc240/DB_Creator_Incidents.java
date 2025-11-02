package csc240;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

//creates a database of incidents

public class DB_Creator_Incidents {
    public static final String DB_URL = "jdbc:sqlite:incidentsDB.db";

    //ensures table created if not already exist
    public static void ensureTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS INCIDENTS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                incident_key TEXT,
                occur_date TEXT,
                boro TEXT,
                precinct TEXT,
                statistical_murder_flag BOOLEAN,
                vic_sex TEXT,
                vic_race TEXT,
                vic_age_group TEXT,
                perp_sex TEXT,
                perp_race TEXT,
                perp_age_group TEXT
            );
            """;
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + System.getProperty("user.dir") + "/IncidentsDB.db");
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("INCIDENTS table created.");
        } catch (SQLException e) {
            System.out.println("ensureTable() failed: " + e.getMessage());
        }
    }

    //clears existing data so it's faster to do it again if new data arrives
    public static void clearTable() {
        String sql = "DELETE FROM INCIDENTS";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + System.getProperty("user.dir") + "/IncidentsDB.db");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("INCIDENTS table cleared.");
        } catch (SQLException e) {
            System.out.println("clearTable() failed: " + e.getMessage());
        }
    }

    //inserts all fetched rows into table
    public static void insertRows(Collection<Map<String, Object>> rows) {
        String sql = """
            INSERT INTO INCIDENTS (incident_key, occur_date, boro, precinct, statistical_murder_flag,
                                   vic_sex, vic_race, vic_age_group, perp_sex, perp_race, perp_age_group)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + System.getProperty("user.dir") + "/IncidentsDB.db");
             PreparedStatement ps = conn.prepareStatement(sql)) {



            for (Map<String, Object> row : rows) {
                ps.setString(1, (String) row.get("incident_key"));
                ps.setString(2, (String) row.get("occur_date"));
                ps.setString(3, (String) row.get("boro"));
                ps.setString(4, (String) row.get("precinct"));
                ps.setBoolean(5, (Boolean) row.getOrDefault("statistical_murder_flag", false));
                ps.setString(6, (String) row.get("vic_sex"));
                ps.setString(7, (String) row.get("vic_race"));
                ps.setString(8, (String) row.get("vic_age_group"));
                ps.setString(9, (String) row.get("perp_sex"));
                ps.setString(10, (String) row.get("perp_race"));
                ps.setString(11, (String) row.get("perp_age_group"));
                ps.addBatch();
            }

            ps.executeBatch();
          

            System.out.println("Inserted " + rows.size() + " incident records.");
        } catch (SQLException e) {
            System.out.println("insertRows() failed: " + e.getMessage());
        }
    }
}
