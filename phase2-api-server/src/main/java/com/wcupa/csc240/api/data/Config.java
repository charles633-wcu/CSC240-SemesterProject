package com.wcupa.csc240.api.data;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Config {

    public static final String PROJECT_ROOT;

    public static final String INCIDENTS_DB_URL;
    public static final String TEMPERATURE_DB_URL;

    static {
        Path cwd = Paths.get("").toAbsolutePath();
        Path root = cwd;

        while (root != null && !root.resolve("IncidentsDB.db").toFile().exists()) {
            root = root.getParent();
        }

        if (root == null) {
            throw new RuntimeException("ERROR: Could not locate project root (missing IncidentsDB.db).");
        }

        PROJECT_ROOT = root.toString();

        INCIDENTS_DB_URL =
            "jdbc:sqlite:" + root.resolve("IncidentsDB.db").toString();

        TEMPERATURE_DB_URL =
            "jdbc:sqlite:" + root.resolve("TemperatureDB.db").toString();

        System.out.println("== DB PATHS RESOLVED ==");
        System.out.println("Project root:       " + PROJECT_ROOT);
        System.out.println("Incidents DB:       " + INCIDENTS_DB_URL);
        System.out.println("Temperature DB:     " + TEMPERATURE_DB_URL);
        System.out.println("======================");
    }

    private Config() {
        throw new AssertionError("Config should not be instantiated.");
    }
}
