package com.alexsoft;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alexsoft.datamodel.RecyclerItemClickListener;
import com.kuzko.aleksey.alexsoft.R;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final static String AQI_BASE_URL = "http://192.168.1.10";
    Retrofit retrofit = new Retrofit.Builder().baseUrl(AQI_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    private AqiService aqiService = retrofit.create(AqiService.class);
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
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Toast.makeText(MainActivity.this, "Clicked position was:" + position, Toast.LENGTH_LONG).show();
                    }
                })
        );
        mRecyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void buttonClicked(View view) {

        String enteredCity = editTextCity.getText().toString().equals("") ? "Chernivtsi" : editTextCity.getText().toString();
        int enteredDaysNumber;

        if(editTextDaysNumber.getText().toString().equals("") || Integer.parseInt(editTextDaysNumber.getText().toString()) > 10){
            enteredDaysNumber = 3;
        }else {
            enteredDaysNumber = Integer.parseInt(editTextDaysNumber.getText().toString());
        }

        aqiService.getForecast()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        weather -> mRecyclerView.setAdapter(new RecyclerAdapter(weather)),
                        error -> {
                            Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                );
    }
}
