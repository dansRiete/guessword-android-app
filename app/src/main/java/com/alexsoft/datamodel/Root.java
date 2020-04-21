
package com.alexsoft.datamodel;

public class Root {
    private Location location;

    private Current current;

    private Forecast forecast;

    public void setLocation(Location location){
        this.location = location;
    }
    public Location getLocation(){
        return this.location;
    }
    public void setCurrent(Current current){
        this.current = current;
    }
    public Current getCurrent(){
        return this.current;
    }
    public void setForecast(Forecast forecast){
        this.forecast = forecast;
    }
    public Forecast getForecast() {
        return this.forecast;
    }
}
