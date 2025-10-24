package classapi.models.Person_Object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Victim extends Person {
    private String sex;
    private String ageRange;
    private String date;

    //No-arg constructor — required for Jackson’s reflection instantiation
   // public Victim() {}

    @JsonCreator
    public Victim(
            @JsonProperty("sex") String sex,
            @JsonProperty("ageRange") String ageRange,
            @JsonProperty("date") String date) {
        super(sex, ageRange);
        this.sex = sex;
        this.ageRange = ageRange;
        this.date = date;
    }

    // required for Jackson to populate fields
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getAgeRange() { return ageRange; }
    public void setAgeRange(String ageRange) { this.ageRange = ageRange; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
