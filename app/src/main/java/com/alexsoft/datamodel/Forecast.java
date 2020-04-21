
package com.alexsoft.datamodel;

import java.util.List;
public class Forecast
{
    private List<Forecastday> forecastday;

    public void setForecastday(List<Forecastday> forecastday){
        this.forecastday = forecastday;
    }
    public List<Forecastday> getForecastday(){
        return this.forecastday;
    }
}
