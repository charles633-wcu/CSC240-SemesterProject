package com.wcupa.csc240.api.classapi;

import static spark.Spark.get;

public class ClassEndpoints {

    private final ClassService classService = new ClassService();

    public void register() {

        // DAILY SUMMARY
        get("/api/class/daily-summary", (req, res) -> {
            res.type("application/json");

            String date = req.queryParams("date");
            if (date != null && !date.isBlank()) {
                return classService.getDailySummaryForDate(date);
            }

            return classService.getDailySummaryAll();
        });

        // COMBINED
        get("/api/class/combined", (req, res) -> {
            res.type("application/json");

            String date = req.queryParams("date");
            if (date != null && !date.isBlank()) {
                return classService.getCombinedForDate(date);
            }

            return classService.getCombinedAll();
        });
    }
}
