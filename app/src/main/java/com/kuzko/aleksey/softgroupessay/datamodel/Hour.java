
package com.kuzko.aleksey.softgroupessay.datamodel;

import java.util.HashMap;
import java.util.Map;

public class Hour {

    private Integer timeEpoch;
    private String time;
    private Double tempC;
    private Double tempF;
    private Integer isDay;
    private Condition__ condition;
    private Double windMph;
    private Double windKph;
    private Integer windDegree;
    private String windDir;
    private Double pressureMb;
    private Double pressureIn;
    private Double precipMm;
    private Double precipIn;
    private Integer humidity;
    private Integer cloud;
    private Double feelslikeC;
    private Double feelslikeF;
    private Double windchillC;
    private Double windchillF;
    private Double heatindexC;
    private Double heatindexF;
    private Double dewpointC;
    private Double dewpointF;
    private Integer willItRain;
    private Integer willItSnow;
    private Double visKm;
    private Double visMiles;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getTimeEpoch() {
        return timeEpoch;
    }

    public void setTimeEpoch(Integer timeEpoch) {
        this.timeEpoch = timeEpoch;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getTempC() {
        return tempC;
    }

    public void setTempC(Double tempC) {
        this.tempC = tempC;
    }

    public Double getTempF() {
        return tempF;
    }

    public void setTempF(Double tempF) {
        this.tempF = tempF;
    }

    public Integer getIsDay() {
        return isDay;
    }

    public void setIsDay(Integer isDay) {
        this.isDay = isDay;
    }

    public Condition__ getCondition() {
        return condition;
    }

    public void setCondition(Condition__ condition) {
        this.condition = condition;
    }

    public Double getWindMph() {
        return windMph;
    }

    public void setWindMph(Double windMph) {
        this.windMph = windMph;
    }

    public Double getWindKph() {
        return windKph;
    }

    public void setWindKph(Double windKph) {
        this.windKph = windKph;
    }

    public Integer getWindDegree() {
        return windDegree;
    }

    public void setWindDegree(Integer windDegree) {
        this.windDegree = windDegree;
    }

    public String getWindDir() {
        return windDir;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public Double getPressureMb() {
        return pressureMb;
    }

    public void setPressureMb(Double pressureMb) {
        this.pressureMb = pressureMb;
    }

    public Double getPressureIn() {
        return pressureIn;
    }

    public void setPressureIn(Double pressureIn) {
        this.pressureIn = pressureIn;
    }

    public Double getPrecipMm() {
        return precipMm;
    }

    public void setPrecipMm(Double precipMm) {
        this.precipMm = precipMm;
    }

    public Double getPrecipIn() {
        return precipIn;
    }

    public void setPrecipIn(Double precipIn) {
        this.precipIn = precipIn;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getCloud() {
        return cloud;
    }

    public void setCloud(Integer cloud) {
        this.cloud = cloud;
    }

    public Double getFeelslikeC() {
        return feelslikeC;
    }

    public void setFeelslikeC(Double feelslikeC) {
        this.feelslikeC = feelslikeC;
    }

    public Double getFeelslikeF() {
        return feelslikeF;
    }

    public void setFeelslikeF(Double feelslikeF) {
        this.feelslikeF = feelslikeF;
    }

    public Double getWindchillC() {
        return windchillC;
    }

    public void setWindchillC(Double windchillC) {
        this.windchillC = windchillC;
    }

    public Double getWindchillF() {
        return windchillF;
    }

    public void setWindchillF(Double windchillF) {
        this.windchillF = windchillF;
    }

    public Double getHeatindexC() {
        return heatindexC;
    }

    public void setHeatindexC(Double heatindexC) {
        this.heatindexC = heatindexC;
    }

    public Double getHeatindexF() {
        return heatindexF;
    }

    public void setHeatindexF(Double heatindexF) {
        this.heatindexF = heatindexF;
    }

    public Double getDewpointC() {
        return dewpointC;
    }

    public void setDewpointC(Double dewpointC) {
        this.dewpointC = dewpointC;
    }

    public Double getDewpointF() {
        return dewpointF;
    }

    public void setDewpointF(Double dewpointF) {
        this.dewpointF = dewpointF;
    }

    public Integer getWillItRain() {
        return willItRain;
    }

    public void setWillItRain(Integer willItRain) {
        this.willItRain = willItRain;
    }

    public Integer getWillItSnow() {
        return willItSnow;
    }

    public void setWillItSnow(Integer willItSnow) {
        this.willItSnow = willItSnow;
    }

    public Double getVisKm() {
        return visKm;
    }

    public void setVisKm(Double visKm) {
        this.visKm = visKm;
    }

    public Double getVisMiles() {
        return visMiles;
    }

    public void setVisMiles(Double visMiles) {
        this.visMiles = visMiles;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
