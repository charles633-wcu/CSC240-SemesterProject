package classapi.models.Temperature;

public class Temperature {

    protected double temperature;

    public Temperature() {}

    public Temperature(double temperature){
        temperature = this.temperature;
    }
    
    public double getTemperature(){
        return temperature;
    }

    public void setTemperature(double temperature){
        this.temperature = temperature;
    }
}
