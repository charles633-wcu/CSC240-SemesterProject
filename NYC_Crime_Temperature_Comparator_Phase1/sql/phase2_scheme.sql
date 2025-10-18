DROP TABLE IF EXISTS crime_daily;
DROP TABLE IF EXISTS weather_daily;
DROP VIEW IF EXISTS vw_crime_weather;

CREATE TABLE crime_daily (
  occur_date TEXT PRIMARY KEY,
  total_incidents INTEGER,
  vic_sex_countMale INTEGER,
  vic_sex_countFemale INTEGER,
  murder_true_count INTEGER,
  murder_false_count INTEGER,
  incident_keys TEXT
);

CREATE TABLE weather_daily (
  occur_date TEXT PRIMARY KEY,
  daily_high_fahrenheit DOUBLE
);

CREATE VIEW vw_crime_weather AS
SELECT c.occur_date,
       c.total_incidents,
       c.vic_sex_countMale,
       c.vic_sex_countFemale,
       c.murder_true_count,
       c.murder_false_count,
       w.daily_high_fahrenheit
FROM crime_daily c
LEFT JOIN weather_daily w USING (occur_date);

CREATE INDEX IF NOT EXISTS idx_c_date ON crime_daily(occur_date);
CREATE INDEX IF NOT EXISTS idx_w_date ON weather_daily(occur_date);
