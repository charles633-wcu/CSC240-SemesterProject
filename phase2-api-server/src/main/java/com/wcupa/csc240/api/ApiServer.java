package com.wcupa.csc240.api;

import com.wcupa.csc240.api.classapi.ClassEndpoints;
import com.wcupa.csc240.api.data.DataEndpoints;
import com.wcupa.csc240.api.frontend.FrontendEndpoints;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.port;

public class ApiServer {
    public static void main(String[] args) {

        port(8080);

        // cors fix, allow requests for phase3
        options("/*",
            (request, response) -> {

                String reqHeaders = request.headers("Access-Control-Request-Headers");
                if (reqHeaders != null) {
                    response.header("Access-Control-Allow-Headers", reqHeaders);
                }

                String reqMethod = request.headers("Access-Control-Request-Method");
                if (reqMethod != null) {
                    response.header("Access-Control-Allow-Methods", reqMethod);
                }

                return "OK";
            }
        );

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
        });

        get("/hello", (req, res) -> "spark server is running!");

        new DataEndpoints().register();
        new ClassEndpoints().register();
        new FrontendEndpoints().register();

        EndpointPrinter.print();
        System.out.println("Phase 2 API Server started with CORS enabled.");
    }
}
