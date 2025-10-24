
package classapi.models.Person_Object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//"is-a Person" relationship
public class Perpetrator extends Person {
    
    private String date;

    public Perpetrator (){}

    @JsonCreator
    public Perpetrator(@JsonProperty("sex") String sex, 
                       @JsonProperty("ageRange") String ageRange, 
                       @JsonProperty("date") String date) {
        super(sex, ageRange);
        this.date = date;
        this.ageRange = ageRange;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
