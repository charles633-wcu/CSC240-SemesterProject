package com.wcupa.csc240.api;

public class EndpointPrinter {

    private static final String BASE = "http://localhost:8080";

    public static void print() {

        System.out.println("\n Endpoints: ");

        System.out.println(BASE + "/hello");

        // DATA API
        System.out.println("\nData API");
        System.out.println(BASE + "/api/data/incidents");
        System.out.println(BASE + "/api/data/temperatures");

        // CLASS API
        System.out.println("\n Class API");
        System.out.println(BASE + "/api/class/daily-summary");
        System.out.println(BASE + "/api/class/daily-summary?date=YYYY-MM-DD");
        System.out.println(BASE + "/api/class/combined");
        System.out.println(BASE + "/api/class/combined?date=YYYY-MM-DD");

        // UI API 
        System.out.println("\n-- UI API --");
        System.out.println(BASE + "/ui");
        System.out.println(BASE + "/ui/daily-summary?date=YYYY-MM-DD");
        System.out.println(BASE + "/ui/daily-summary/all");
        System.out.println(BASE + "/ui/combined?date=YYYY-MM-DD");

        // APISIX gateway 
        System.out.println("http://localhost:9080/.....");
    }
}