package com.alexsoft;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.alexsoft.datamodel.Question;
import com.kuzko.aleksey.alexsoft.R;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class QuestionEditFragment extends Fragment {

    public static final String QUESTION_KEY = "Question";
    private Question question;
    EditText forWord;
    EditText natWord;
    EditText label;
    LinearLayout linearLayout;

    AtomicInteger i1 = new AtomicInteger();
    Map<Integer, String> map = new HashMap<>();

    public static QuestionEditFragment newInstance(Question question) {
        QuestionEditFragment fragment = new QuestionEditFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUESTION_KEY, question);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.question_edit_fragment, null);
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(QUESTION_KEY);
        }
        Button saveButton = view.findViewById(R.id.save);
        linearLayout = view.findViewById(R.id.editQuestionLayout);

        question.getWords().forEach(word -> {
            map.put(i1.getAndIncrement(), word.getWord());
            EditText editText = new EditText(getContext());
            editText.setText(word.getWord());
            linearLayout.addView(editText);
        });

        saveButton.setOnClickListener(this::save);
        /*forWord = view.findViewById(R.id.forWord);
        natWord = view.findViewById(R.id.natWord);
        label = view.findViewById(R.id.label);
        Optional<Word> en = question.getWords().stream().filter(word -> word.getLanguage().equals("en")).findFirst();
        en.ifPresent(word -> forWord.setText(word.getWord()));
        Optional<Word> ru = question.getWords().stream().filter(word -> word.getLanguage().equals("ru")).findFirst();
        ru.ifPresent(word -> natWord.setText(word.getWord()));
        if(question.getTag() != null && !question.getTag().equals("")) {
            label.setText(question.getTag());
        }*/

        return view;
    }

    private void save(View view) {

        final int childCount = linearLayout.getChildCount();
        for (int i = 0, i1 = 0; i < childCount; i++, i1++) {
            EditText editText = (EditText) linearLayout.getChildAt(i);
            String editedWord = editText.getText().toString();
            if(editedWord.equals("$del")) {
                question.getWords().remove(i1);
                i1--;
            } else {
                question.getWords().get(i1).setWord(editedWord);
            }
        }
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }
        Context context = getContext();
        activity.getQuestionService().update(question, question.getId())
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
}
