package com.alexsoft;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.alexsoft.datamodel.Question;
import com.alexsoft.datamodel.Word;
import com.kuzko.aleksey.alexsoft.R;

import java.util.Optional;

public class QuestionEditFragment extends Fragment {

    public static final String QUESTION_KEY = "Question";

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
        View view = inflater.inflate(R.layout.fragment1, null);
        Question question = null;
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(QUESTION_KEY);
        }
        EditText forWord = view.findViewById(R.id.forWord);
        EditText natWord = view.findViewById(R.id.natWord);
        EditText label = view.findViewById(R.id.label);
        Optional<Word> en = question.getWords().stream().filter(word -> word.getLanguage().equals("en")).findFirst();
        en.ifPresent(word -> {
            forWord.setText(word.getWord());
        });
        Optional<Word> ru = question.getWords().stream().filter(word -> word.getLanguage().equals("ru")).findFirst();
        ru.ifPresent(word -> {
            natWord.setText(word.getWord());
        });
        if(question.getTag() != null && !question.getTag().equals("")) {
            label.setText(question.getTag());
        }

        return view;
    }
}
