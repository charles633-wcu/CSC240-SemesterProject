package com.wcupa.csc240.api.frontend;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ClassApiClient {

    private static final String CLASS_API_BASE = "http://localhost:8080/api/class";

    private final OkHttpClient client = new OkHttpClient();

    public String get(String path) {
        try {
            Request req = new Request.Builder()
                    .url(CLASS_API_BASE + path)
                    .build();

            Response res = client.newCall(req).execute();
            return res.body().string();

        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
}
