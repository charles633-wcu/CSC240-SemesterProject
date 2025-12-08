# CSC240 Semester Project – NYC Crime Temperature Comparator

**Analyzes the relationship between temperature and violent crime in New York City**

---

## Overview

This project is a **multi-module Java project** that compares daily **NYC crime incidents** and **temperature data** to find potential correlations.
It uses:

* **Java 21** and **Maven** for modular service management  
* **SQLite** databases for persistent data storage  
* **SparkJava** as the HTTP framework for the unified API server  
* **Apache APISIX** for routing and load balancing through a single public entrypoint  
* **Docker Compose** for deployment and orchestration of the API server, APISIX, and etcd  
* **Apache httpd** for serving the static website generated in Phase 3  
---

## Architecture

| Tier / Role                     | Module / Component        | Port(s)                         | Description                                                                                                   |
|---------------------------------|---------------------------|---------------------------------|---------------------------------------------------------------------------------------------------------------|
| **Data Loader**                    | phase1_DataLoader/        | (pre-processing)                | Loads, verifies, and writes NYC crime + temperature data into SQLite databases. Run once before backend.      |
| **Unified API Server (Spark)**      | phase2-api-server/        | 8080 (internal)                 | Single Java service containing all API layers: /api/data/* , /api/class/* , /ui/*                             |
| **UI Static Site Generator**        | phase3-site-generator/    | (static files)                  | Generates index.html, dashboard graph, summary pages, and JS. Consumes /ui/* endpoints.                       |
| **API Gateway – APISIX**            | apisix + route-init       | 9080 (public), 9180 (admin)     | Reverse proxy for all API traffic. Routes /api/data/* , /api/class/* , /ui/* to the Spark server.            |
| **SQLite Databases**                | *.db files in project root| (mounted into container)        | Stores IncidentsDB, VictimsDB, PerpetratorsDB, TemperatureDB. Used by the API server at runtime.             |

---

## Running the Project (Containerized)

### Prerequisites

* Install [Docker Desktop](https://www.docker.com/products/docker-desktop)
* Clone this repository:

  ```bash
  git clone https://github.com/charles633-wcu/CSC240-SemesterProject.git
  cd CSC240-SemesterProject
  ```

### Start All Services

```bash
docker compose up --build
```

cmd, run phase3
mvn -pl phase3-site-generator -Dexec.mainClass=com.wcupa.csc240.generator.SiteGenerator org.codehaus.mojo:exec-maven-plugin:3.1.0:java

### Access the APIs

| Service      | Example Endpoint                                                                               | Description                                                           |
| ------------ | ---------------------------------------------------------------------------------------------- | --------------------------------------------------------------------- |
| **DataAPI**  | [http://localhost:9080/data/incidents](http://localhost:9080/data/incidents)                   | Returns all NYC crime incidents                                       |
|              | [http://localhost:9080/data/perpetrators](http://localhost:9080/data/perpetrators)             | Returns perpetrator details                                           |
|              | [http://localhost:9080/data/victims](http://localhost:9080/data/victims)                       | Returns victim data                                                   |
|              | [http://localhost:9080/data/temperature](http://localhost:9080/data/temperature)               | Returns temperature data                                              |
| **ClassAPI** | [http://localhost:9080/summary/{YYYY-MM-DD}](http://localhost:9080/summary/{YYYY-MM-DD})       | Returns aggregated incidents and temperature summary for a given date |
| **UIAPI**    | [http://localhost:9080/ui/view?{YYYY-MM-DD}](http://localhost:9080/ui/view?{YYYY-MM-DD}) | (In progress) Will display formatted results for a given date         |

To stop all containers:

```bash
docker compose down
```
### Access the Website

The  site is generated into:
```
phase3-site-generator/target/site/
```
If **Apache httpd** is configured with:
```
DocumentRoot ".../phase3-site-generator/target/site"
```

Then:

* Open CMD
* Navigate to the root: CSC240-SemesterProject 
* Run: mvn -pl phase3-site-generator -Dexec.mainClass=com.wcupa.csc240.generator.SiteGenerator org.codehaus.mojo:exec-maven-plugin:3.1.0:java

Then the site is available at:

**http://localhost/**

### Available Pages
| Page | URL | Purpose |
|------|-----|---------|
| **Homepage (Graph Dashboard)** | <http://localhost/> | Scatterplot + regression line (temperature vs incidents) |
| **Daily Summary Lookup** | <http://localhost/summary.html> | Date-based lookup of aggregated crime + temperature |
| **Combined Lookup** | <http://localhost/combined.html> | Shows every incident paired with temperature |
---

## Databases

The following SQLite databases are mounted into the container:

* `IncidentsDB.db`
* `VictimsDB.db`
* `PerpetratorsDB.db`
* `TemperatureDB.db`

---

## Summary

This project is a three-phase system that:
1. Fetches, verifies, and creates SQLite databases from external historical NYC crime and temperature APIs  
2. Creates a containerized **Spark** server that exposes API layers: Data, Class, UI, for front-end consumption and routed with **Apache APISIX**
3. Generates static HTML websites that use **JavaScript** to fetch data from API layer endpoints
4. These websites are served by **Apache httpd** with the index page at **http://localhost/**  


### How the Technologies Fit Together

- **SQLite**  
  Stores all raw data built by Phase 1.

- **SparkJava**  
  Runs the API server in Phase 2 and handles all routing inside the container.

- **Docker Compose**  
  Starts the API server container, APISIX, and etcd, and wires everything together so the system runs consistently on any machine.

- **Apache APISIX**  
  Acts as the gateway. It exposes port 9080 and forwards requests to the Spark server.

- **Apache httpd**  
  Serves the static HTML generated by Phase 3.

---
