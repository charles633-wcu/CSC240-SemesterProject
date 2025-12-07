package com.wcupa.csc240.model.Location_Object;

//defined as abstract because it's a blueprint that defines the scope of "borough"
public abstract class Location {

    protected String name;
    protected double area;
    
    public abstract String getName();
    public abstract double getArea();

    public String describe(){
        return  name + " covers" + area + " square miles.";
    }

    
    
}
