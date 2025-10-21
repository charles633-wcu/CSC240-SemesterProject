package classapi.models;
//creating a class hiearchy for the project
public class Victim extends Person {
    
    public void Victim(String sex, String race, String ageRange){
        sex = super.sex;
        race = super.race;
        ageRange = super.ageRange;
    }
}
