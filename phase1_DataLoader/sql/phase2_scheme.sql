-- incidents table  by date
CREATE TABLE IF NOT EXISTS incidents (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    occur_date        TEXT    NOT NULL,          -- 'YYYY-MM-DD'format
    boro              TEXT    NOT NULL,          -- Borough name
    vic_sex           TEXT,                      -- Victim sex
    vic_age_group     TEXT,                      -- Victim age range
    perp_sex          TEXT,                      -- Perpetrator sex
    perp_age_group    TEXT                       -- Perpetrator age range
);

CREATE INDEX IF NOT EXISTS idx_incidents_date ON incidents(occur_date);
CREATE INDEX IF NOT EXISTS idx_incidents_boro ON incidents(boro);


-- temperature table by date
CREATE TABLE IF NOT EXISTS temperature_raw (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    date              TEXT    NOT NULL,          -- 'YYYY-MM-DD'
    max_temp          REAL    NOT NULL           -- Daily max temperature (°F or °C)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_temperature_date ON temperature_raw(date);


CREATE TABLE IF NOT EXISTS victims (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    incident_id       INTEGER,
    sex               TEXT,
    age_group         TEXT,
    FOREIGN KEY (incident_id) REFERENCES incidents(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS perpetrators (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    incident_id       INTEGER,
    sex               TEXT,
    age_group         TEXT,
    FOREIGN KEY (incident_id) REFERENCES incidents(id) ON DELETE CASCADE
);
