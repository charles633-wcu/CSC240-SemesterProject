# NYC Crime and Temperature Analysis – Phase 2

This project is a **three-tiered data-class-ui program** that looks at relationships between daily **New York City crime incidents** and **temperature data**.  
The system is organized into three Java-based microservices using communication through **HTTP** and data stored in **SQLite**.  
An **Apache APISIX** gateway routes external requests to each API tier.

---

## Overview

This project contains modular API design using basic java and sqlite.  
It looks at a dataset of daily crime and weather statistics to explore  correlations between temperature and violent incidents in NYC.

---

## Architecture

| Tier | Module | Port | Description |
|------|---------|------|-------------|
| **Tier 1 – Data API** | `DataAPI/` | 8081 (port so far until docker handles it) | Provides raw access to the SQLite database (CRUD). |
| **Tier 2 – Class API** | `ClassAPI/` | 8082 | Calls the Data API and applies business logic (looking at correlations). |
| **Tier 3 – UI API** | `UIAPI/` | 8083 | Allwos the user to call the calss API. |

**API Gateway (APISIX)** routes:
- `/data/*` → Data API  
- `/api/*` → Class API  
- `/ui/*` → UI API  

---

## Features

- modular Maven Java project
- SQLite integration with JDBC
- Inter-API communication using OkHttp library
- JSON serialization with Jackson library
- Configurable routes through Apache APISIX

---

## Running the Project

### Prerequisites

- JDK 21 or later  
- Apache Maven 3.9+  
- Apache APISIX (optional for routing)
- GitBash (to run start-all)

### Build

```bash
mvn clean compile
