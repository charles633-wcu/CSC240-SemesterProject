package dataapi;

public class Main {
    public static void main(String[] args) {
        try {
            DataAPIServer.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
