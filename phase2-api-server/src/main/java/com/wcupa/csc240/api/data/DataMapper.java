package com.wcupa.csc240.api.data;

import java.util.Map;

import com.wcupa.csc240.model.Incident;
import com.wcupa.csc240.model.Location_Object.Borough;
import com.wcupa.csc240.model.Person_Object.Perpetrator;
import com.wcupa.csc240.model.Person_Object.Victim;
import com.wcupa.csc240.model.Temperature.TemperatureMax_Date;

public class DataMapper {

    public static Incident mapIncident(Map<String,Object> row) {
        
        Incident incident = new Incident(
            (String) row.get("occur_date"),

            new Borough((String) row.get("boro")),
            new Victim(
                (String) row.get("vic_sex"),
                (String) row.get("vic_age_group")
            ),

            new Perpetrator(
                (String) row.get("perp_sex"),
                (String) row.get("perp_age_group")
            )
        );
        return incident;
    }

    public static TemperatureMax_Date mapTemp(Map<String,Object> row) {
        return new TemperatureMax_Date(
            (String) row.get("date"),
            (double) row.get("temperature_max")
        );
    }
}
