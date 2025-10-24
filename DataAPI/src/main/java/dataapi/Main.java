package dataapi;

public class Main {

    public static void main(String[] args) throws Exception{


        System.out.println("Using DB paths:");
        System.out.println("  Incidents: " + Config.INCIDENTS_DB_URL);
        System.out.println("  Victims: " + Config.VICTIMS_DB_URL);
        System.out.println("  Perpetrators: " + Config.PERPETRATORS_DB_URL);
        System.out.println("  Temperature: " + Config.TEMPERATURE_DB_URL);

        DataAPIServer.execute();
    }
    
}
