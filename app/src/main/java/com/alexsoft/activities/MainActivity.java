package com.alexsoft.activities;

import static com.alexsoft.enums.QuestionEditMode.CREATE;
import static com.alexsoft.enums.QuestionEditMode.UPDATE;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alexsoft.adapter.RecyclerAdapter;
import com.alexsoft.datamodel.Question;
import com.alexsoft.fragment.QuestionEditFragment;
import com.alexsoft.service.QuestionService;
import com.alexsoft.view.ContextMenuRecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.kuzko.aleksey.alexsoft.R;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String GUESSWORD_SERVER = "http://" + "192.168.0.201" + ":" + 24868 + "/api/";
    private static final String LOCALHOST = "http://" + "10.0.2.2" + ":" + 24868 + "/api/";
    private static final int MAX_DUPLICATE_RETRIES = 5;

    private final Retrofit retrofit = new Retrofit.Builder().baseUrl(GUESSWORD_SERVER)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    private final QuestionService questionService = retrofit.create(QuestionService.class);
    private final RecyclerAdapter recyclerAdapter = new RecyclerAdapter();
    private boolean isFetchingTiles = false;
    private Question lastAnsweredQuestion;
    private TextView serviceText;
    private DrawerLayout mDrawer;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportActionBar() != null) {
                boolean hasBackStack = getSupportFragmentManager().getBackStackEntryCount() > 0;
                getSupportActionBar().setDisplayHomeAsUpEnabled(hasBackStack);
            }
        });

        mDrawer = findViewById(R.id.drawer_layout);
        NavigationView nvDrawer = findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);
        serviceText = findViewById(R.id.serviceInfoText);

        mRecyclerView.setHasFixedSize(true);
        if (mRecyclerView.getItemAnimator() instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRecyclerView.setAdapter(recyclerAdapter);
        mRecyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int height = bottom - top;
            if (height > 0) {
                recyclerAdapter.setRecyclerViewHeight(height);
            }
        });
        registerForContextMenu(mRecyclerView);

        setupSwipeSupport(mRecyclerView);
        setStatisticFooter();
        fillTiles();
    }

    private void setupSwipeSupport(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                Question question = recyclerAdapter.getQuestion(position);
                if (question == null) {
                    return makeMovementFlags(0, 0);
                }
                if (question.getAnsweredCorrectly() == null) {
                    return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                }
                // Allow swiping to the opposite side to revert Know / Don't know
                int allowedDirection = question.getAnsweredCorrectly()
                        ? ItemTouchHelper.RIGHT
                        : ItemTouchHelper.LEFT;
                return makeMovementFlags(0, allowedDirection);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX != 0) {
                    View itemView = viewHolder.itemView;
                    GradientDrawable bg = new GradientDrawable();
                    float radius = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
                    bg.setCornerRadius(radius);
                    // Swipe Left (dX < 0) = Answered Right (Green); Swipe Right (dX > 0) = Answered Wrong (Red)
                    bg.setColor(dX < 0 ? Color.parseColor("#C8E6C9") : Color.parseColor("#FFCDD2"));
                    bg.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    bg.draw(c);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Question current = recyclerAdapter.getQuestion(position);
                // Clear ItemTouchHelper's recover animation so the tile snaps back into place at translationX = 0
                viewHolder.itemView.setTranslationX(0f);
                itemTouchHelper.attachToRecyclerView(null);
                itemTouchHelper.attachToRecyclerView(recyclerView);

                if (current == null) {
                    return;
                }
                // Swipe Left = Answered Right; Swipe Right = Answered Wrong
                boolean answeredRight = (direction == ItemTouchHelper.LEFT);
                handleTileSwiped(position, current, answeredRight);
            }
        };

        itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void handleTileSwiped(int position, Question current, boolean answeredRight) {
        boolean isRevert = current.getAnsweredCorrectly() != null;
        Double previousStudyPoints = isRevert && current.getPreviousStudyPoints() != null
                ? current.getPreviousStudyPoints()
                : current.getStudyPoints();
        Double previousStudyMultiplier = isRevert && current.getPreviousStudyPointsMultiplier() != null
                ? current.getPreviousStudyPointsMultiplier()
                : current.getStudyPointsMultiplier();

        current.setPreviousStudyPoints(previousStudyPoints);
        current.setPreviousStudyPointsMultiplier(previousStudyMultiplier);
        current.setAnsweredCorrectly(answeredRight);
        recyclerAdapter.notifyItemChanged(position);

        Observable<Question> answerCall;
        if (isRevert) {
            boolean isLastAnswered = lastAnsweredQuestion != null
                    && (lastAnsweredQuestion == current
                    || (current.getId() != null && current.getId().equals(lastAnsweredQuestion.getId())));
            if (isLastAnswered) {
                answerCall = answeredRight
                        ? questionService.previousRight()
                        : questionService.previousWrong();
            } else {
                current.setStudyPoints(previousStudyPoints);
                current.setStudyPointsMultiplier(previousStudyMultiplier);
                answerCall = answeredRight
                        ? questionService.rightAnswer(current)
                        : questionService.wrongAnswer(current);
            }
        } else {
            lastAnsweredQuestion = current;
            answerCall = answeredRight
                    ? questionService.rightAnswer(current)
                    : questionService.wrongAnswer(current);
        }

        answerCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        question -> {
                            question.setAnsweredCorrectly(answeredRight);
                            question.setPreviousStudyPoints(previousStudyPoints);
                            question.setPreviousStudyPointsMultiplier(previousStudyMultiplier);
                            question.setActualAnswer(current.getActualAnswer());
                            lastAnsweredQuestion = question;
                            recyclerAdapter.recordAnswer(current, question);
                            recyclerAdapter.updateQuestion(current, question);
                            setStatisticFooter();
                        },
                        error -> {
                            Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                );
    }

    public void replaceAnsweredTilesAction(View view) {
        recyclerAdapter.removeAnsweredQuestions();
        fillTiles();
    }

    public void newPhraseButtonAction(View view) {
        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        QuestionEditFragment frag = QuestionEditFragment.newInstance(CREATE);
        fTrans.add(R.id.frgmCont, frag);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    public void fillTiles() {
        if (isFetchingTiles || recyclerAdapter.getItemCount() >= RecyclerAdapter.TILE_COUNT) {
            return;
        }
        isFetchingTiles = true;
        fetchNextTile(MAX_DUPLICATE_RETRIES);
    }

    private void fetchNextTile(int duplicateRetriesLeft) {
        if (recyclerAdapter.getItemCount() >= RecyclerAdapter.TILE_COUNT) {
            isFetchingTiles = false;
            return;
        }
        questionService.getNext()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        question -> {
                            if (recyclerAdapter.getItemCount() >= RecyclerAdapter.TILE_COUNT) {
                                isFetchingTiles = false;
                                return;
                            }
                            if (question != null
                                    && recyclerAdapter.containsQuestionId(question.getId())
                                    && duplicateRetriesLeft > 0) {
                                fetchNextTile(duplicateRetriesLeft - 1);
                                return;
                            }
                            recyclerAdapter.addItem(question);
                            setStatisticFooter();
                            fetchNextTile(MAX_DUPLICATE_RETRIES);
                        },
                        error -> {
                            isFetchingTiles = false;
                            Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                );
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);

        ContextMenuRecyclerView.RecyclerContextMenuInfo info =
                (ContextMenuRecyclerView.RecyclerContextMenuInfo) menuInfo;
        Question question = info != null ? recyclerAdapter.getQuestion(info.position) : null;
        MenuItem answerItem = menu.findItem(R.id.context_menu_answer);
        if (answerItem != null) {
            answerItem.setVisible(question != null && Boolean.FALSE.equals(question.getAnsweredCorrectly()));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        Question question = recyclerAdapter.getQuestion(info.position);
        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        switch (item.getTitle().toString()) {
            case "Edit":
                if (question != null) {
                    QuestionEditFragment frag1 = QuestionEditFragment.newInstance(question, UPDATE);
                    fTrans.add(R.id.frgmCont, frag1);
                    fTrans.addToBackStack(null);
                    fTrans.commit();
                }
                break;
            case "Answer":
                if (question != null) {
                    showActualAnswerDialog(question);
                }
                break;
            case "Delete":
                if (question != null) {
                    questionService.delete(question.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    question1 -> {
                                        recyclerAdapter.remove(info.position);
                                        Toast.makeText(getApplicationContext(), "Question was deleted",
                                                Toast.LENGTH_SHORT).show();
                                        fillTiles();
                                    },
                                    error -> Toast.makeText(getApplicationContext(),
                                            String.format("Couldn't delete a question <%d>, reason: %s",
                                                    question.getId(), error.getLocalizedMessage()),
                                            Toast.LENGTH_LONG).show()
                            );
                }
                break;
            default:
                //nop
        }
        return false;
    }

    private void showActualAnswerDialog(Question question) {
        EditText input = new EditText(this);
        input.setHint("Your answer");
        if (question.getActualAnswer() != null) {
            input.setText(question.getActualAnswer());
            input.setSelection(question.getActualAnswer().length());
        }

        int marginPx = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics()));
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = marginPx;
        params.rightMargin = marginPx;
        input.setLayoutParams(params);
        container.addView(input);

        new AlertDialog.Builder(this)
                .setTitle("Record Actual Answer")
                .setView(container)
                .setPositiveButton("Save", (dialog, which) -> {
                    String typedAnswer = input.getText().toString().trim();
                    questionService.saveActualAnswer(question.getId(), typedAnswer)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    unused -> {
                                        question.setActualAnswer(typedAnswer);
                                        Toast.makeText(this, "Answer saved", Toast.LENGTH_SHORT).show();
                                    },
                                    error -> Toast.makeText(this,
                                            "Failed to save answer: " + error.getLocalizedMessage(),
                                            Toast.LENGTH_LONG).show()
                            );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        int id = menuItem.getItemId();
        if (id == R.id.nav_first_fragment) {
            Toast.makeText(MainActivity.this, "nav_first_fragment", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_second_fragment) {
            Toast.makeText(MainActivity.this, "nav_second_fragment", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_third_fragment) {
            Toast.makeText(MainActivity.this, "nav_third_fragment", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "default_fragment", Toast.LENGTH_LONG).show();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
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
        if (item.getItemId() == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setStatisticFooter() {
        String stat = String.format(
                "Answered: <b>%d</b>, <font color='%s'>Studied: %d</font>," +
                        " <font color='%s'>Right: %d%%</font>",
                recyclerAdapter.getTotalAnswered(),
                "#4CAF50",
                recyclerAdapter.getStudiedQuestions(),
                "#3F51B5",
                recyclerAdapter.getRightAnswerPercentage());
        serviceText.setText(Html.fromHtml(stat, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
    }

    public QuestionService getQuestionService() {
        return questionService;
    }

    public RecyclerAdapter getRecyclerAdapter() {
        return recyclerAdapter;
    }
}
