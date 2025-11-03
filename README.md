**Analyzes relationship between temperature and violent crime in New York City**

**RUN WITH 'start-all.ps1' ON POWERSHELL 

This project is a **three-tiered data-class-ui program** that looks at relationships between daily **New York City crime incidents** and **temperature data**.  
The system is organized into three Java-based microservices using communication through **HTTP** and data stored in **SQLite**.  
An **Apache APISIX** gateway routes external requests to each API tier.
    -It routes from Ports 8081 (dataapi), 8082 (classapi), 8083 (uiapi, in progress), to Port 9080

---

## Overview

This project contains modular API design using basic java and sqlite.  
It looks at a dataset of daily crime and weather statistics to explore  correlations between temperature and violent incidents in NYC.

---

## Architecture

| Tier | Module | Port | Description |
|------|---------|------|-------------|
| **Tier 0 - DataLoader (from Phase 1)**|'phase1_DataLoader/'| Loads and verifies databases from NYC historical crime API and NYC historical weather API.
| **Tier 1 – Data API** | `DataAPI/` | 8081 (port so far until docker handles it) | Provides raw access to the SQLite database (CRUD). |
| **Tier 2 – Class API** | `ClassAPI/` | 8082 | Calls the Data API and applies business logic (creating objects out of raw data for later calculations such as stastical correlations). |
| **Tier 3 – UI API** | `UIAPI/` | 8083 | Allwos the user to call the class API. |

**API Gateway (APISIX)** routes:
- `/data/*` → Data API  
- `/api/*` → Class API  
- `/ui/*` → UI API  

---

## Features

- Pre-Loaded Databases include in case of external NYC API's are unavailable
- Modular Maven Java project
- SQLite integration with JDBC
- Inter-API communication using OkHttp library
- JSON serialization with Jackson library
- Configurable routes through Apache APISIX

---

## Running the Project

- Run START_ALL.ps1 on Powershell

### Prerequisites

- JDK 21 or later  
- Apache Maven 3.9+  
- Apache APISIX (optional for routing)
- GitBash (to run start-all)

### Build

```bash
mvn clean compile
