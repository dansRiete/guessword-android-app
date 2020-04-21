package com.alexsoft;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Aleks on 20.03.2017.
 */

interface AqiService {

    @GET("/")
    Observable<String> getForecast();
}
