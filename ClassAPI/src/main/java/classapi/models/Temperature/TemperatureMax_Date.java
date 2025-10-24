package classapi.models.Temperature;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TemperatureMax_Date {

    private String date;
    private double temp;

    // required by jackson
    public TemperatureMax_Date() {}

    // Explicit constructor (for manual creation or @JsonCreator)
    @JsonCreator
    public TemperatureMax_Date(
            @JsonProperty("date") String date,
            @JsonProperty("max_temp") double max_temp) {
        this.date = date;
        this.temp = max_temp;
    }

    public String getDate() { return date; }
    public double getTemperature() { return temp; }
}
