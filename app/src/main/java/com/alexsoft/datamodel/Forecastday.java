
package com.alexsoft.datamodel;

import java.util.List;

public class Forecastday
{
    private String date;

    private int date_epoch;

    private Day day;

    private Astro astro;

    private List<Hour> hour;

    public void setDate(String date){
        this.date = date;
    }
    public String getDate(){
        return this.date;
    }
    public void setDate_epoch(int date_epoch){
        this.date_epoch = date_epoch;
    }
    public int getDate_epoch(){
        return this.date_epoch;
    }
    public void setDay(Day day){
        this.day = day;
    }
    public Day getDay(){
        return this.day;
    }
    public void setAstro(Astro astro){
        this.astro = astro;
    }
    public Astro getAstro(){
        return this.astro;
    }
    public void setHour(List<Hour> hour){
        this.hour = hour;
    }
    public List<Hour> getHour(){
        return this.hour;
    }

    @Override
    public String toString() {
        return "Forecastday{" +
               "date='" + date + '\'' +
               ", day=" + day +
               '}';
    }
}
