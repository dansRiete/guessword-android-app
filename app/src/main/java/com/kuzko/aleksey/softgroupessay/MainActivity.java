package com.kuzko.aleksey.softgroupessay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final static String BASE_APIXU_URL = "http://api.apixu.com/v1/";
    private WeatherService weatherService;
    private RecyclerView mRecyclerView;
    private EditText editTextCity;
    private EditText editTextDaysNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = (EditText) findViewById(R.id.editTextCity);
        editTextDaysNumber = (EditText) findViewById(R.id.editTextDaysNumber);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Retrofit apixuWeather = new Retrofit.Builder().baseUrl(BASE_APIXU_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        weatherService = apixuWeather.create(WeatherService.class);

    }

    public void buttonClicked(View view) {

        String enteredCity = editTextCity.getText().toString().equals("") ? "Chernivtsi" : editTextCity.getText().toString();
        int enteredDaysNumber;

        if(editTextDaysNumber.getText().toString().equals("") || Integer.valueOf(editTextDaysNumber.getText().toString()) > 10){
            enteredDaysNumber = 3;
        }else {
            enteredDaysNumber = Integer.valueOf(editTextDaysNumber.getText().toString());
        }

        weatherService.getForecast(enteredCity, enteredDaysNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        weather -> mRecyclerView.setAdapter(new RecyclerAdapter(weather.getForecast().getForecastday())),
                        error -> Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
