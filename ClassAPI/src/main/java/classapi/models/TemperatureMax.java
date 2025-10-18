package classapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TemperatureMax {
    public String date;
    public double daily_high_fahrenheit;

    @Override
    public String toString() {
        return String.format("%s | High: %.1f°F", date, daily_high_fahrenheit);
    }
}
