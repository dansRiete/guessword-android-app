package com.alexsoft;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsoft.datamodel.RecyclerItemClickListener;
import com.kuzko.aleksey.alexsoft.R;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final static String GUESSWORD_SERVER = "http://192.168.1.4:8080/api/";
    Retrofit retrofit = new Retrofit.Builder().baseUrl(GUESSWORD_SERVER)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    private QuestionService questionService = retrofit.create(QuestionService.class);
    private RecyclerAdapter recyclerAdapter = new RecyclerAdapter();
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, (view, position) ->
                        Toast.makeText(MainActivity.this, "Clicked position was:" + position, Toast.LENGTH_LONG).show()
                )
        );
        mRecyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(recyclerAdapter);

        questionService.getNext()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        question -> {
                            recyclerAdapter.addItem(question);
                            recyclerAdapter.notifyDataSetChanged();
                        },
                        error -> {
                            Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                );

    }

    public void iDontKnowButtonAction(View view) {

        questionService.wrongAnswer(recyclerAdapter.getCurrent())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        question -> {
                            recyclerAdapter.setCurrent(question);
                            questionService.getNext()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            question1 -> {
                                                recyclerAdapter.addItem(question1);
                                                recyclerAdapter.notifyDataSetChanged();
                                            },
                                            error -> {
                                                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                error.printStackTrace();
                                            }
                                    );
                            },
                        error -> {
                            Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                );
    }

    public void iKnowButtonAction(View view) {

        questionService.rightAnswer(recyclerAdapter.getCurrent())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        question -> {
                            recyclerAdapter.setCurrent(question);
                            questionService.getNext()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            question1 -> {
                                                recyclerAdapter.addItem(question1);
                                                recyclerAdapter.notifyDataSetChanged();
                                            },
                                            error -> {
                                                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                error.printStackTrace();
                                            }
                                    );
                        },
                        error -> {
                            Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                );
    }
}
