package com.alexsoft.activities;

import static com.alexsoft.enums.QuestionEditMode.CREATE;
import static com.alexsoft.enums.QuestionEditMode.UPDATE;

import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsoft.adapter.RecyclerAdapter;
import com.alexsoft.datamodel.Question;
import com.alexsoft.fragment.QuestionEditFragment;
import com.alexsoft.listener.RecyclerItemClickListener;
import com.alexsoft.service.QuestionService;
import com.alexsoft.view.ContextMenuRecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.kuzko.aleksey.alexsoft.R;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String GUESSWORD_SERVER = "http://" + "173.233.98.197" + ":" + 24868 + "/api/";
    private static final String LOCALHOST = "http://" + "10.0.2.2" + ":" + 26452 + "/api/";

    private final Retrofit retrofit = new Retrofit.Builder().baseUrl(GUESSWORD_SERVER)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    private final QuestionService questionService = retrofit.create(QuestionService.class);
    private final RecyclerAdapter recyclerAdapter = new RecyclerAdapter();
    private boolean previousSelected;
    private TextView serviceText;
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This will display an Up icon (<-), we will replace it with hamburger later
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        mDrawer = findViewById(R.id.drawer_layout);
        NavigationView nvDrawer = findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);
        serviceText = findViewById(R.id.serviceInfoText);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, (view, position) ->
                        Toast.makeText(MainActivity.this, "Clicked position was:" + position, Toast.LENGTH_LONG).show()
                )
        );
        mRecyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(recyclerAdapter);
        registerForContextMenu(mRecyclerView);

        Button rightButton = (Button) findViewById(R.id.rigthButton);
        rightButton.setOnLongClickListener(v -> {
            questionService.previousRight()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            question -> {
                                question.setAnsweredCorrectly(true);
                                question.setPreviousStudyPoints(recyclerAdapter.getPrevious().getPreviousStudyPoints());
                                question.setPreviousStudyPointsMultiplier(recyclerAdapter.getPrevious().getPreviousStudyPointsMultiplier());
                                recyclerAdapter.setPrevious(question);
                                recyclerAdapter.notifyDataSetChanged();
                                setStatisticFooter();

                            },
                            error -> {
                                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                            }
                    );
            return true;
        });

        Button wrongButton = (Button) findViewById(R.id.leftButton);
        wrongButton.setOnLongClickListener(v -> {
            questionService.previousWrong()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            question -> {
                                question.setAnsweredCorrectly(false);
                                question.setPreviousStudyPoints(recyclerAdapter.getPrevious().getPreviousStudyPoints());
                                question.setPreviousStudyPointsMultiplier(recyclerAdapter.getPrevious().getPreviousStudyPointsMultiplier());
                                recyclerAdapter.setPrevious(question);
                                recyclerAdapter.notifyDataSetChanged();
                                setStatisticFooter();

                            },
                            error -> {
                                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                            }
                    );
            return true;
        });


        loadNext();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        Question question = recyclerAdapter.getQuestion(info.position);
        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        switch (item.getTitle().toString()){
            case "Edit":
                QuestionEditFragment frag1 = QuestionEditFragment.newInstance(question, UPDATE);
                fTrans.add(R.id.frgmCont, frag1);
                fTrans.addToBackStack(null);
                fTrans.commit();
                break;
            case "New":
                frag1 = QuestionEditFragment.newInstance(CREATE);
                fTrans.add(R.id.frgmCont, frag1);
                fTrans.addToBackStack(null);
                fTrans.commit();
                break;
            case "Delete":
                questionService.delete(question.getId())
                .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            question1 -> {
                                recyclerAdapter.remove(info.position);
                                Toast.makeText(getApplicationContext(), "Question was deleted",
                                        Toast.LENGTH_SHORT).show();
                                if (info.position == 0){
                                    nextButton();
                                } else {
                                    getRecyclerAdapter().notifyDataSetChanged();
                                }
                            },
                            error -> Toast.makeText(getApplicationContext(),
                                    String.format("Couldn't delete a question <%d>, reason: %s",
                                            question.getId(), error.getLocalizedMessage()) +
                                    error.getLocalizedMessage(), Toast.LENGTH_LONG).show()
                    );;
                break;
            default:
                //nop
        }
        return false;
    }



    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                Toast.makeText(MainActivity.this, "nav_first_fragment", Toast.LENGTH_LONG).show();
//                fragmentClass = FirstFragment.class;
                break;
            case R.id.nav_second_fragment:
                Toast.makeText(MainActivity.this, "nav_second_fragment", Toast.LENGTH_LONG).show();
//                fragmentClass = SecondFragment.class;
                break;
            case R.id.nav_third_fragment:
                Toast.makeText(MainActivity.this, "nav_third_fragment", Toast.LENGTH_LONG).show();
//                fragmentClass = ThirdFragment.class;
                break;
            default:
                Toast.makeText(MainActivity.this, "default_fragment", Toast.LENGTH_LONG).show();
//                fragmentClass = FirstFragment.class;
        }

        /*try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (item.getItemId() == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                                setStatisticFooter();

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
                                if (previousSelected) {
                                    previousSelected = false;
                                    recyclerAdapter.setPreviousSelected(false);
                                    recyclerAdapter.notifyDataSetChanged();
                                    setStatisticFooter();

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
                                setStatisticFooter();

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
                                    setStatisticFooter();
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

    public void nextButton() {
        if (previousSelected) {
            previousSelected = false;
            recyclerAdapter.setPreviousSelected(false);
            recyclerAdapter.notifyDataSetChanged();
        } else {
            loadNext();
        }
    }

    public void nextButton(View view) {
        nextButton();
    }

    private void setStatisticFooter() {
        String stat = String.format(
                "<font color='%s'>Studied today: %d</font>," +
                        " <font color='%s'>Right answers %d%%</font>",
                "#4CAF50",
                recyclerAdapter.getStudiedQuestions(),
                "#3F51B5",
                recyclerAdapter.getRightAnswerPercentage());
        serviceText.setText(Html.fromHtml(stat), TextView.BufferType.SPANNABLE);
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
                            setStatisticFooter();
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

    public QuestionService getQuestionService() {
        return questionService;
    }

    public RecyclerAdapter getRecyclerAdapter() {
        return recyclerAdapter;
    }
}
