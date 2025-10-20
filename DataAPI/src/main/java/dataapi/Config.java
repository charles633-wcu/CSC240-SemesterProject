package dataapi;

public class Config {
    public static final String DB_URL =
        "jdbc:sqlite:" + System.getProperty("user.dir") + "/CrimeTemperatureDB.db";
}
