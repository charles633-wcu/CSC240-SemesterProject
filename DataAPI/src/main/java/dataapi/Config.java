package dataapi;

//this constant is created so hardcoding the database path everytime

public class Config {
    public static final String DB_URL =
        "jdbc:sqlite:" + System.getProperty("user.dir") + "/CrimeTemperatureDB.db";
}
