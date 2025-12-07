package com.wcupa.csc240.model.Person_Object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Victim extends Person {

    //No-arg constructor — required for Jackson’s reflection instantiation
    public Victim() {}

    @JsonCreator
    public Victim(
            @JsonProperty("vic_sex") String sex,
            @JsonProperty("vic_age_group") String ageRange) {
        super(sex, ageRange);
        this.sex = sex;
        this.ageRange = ageRange;
    }

    // required for Jackson to populate fields
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getAgeRange() { return ageRange; }
    public void setAgeRange(String ageRange) { this.ageRange = ageRange; }

}
