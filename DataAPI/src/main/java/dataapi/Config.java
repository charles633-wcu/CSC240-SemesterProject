package dataapi;

import java.nio.file.Paths;

/**
//config class that gives location of the databases, 
//works on any computer as long as databases lives 
//in the parent folder of DataAPI (CSC240-SemesterProject) folder
 */

public final class Config {

    // Port number for the DataAPI server
    public static final int PORT = 8081;

    // find the project root (parent of DataAPI)
    protected static final String PROJECT_ROOT = Paths.get(System.getProperty("user.dir"))
                                                    .toAbsolutePath()
                                                    .toString()
                                                    .endsWith("DataAPI")
        ? Paths.get(System.getProperty("user.dir")).getParent().toString()
        : System.getProperty("user.dir");

    // database urls
    public static final String INCIDENTS_DB_URL =
    "jdbc:sqlite:" + Paths.get(PROJECT_ROOT, "IncidentsDB.db").toString();
    public static final String PERPETRATORS_DB_URL =
    "jdbc:sqlite:" + Paths.get(PROJECT_ROOT, "PerpetratorsDB.db").toString();
    public static final String VICTIMS_DB_URL =
    "jdbc:sqlite:" + Paths.get(PROJECT_ROOT, "VictimsDB.db").toString();
    public static final String TEMPERATURE_DB_URL =
    "jdbc:sqlite:" + Paths.get(PROJECT_ROOT, "TemperatureDB.db").toString();

    private Config() {
        throw new AssertionError("Config class should not be instantiated.");
    }
}
