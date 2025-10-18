# NYC Crime and Temperature Analysis – Phase 2

This project implements a **three-tiered service-oriented architecture (SOA)** that analyzes potential relationships between daily **New York City crime incidents** and **temperature data**.  
The system is organized into three Java-based microservices using **Spark Java**, with communication through **HTTP** and data stored in **SQLite**.  
An **Apache APISIX** gateway routes external requests to each API tier.

---

## Overview

The application demonstrates modular API design using Java, SQLite, and REST principles.  
It builds upon a dataset of daily crime and weather statistics to explore possible correlations between temperature and incident frequency.

---

## Architecture

| Tier | Module | Port | Description |
|------|---------|------|-------------|
| **Tier 1 – Data API** | `DataAPI/` | 8081 | Provides raw access to the SQLite database (CRUD operations). |
| **Tier 2 – Class API** | `ClassAPI/` | 8082 | Consumes the Data API and applies business logic (correlation analysis, data transformation). |
| **Tier 3 – UI API** | `UIAPI/` | 8083 | Consumes the Class API and provides data aggregated for visualization or dashboards. |

**API Gateway (APISIX)** routes:
- `/data/*` → Data API  
- `/api/*` → Class API  
- `/ui/*` → UI API  

---

## Features

- Multi-module Java project (Maven)
- REST microservices using Spark Java
- SQLite integration with JDBC
- Inter-API communication using OkHttp
- JSON serialization with Jackson
- Configurable routes through Apache APISIX

---

## Business Logic

The **Class API** introduces analytical behavior by transforming and combining raw datasets from the Data API.  
It supports:

1. **Correlation Analysis** – Computes the Pearson correlation between temperature and incident frequency.
2. **Graph Data Preparation** – Outputs datasets formatted for line charts (date, temperature, incidents).
3. **Summary Insights** – Optionally computes averages or highlights specific high-crime or high-temperature days.

These transformations represent the project's “business logic” layer, turning raw data into meaningful, interpretable insights.

---

## Running the Project

### Prerequisites

- JDK 21 or later  
- Apache Maven 3.9+  
- Apache APISIX (optional for routing)

### Build

```bash
mvn clean compile
