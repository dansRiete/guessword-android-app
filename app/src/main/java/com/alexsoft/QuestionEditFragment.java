package com.alexsoft;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.alexsoft.datamodel.Question;
import com.alexsoft.datamodel.Word;
import com.kuzko.aleksey.alexsoft.R;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.alexsoft.datamodel.Question.ENGLISH_KEY;
import static com.alexsoft.datamodel.Question.RUSSIAN_KEY;

public class QuestionEditFragment extends Fragment {

    public static final String QUESTION_KEY = "Question";
    public static final String QUESTION_EDIT_MODE_KEY = "QuestionEditMode";
    private Question question;
    private QuestionEditMode questionEditMode;
    LinearLayout linearLayout;

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
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(QUESTION_KEY);
            questionEditMode = (QuestionEditMode) getArguments().getSerializable(QUESTION_EDIT_MODE_KEY);
        }
        Button saveButton = view.findViewById(R.id.save);
        linearLayout = view.findViewById(R.id.editQuestionLayout);

        if (questionEditMode == QuestionEditMode.UPDATE) {
            question.getWords().forEach(word -> {
                EditText editText = new EditText(getContext());
                editText.setText(word.getWord());
                editText.setHint(word.getLanguage());
                linearLayout.addView(editText);
            });
        } else if (questionEditMode == QuestionEditMode.CREATE) {
            EditText editTextEn = new EditText(getContext());
            editTextEn.setHint(ENGLISH_KEY);
            linearLayout.addView(editTextEn);

            EditText editTextRu = new EditText(getContext());
            editTextRu.setHint(RUSSIAN_KEY);
            linearLayout.addView(editTextRu);
        }

        saveButton.setOnClickListener(this::save);
        /*forWord = view.findViewById(R.id.forWord);
        natWord = view.findViewById(R.id.natWord);
        label = view.findViewById(R.id.label);
        Optional<Word> en = question.getWords().stream().filter(word -> word.getLanguage().equals(ENGLISH_KEY)).findFirst();
        en.ifPresent(word -> forWord.setText(word.getWord()));
        Optional<Word> ru = question.getWords().stream().filter(word -> word.getLanguage().equals(RUSSIAN_KEY)).findFirst();
        ru.ifPresent(word -> natWord.setText(word.getWord()));
        if(question.getTag() != null && !question.getTag().equals("")) {
            label.setText(question.getTag());
        }*/

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void save(View view) {

        final int childCount = linearLayout.getChildCount();
        if (questionEditMode == QuestionEditMode.CREATE) {
            question = new Question();
        }
        for (int i = 0, i1 = 0; i < childCount; i++, i1++) {
            EditText editText = (EditText) linearLayout.getChildAt(i);
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
