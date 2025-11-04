# CSC240 Semester Project – NYC Crime Temperature Comparator

**Analyzes the relationship between temperature and violent crime in New York City**

---

## Project Evolution

This project was originally launched through a **PowerShell script** (`start-all.ps1` / `APISIX_START.ps1`) which manually:

* Set environment variables (like `DATA_API_URL`)
* Started individual APIs and APISIX
* Registered API routes using `curl` commands

While functional, that setup required Windows-specific scripting and manual orchestration.
It has now been **fully containerized** using **Docker Compose** and **Apache APISIX**, so all services start, connect, and route automatically on any machine with Docker installed.

---

## Overview

This project is a **three-tiered Java system** that compares daily **NYC crime incidents** and **temperature data** to find potential correlations.
It uses:

* **Java 21** and **Maven** for modular service management
* **SQLite** databases for persistent data storage
* **Apache APISIX** for routing and load balancing
* **Docker Compose** for deployment and orchestration

---

## Architecture

| Tier                                    | Module               | Port                             | Description                                                                                                                            |
| --------------------------------------- | -------------------- | -------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| **Tier 0 – Data Loader (from Phase 1)** | `phase1_DataLoader/` | *(pre-processing)*               | Loads historical NYC crime and weather data into SQLite databases.                                                                     |
| **Tier 1 – Data API**                   | `DataAPI/`           | `8081`                           | Serves raw data from SQLite. Endpoints:<br>• `/data/incidents`<br>• `/data/perpetrators`<br>• `/data/victims`<br>• `/data/temperature` |
| **Tier 2 – Class API**                  | `ClassAPI/`          | `8082`                           | Fetches data from DataAPI, aggregates it, and provides analytical summaries by date:<br>• `/summary/{YYYY-MM-DD}`                      |
| **Tier 3 – UI API**                     | `UIAPI/`             | `8083`                           | In progress. Will eventually serve a front-end interface for summary visualization.                                                    |
| **API Gateway – APISIX**                | `apisix`             | `9080` (public) / `9180` (admin) | Routes external requests:<br>• `/data/*` → DataAPI<br>• `/summary/*` → ClassAPI<br>• `/ui/*` → UIAPI                                   |

---

## Architecture Summary

```
Host (localhost:9080)
   │
   ▼
APISIX → DataAPI (8081)
       → ClassAPI (8082)
       → UIAPI (8083)
```

All containers run on a shared internal Docker network.
External requests are routed through APISIX via port **9080**.

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

---

## Environment Variables

Each container automatically receives the correct internal URLs through `docker-compose.yml`.

Example:

```yaml
DATA_API_URL: "http://apisix:9080/data"
```

This allows APIs to communicate internally without manual setup.

---

## Notes on Transition from PowerShell Script

Earlier versions of this project used a PowerShell script to:

* Launch containers manually
* Inject environment variables such as `DATA_API_URL`
* Register APISIX routes at runtime

Those steps are now **fully automated** inside Docker Compose:

* The `route-init` container registers routes automatically once APISIX is ready
* Environment variables are defined per service in `docker-compose.yml`

**Result:**

* No PowerShell scripts required
* Entire system launches with one command:

  ```bash
  docker compose up --build
  ```

---

## Databases

The following SQLite databases are automatically mounted into the DataAPI container:

* `IncidentsDB.db`
* `VictimsDB.db`
* `PerpetratorsDB.db`
* `TemperatureDB.db`

---

## Data Format Example

```json
{
  "date": "2023-12-01",
  "borough": "Manhattan",
  "temperature_fahrenheit": 44.6,
  "total_incidents": 128
}
```

---

## Summary

* Fully containerized using Docker Compose
* Automatic API routing via APISIX
* SQLite databases embedded and served dynamically
* PowerShell scripts deprecated (Docker now handles all orchestration)
* Ready for evaluation and local testing on any machine with Docker

---

## Instructor Notes

If you previously ran the project with the PowerShell script, please note:

* The new version runs entirely via Docker Compose
* No manual port configuration or environment setup is needed
* APISIX automatically registers `/data`, `/summary`, and `/ui` routes on startup
