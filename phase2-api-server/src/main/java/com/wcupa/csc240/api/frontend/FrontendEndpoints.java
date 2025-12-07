package com.wcupa.csc240.api.frontend;

import static spark.Spark.get;

public class FrontendEndpoints {

    private final ClassApiClient client = new ClassApiClient();

    public void register() {

        // Simple UI test endpoint
        get("/ui", (req, res) -> "UI API is running.");

        // /ui/daily-summary?date=YYYY-MM-DD
        get("/ui/daily-summary", (req, res) -> {
            res.type("application/json");

            String date = req.queryParams("date");
            if (date == null) {
                return "{\"error\":\"Missing ?date=YYYY-MM-DD\"}";
            }

            return client.get("/daily-summary?date=" + date);
        });

        // /ui/daily-summary/all
        get("/ui/daily-summary/all", (req, res) -> {
            res.type("application/json");
            return client.get("/daily-summary");
        });

        // /ui/combined?date=YYYY-MM-DD
        get("/ui/combined", (req, res) -> {
            res.type("application/json");

            String date = req.queryParams("date");
            if (date == null) {
                return "{\"error\":\"Missing ?date=YYYY-MM-DD\"}";
            }

            return client.get("/combined?date=" + date);
        });
    }
}
