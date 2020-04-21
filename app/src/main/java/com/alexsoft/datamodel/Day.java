
package com.alexsoft.datamodel;

public class Day {

    @Override
    public String toString() {
        return "Day{" +
               "maxtemp_c=" + maxtemp_c +
               ", mintemp_c=" + mintemp_c +
               ", avgtemp_c=" + avgtemp_c +
               '}';
    }

    private double maxtemp_c;

    private double maxtemp_f;

    private double mintemp_c;

    private double mintemp_f;

    private double avgtemp_c;

    private double avgtemp_f;

    private double maxwind_mph;

    private double maxwind_kph;

    private double totalprecip_mm;

    private double totalprecip_in;

    private double avgvis_km;

    private double avgvis_miles;

    private double avghumidity;

    private Condition condition;

    public void setMaxtemp_c(double maxtemp_c) {
        this.maxtemp_c = maxtemp_c;
    }

    public double getMaxtemp_c() {
        return this.maxtemp_c;
    }

    public void setMaxtemp_f(double maxtemp_f) {
        this.maxtemp_f = maxtemp_f;
    }

    public double getMaxtemp_f() {
        return this.maxtemp_f;
    }

    public void setMintemp_c(double mintemp_c) {
        this.mintemp_c = mintemp_c;
    }

    public double getMintemp_c() {
        return this.mintemp_c;
    }

    public void setMintemp_f(double mintemp_f) {
        this.mintemp_f = mintemp_f;
    }

    public double getMintemp_f() {
        return this.mintemp_f;
    }

    public void setAvgtemp_c(double avgtemp_c) {
        this.avgtemp_c = avgtemp_c;
    }

    public double getAvgtemp_c() {
        return this.avgtemp_c;
    }

    public void setAvgtemp_f(double avgtemp_f) {
        this.avgtemp_f = avgtemp_f;
    }

    public double getAvgtemp_f() {
        return this.avgtemp_f;
    }

    public void setMaxwind_mph(double maxwind_mph) {
        this.maxwind_mph = maxwind_mph;
    }

    public double getMaxwind_mph() {
        return this.maxwind_mph;
    }

    public void setMaxwind_kph(double maxwind_kph) {
        this.maxwind_kph = maxwind_kph;
    }

    public double getMaxwind_kph() {
        return this.maxwind_kph;
    }

    public void setTotalprecip_mm(double totalprecip_mm) {
        this.totalprecip_mm = totalprecip_mm;
    }

    public double getTotalprecip_mm() {
        return this.totalprecip_mm;
    }

    public void setTotalprecip_in(double totalprecip_in) {
        this.totalprecip_in = totalprecip_in;
    }

    public double getTotalprecip_in() {
        return this.totalprecip_in;
    }

    public void setAvgvis_km(double avgvis_km) {
        this.avgvis_km = avgvis_km;
    }

    public double getAvgvis_km() {
        return this.avgvis_km;
    }

    public void setAvgvis_miles(double avgvis_miles) {
        this.avgvis_miles = avgvis_miles;
    }

    public double getAvgvis_miles() {
        return this.avgvis_miles;
    }

    public void setAvghumidity(double avghumidity) {
        this.avghumidity = avghumidity;
    }

    public double getAvghumidity() {
        return this.avghumidity;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Condition getCondition() {
        return this.condition;
    }
}
