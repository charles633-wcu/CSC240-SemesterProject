package com.wcupa.csc240.model.Person_Object;

//creating a class hierarchy for the project
public abstract class Person {

    protected String sex;
    protected String ageRange;

    public Person() {}
    
    public Person(String sex, String ageRange){
        this.sex = sex;
        this.ageRange = ageRange;
    }
    
    // getters and setters
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }
    
}
