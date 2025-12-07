package com.wcupa.csc240.model.Temperature;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TemperatureMax_Date extends Temperature {

    private String date;

    // required by jackson
    public TemperatureMax_Date() {
        super(0.0);
    }

    // Explicit constructor (for manual creation or @JsonCreator)
    @JsonCreator
    public TemperatureMax_Date(
            @JsonProperty("date") String date,
            @JsonProperty("temperature_max") double max_temp) {
        this.date = date;
        super.temperature = max_temp;
    }

    public String getDate() { return date; }
    public double getTemperature() { return temperature; }
}
