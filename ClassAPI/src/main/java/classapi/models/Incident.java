package classapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Incident {
    public String occur_date;
    public int total_incidents;
    public int vic_sex_countMale;
    public int vic_sex_countFemale;
    public int murder_true_count;
    public int murder_false_count;

    @Override
    public String toString() {
        return String.format("%s | Incidents: %d | Murders: %d | Male Victims: %d | Female Victims: %d",
            occur_date, total_incidents, murder_true_count, vic_sex_countMale, vic_sex_countFemale);
    }
}
