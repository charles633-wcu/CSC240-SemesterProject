package com.wcupa.csc240.model.Location_Object;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Borough extends Location {



    @JsonCreator
    public Borough(@JsonProperty("boro") String boro) {
        this.name = boro;
        this.area = 0.0;    
    }



    public String getName(){
        return name;
    }

    public double getArea(){
        return area;
    }
    
}
