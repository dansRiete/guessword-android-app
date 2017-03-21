package com.kuzko.aleksey.softgroupessay;

import com.kuzko.aleksey.softgroupessay.datamodel.Root;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Aleks on 20.03.2017.
 */

interface WeatherService {
    String APIXU_KEY = "5b9c5cb2208948cc84c162948171302";

    @GET("forecast.json?key=" + APIXU_KEY)
    Observable<Root> getForecast(@Query("q") String city, @Query("days") int days);
}
