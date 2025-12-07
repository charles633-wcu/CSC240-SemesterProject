
package com.wcupa.csc240.model.Person_Object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//"is-a Person" relationship
public class Perpetrator extends Person {
    

    public Perpetrator (){}

    @JsonCreator
    public Perpetrator(@JsonProperty("perp_sex") String sex, 
                       @JsonProperty("perp_age_group") String ageRange) {
        super(sex, ageRange);
        this.sex = sex;
        this.ageRange = ageRange;
    }


   
    // getters and setters
    public String getSex() {return this.sex;}
    public void setSex(String sex) {this.sex = sex;}

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }
}
