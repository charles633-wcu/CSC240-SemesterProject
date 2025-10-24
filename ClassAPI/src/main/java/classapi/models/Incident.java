package classapi.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import classapi.models.Location_Object.Borough;
import classapi.models.Person_Object.Perpetrator;
import classapi.models.Person_Object.Victim;

public class Incident {

    private String occurDate;
    private Borough borough;

    private Victim victim;
    private Perpetrator perpetrator;

    //required by jackson
    public Incident() {}

    @JsonCreator
    public Incident(@JsonProperty("occur_date") String occurDate,
                    @JsonProperty("location") Borough borough,
                    @JsonProperty("victim") Victim victim,
                    @JsonProperty("perpetrator") Perpetrator perpetrator) {
        this.occurDate = occurDate;
        this.borough = borough;
        this.victim = victim;
        this.perpetrator = perpetrator;
    }

    public String getOccurDate() { return occurDate; }
    public void setOccurDate(String occurDate) { this.occurDate = occurDate; }

    public Borough getBorough() { return borough; }
    public void setBorough(Borough borough) { this.borough = borough; }

    public Victim getVictim() { return victim; }
    public void setVictim(Victim victim) { this.victim = victim; }

    public Perpetrator getPerpetrator() { return perpetrator; }
    public void setPerpetrator(Perpetrator perpetrator) { this.perpetrator = perpetrator; }

}
