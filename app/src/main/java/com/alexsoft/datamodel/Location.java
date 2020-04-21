
package com.alexsoft.datamodel;

import java.util.HashMap;
import java.util.Map;

public class Location {

    private String name;
    private String region;
    private String country;
    private Double lat;
    private Double lon;
    private String tzId;
    private Integer localtimeEpoch;
    private String localtime;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getTzId() {
        return tzId;
    }

    public void setTzId(String tzId) {
        this.tzId = tzId;
    }

    public Integer getLocaltimeEpoch() {
        return localtimeEpoch;
    }

    public void setLocaltimeEpoch(Integer localtimeEpoch) {
        this.localtimeEpoch = localtimeEpoch;
    }

    public String getLocaltime() {
        return localtime;
    }

    public void setLocaltime(String localtime) {
        this.localtime = localtime;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
