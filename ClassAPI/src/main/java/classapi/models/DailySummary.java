package classapi.models;

public class DailySummary {
    public Incident incident;
    public TemperatureMax temperature;

    public String getDate() {
        return (incident != null) ? incident.occur_date : 
               (temperature != null ? temperature.date : null);
    }

    @Override
    public String toString() {
        return String.format("%s | Incidents: %d | High: %.1f°F",
            getDate(),
            (incident != null ? incident.total_incidents : 0),
            (temperature != null ? temperature.daily_high_fahrenheit : Double.NaN)
        );
    }
}
