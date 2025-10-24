package dataapi;

/**
//config class that gives location of the databases, 
//works on any computer as long as databases lives 
//in the parent folder of DataAPI (CSC240-SemesterProject) folder
 */
public final class Config {

    // Port number for the DataAPI server
    public static final int PORT = 8081;


    // database urls
    public static final String INCIDENTS_DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/IncidentsDB.db";
    public static final String PERPETRATORS_DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/PerpetratorsDB.db";
    public static final String VICTIMS_DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/VictimsDB.db";
    public static final String TEMPERATURE_DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/TemperatureDB.db";


    private Config() {
        throw new AssertionError("Config class should not be instantiated.");
    }

}
