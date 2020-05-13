package com.alexsoft;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsoft.datamodel.Question;
import com.alexsoft.listener.RecyclerItemClickListener;
import com.alexsoft.service.QuestionService;
import com.kuzko.aleksey.alexsoft.R;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String LOCALHOST = "10.0.2.2";
    private static final String NP300 = "192.168.1.5";
    private final static String GUESSWORD_SERVER = "http://" + LOCALHOST + ":8080/api/";

    Retrofit retrofit = new Retrofit.Builder().baseUrl(GUESSWORD_SERVER)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    private QuestionService questionService = retrofit.create(QuestionService.class);
    private RecyclerAdapter recyclerAdapter = new RecyclerAdapter();
    private RecyclerView mRecyclerView;
    private boolean previousSelected;
    TextView serviceText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        serviceText = findViewById(R.id.serviceInfoText);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, (view, position) ->
                        Toast.makeText(MainActivity.this, "Clicked position was:" + position, Toast.LENGTH_LONG).show()
                )
        );
        mRecyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(recyclerAdapter);

        loadNext();

    }

    public void iDontKnowButtonAction(View view) {

        Question current = recyclerAdapter.getCurrent();
        if (previousSelected && current.getAnsweredCorrectly() != null) {
            questionService.previousWrong()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            question -> {
                                question.setAnsweredCorrectly(false);
                                question.setPreviousStudyPoints(current.getPreviousStudyPoints());
                                question.setPreviousStudyPointsMultiplier(current.getPreviousStudyPointsMultiplier());
                                recyclerAdapter.setCurrent(question);
                                previousSelected = false;
                                recyclerAdapter.setPreviousSelected(false);
                                recyclerAdapter.notifyDataSetChanged();
                                serviceText.setText(String.format("Right answers %d%%", recyclerAdapter.getRightAnswerPercentage()));

                            },
                            error -> {
                                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                            }
                    );
        } else {
            questionService.wrongAnswer(current)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            question -> {
                                question.setAnsweredCorrectly(false);
                                question.setPreviousStudyPoints(current.getStudyPoints());
                                question.setPreviousStudyPointsMultiplier(current.getStudyPointsMultiplier());
                                recyclerAdapter.setCurrent(question);
                                if(previousSelected) {
                                    previousSelected = false;
                                    recyclerAdapter.setPreviousSelected(false);
                                    recyclerAdapter.notifyDataSetChanged();
                                    serviceText.setText(String.format("Right answers %d%%", recyclerAdapter.getRightAnswerPercentage()));

                                } else {
                                    loadNext();
                                }
                            },
                            error -> {
                                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                            }
                    );
        }
    }

    public void iKnowButtonAction(View view) {

        Question current = recyclerAdapter.getCurrent();
        if (previousSelected && current.getAnsweredCorrectly() != null) {
            questionService.previousRight()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            question -> {
                                question.setAnsweredCorrectly(true);
                                question.setPreviousStudyPoints(current.getPreviousStudyPoints());
                                question.setPreviousStudyPointsMultiplier(current.getPreviousStudyPointsMultiplier());
                                recyclerAdapter.setCurrent(question);
                                previousSelected = false;
                                recyclerAdapter.setPreviousSelected(false);
                                recyclerAdapter.notifyDataSetChanged();
                                serviceText.setText(String.format("Right answers %d%%", recyclerAdapter.getRightAnswerPercentage()));

                            },
                            error -> {
                                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                            }
                    );
        } else {
            questionService.rightAnswer(current)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            question -> {
                                question.setAnsweredCorrectly(true);
                                question.setPreviousStudyPoints(current.getStudyPoints());
                                question.setPreviousStudyPointsMultiplier(current.getStudyPointsMultiplier());
                                recyclerAdapter.setCurrent(question);
                                if(previousSelected) {
                                    previousSelected = false;
                                    recyclerAdapter.setPreviousSelected(false);
                                    recyclerAdapter.notifyDataSetChanged();
                                    serviceText.setText(String.format("Right answers %d%%", recyclerAdapter.getRightAnswerPercentage()));
                                } else {
                                    loadNext();
                                }
                            },
                            error -> {
                                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                            }
                    );
        }
    }

    public void nextButton(View view) {
        if (previousSelected) {
            previousSelected = false;
            recyclerAdapter.setPreviousSelected(false);
            recyclerAdapter.notifyDataSetChanged();
        } else {
            loadNext();
        }
    }

    private void loadNext() {
        previousSelected = false;
        recyclerAdapter.setPreviousSelected(false);
        questionService.getNext()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        question -> {
                            recyclerAdapter.addItem(question);
                            recyclerAdapter.notifyDataSetChanged();
                            serviceText.setText(String.format("Right answers %d%%", recyclerAdapter.getRightAnswerPercentage()));
                        },
                        error -> {
                            Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                );
    }

    public void previousButton(View view) {
        this.previousSelected = true;
        recyclerAdapter.setPreviousSelected(true);
        recyclerAdapter.notifyDataSetChanged();
    }
}
