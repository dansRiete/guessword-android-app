package com.alexsoft.fragment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.RelativeLayout.BELOW;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.alexsoft.activities.MainActivity;
import com.alexsoft.adapter.RecyclerAdapter;
import com.alexsoft.enums.QuestionEditMode;
import com.alexsoft.datamodel.Question;
import com.alexsoft.datamodel.Word;
import com.kuzko.aleksey.alexsoft.R;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.alexsoft.datamodel.Question.ENGLISH_KEY;
import static com.alexsoft.datamodel.Question.RUSSIAN_KEY;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QuestionEditFragment extends Fragment {

    public static final String QUESTION_KEY = "Question";
    public static final String QUESTION_EDIT_MODE_KEY = "QuestionEditMode";
    private Question question;
    private List<Question> allQuestions;
    private QuestionEditMode questionEditMode;
    RelativeLayout relativeLayout;

    public static QuestionEditFragment newInstance(QuestionEditMode questionEditMode) {
        return newInstance(null, questionEditMode);
    }

    public static QuestionEditFragment newInstance(Question question, QuestionEditMode questionEditMode) {
        QuestionEditFragment fragment = new QuestionEditFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUESTION_KEY, question);
        bundle.putSerializable(QUESTION_EDIT_MODE_KEY, questionEditMode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.question_edit_fragment, null);
        AtomicInteger maxId = new AtomicInteger(1);
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(QUESTION_KEY);
            questionEditMode = (QuestionEditMode) getArguments().getSerializable(QUESTION_EDIT_MODE_KEY);
        }
        relativeLayout = view.findViewById(R.id.editQuestionLayout);

        ((MainActivity) getActivity()).getQuestionService().getAllQuestions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(questions -> {
                    this.allQuestions = questions;
                });

        TextView hintsTextView = new TextView(getContext());

        if (questionEditMode == QuestionEditMode.UPDATE) {
            question.getWords().forEach(word -> {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                layoutParams.addRule(BELOW, maxId.get());
                EditText editText = new EditText(getContext());
                editText.setLayoutParams(layoutParams);
                editText.setText(word.getWord());
                editText.setHint(word.getLanguage());
                editText.setId(maxId.incrementAndGet());
                editText.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override public void afterTextChanged(Editable editable) {
                        try {
                            if (CollectionUtils.isEmpty(allQuestions) || "".equals(editable.toString())) {
                                hintsTextView.setText("");
                                return;
                            }
                            // Find words that match current user input
                            List<Question> matchedQuestions = findMattchedQuestions(editable, word.getLanguage());

                            //Set hits text view with possible matches
                            hintsTextView.setText(getMatchedQuestionsTextView(matchedQuestions, word.getLanguage()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                relativeLayout.addView(editText);
            });
        } else if (questionEditMode == QuestionEditMode.CREATE) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            EditText editTextEn = new EditText(getContext());
            editTextEn.setHint(ENGLISH_KEY);
            editTextEn.setId(maxId.get());
            editTextEn.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            editTextEn.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override public void afterTextChanged(Editable editable) {
                    try {
                        if (CollectionUtils.isEmpty(allQuestions) || "".equals(editable.toString())) {
                            hintsTextView.setText("");
                            return;
                        }
                        // Find words that match current user input
                        List<Question> matchedQuestions = findMattchedQuestions(editable, ENGLISH_KEY);

                        //Set hits text view with possible matches
                        hintsTextView.setText(getMatchedQuestionsTextView(matchedQuestions, ENGLISH_KEY));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            relativeLayout.addView(editTextEn);

            EditText editTextRu = new EditText(getContext());
            editTextRu.setHint(RUSSIAN_KEY);
            layoutParams.addRule(BELOW, maxId.get());
            editTextRu.setId(maxId.incrementAndGet());
            editTextRu.setLayoutParams(layoutParams);
            editTextRu.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override public void afterTextChanged(Editable editable) {
                    try {
                        if (CollectionUtils.isEmpty(allQuestions) || "".equals(editable.toString())) {
                            hintsTextView.setText("");
                            return;
                        }
                        // Find words that match current user input
                        List<Question> matchedQuestions = findMattchedQuestions(editable, RUSSIAN_KEY);

                        //Set hits text view with possible matches
                        hintsTextView.setText(getMatchedQuestionsTextView(matchedQuestions, RUSSIAN_KEY));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            relativeLayout.addView(editTextRu);
        }

        RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        textViewParams.addRule(BELOW, maxId.get());
        hintsTextView.setId(maxId.incrementAndGet());
        hintsTextView.setLayoutParams(textViewParams);
        relativeLayout.addView(hintsTextView);

        Button saveButton = new Button(getContext());
        saveButton.setText("Save");
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(500, 200);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        saveButton.setId(maxId.incrementAndGet());
        saveButton.setLayoutParams(buttonParams);
        relativeLayout.addView(saveButton);


        saveButton.setOnClickListener(this::save);

        return view;
    }

    @NonNull
    private String getMatchedQuestionsTextView(List<Question> matchedQuestions, String language) {
        return matchedQuestions.stream().map(question1 -> RecyclerAdapter.questionToString(question1, language)).collect(Collectors.joining("\n"));
    }

    @NonNull
    private List<Question> findMattchedQuestions(Editable editable, String language) {
        return allQuestions.stream().filter(
                q -> q.getWords().stream().filter(w -> w.getLanguage().equals(language)).anyMatch(w -> w.getWord().contains(editable.toString()))
        ).collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void save(View view) {

        final int childCount = relativeLayout.getChildCount();
        if (questionEditMode == QuestionEditMode.CREATE) {
            question = new Question();
        }
        for (int i = 0, i1 = 0; i < childCount; i++, i1++) {
            EditText editText = (EditText) relativeLayout.getChildAt(i);
            String editedWord = editText.getText().toString();
            String lang = editText.getHint().toString();
            if (editedWord.equals("$del")) {
                if (i1 < question.getWords().size()) {
                    question.getWords().remove(i1);
                }
                i1--;
            } else {
                if (questionEditMode == QuestionEditMode.CREATE) {
                    question.getWords().add(new Word(null, editedWord, lang, null));
                } else {
                    question.getWords().get(i1).setWord(editedWord);
                }
            }
        }
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }
        Context context = getContext();
        saveOrUpdate(activity, question)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        question -> {
                            Toast.makeText(context, "Question was saved", Toast.LENGTH_SHORT).show();
                            activity.getRecyclerAdapter().notifyDataSetChanged();
                            activity.onBackPressed();
                        },
                        error -> Toast.makeText(context, "Failed saving a question, reason: " +
                                error.getLocalizedMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private Observable<Question> saveOrUpdate(MainActivity activity, Question question) {
        if (questionEditMode == QuestionEditMode.UPDATE) {
            return activity.getQuestionService().update(question, question.getId());
        } else if (questionEditMode == QuestionEditMode.CREATE) {
            return activity.getQuestionService().create(question);
        } else {
            throw new RuntimeException("Not supported question edit mode " + questionEditMode.toString());
        }
    }
}
