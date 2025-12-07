package com.wcupa.csc240.api.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataRepo {

    private static final String INCIDENTS_DB = Config.INCIDENTS_DB_URL;  
    private static final String TEMPERATURE_DB = Config.TEMPERATURE_DB_URL;

   //converts fake-null strings to real nulls
    private Object clean(Object value) {
        if (value == null) return null;

        if (value instanceof String) {
            String s = ((String) value).trim();
            if (s.equalsIgnoreCase("(null)") ||
                s.equalsIgnoreCase("null") ||
                s.isEmpty()) 
            {
                return null;
            }
        }

        return value;
    }

   //fetches all rows
    public List<Map<String, Object>> fetchAllRows(String dbUrl, String table) {
        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + table)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    Object raw = rs.getObject(i);
                    row.put(meta.getColumnName(i), clean(raw));  // CLEANING HERE
                }
                rows.add(row);
            }

        } catch (SQLException e) {
            System.err.println("DB ERROR (" + table + "): " + e.getMessage());
        }

        return rows;
    }

    public List<Map<String, Object>> getIncidents() {
        return fetchAllRows(INCIDENTS_DB, "INCIDENTS");
    }

    public List<Map<String, Object>> getTemperature() {
        return fetchAllRows(TEMPERATURE_DB, "TEMPERATURE_RAW");
    }
}
