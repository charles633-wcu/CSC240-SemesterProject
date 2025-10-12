DROP TABLE IF EXISTS crimeNYC_Daily;

CREATE TABLE crimeNYC_Daily (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    occur_date TEXT UNIQUE,                  
    total_incidents INTEGER,            
    vic_sex_countMale INTEGER,          
    vic_sex_countFemale INTEGER,        
    murder_true_count INTEGER,          
    murder_false_count INTEGER,         
    daily_high_celcius DOUBLE,          
    incident_keys TEXT                  
);
