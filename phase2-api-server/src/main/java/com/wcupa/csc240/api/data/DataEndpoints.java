package com.wcupa.csc240.api.data;

import static spark.Spark.get;


public class DataEndpoints {

    private final DataService dataService = new DataService();

    public void register() {

        get("/api/data/incidents", (req, res) -> {
            res.type("application/json");
            return dataService.getIncidentsJson();
        });

        get("/api/data/temperatures", (req, res) -> {
            res.type("application/json");
            return dataService.getTemperatureJson();
        });
    }
}
