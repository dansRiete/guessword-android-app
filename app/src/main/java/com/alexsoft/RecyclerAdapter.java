package com.alexsoft;

/**
 * Created by Aleks on 20.03.2017.
 */
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexsoft.datamodel.Question;
import com.alexsoft.datamodel.Word;
import com.kuzko.aleksey.alexsoft.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Question> questions = new LinkedList<>();

    RecyclerAdapter(List<Question> dataset) {
        questions = dataset == null ? new LinkedList<>() : dataset;
    }

    RecyclerAdapter() {
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRecyclerItem;
        ViewHolder(View view) {
            super(view);
            textViewRecyclerItem = view.findViewById(R.id.textViewRecyclerItem);
        }
    }

    void addItem(Question question){
        if(question != null) {
            questions.add(0, question);
        }
    }

    Question getCurrent() {
        return questions.size() > 0 ? questions.get(0) : null;
    }

    Question setCurrent(Question question) {
        return questions.set(0, question);
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Question question = questions.get(position);
        List<Word> foreignWords = question.getWords().stream().filter(word -> word.getLanguage().equals("en")).collect(Collectors.toList());
        List<Word> nativeWords = question.getWords().stream().filter(word -> word.getLanguage().equals("ru")).collect(Collectors.toList());
        String words = question.getAnsweredCorrectly() == null ? foreignWords.toString() : foreignWords.toString() + " - " + nativeWords.toString();
        String formattedWords = String.format("%d. %s", questions.size() - position, words);
        String formattedProbabilityValues;
        if(question.getAnsweredCorrectly() == null) {
            formattedProbabilityValues = String.format("%.1f/%.2f", question.getProbabilityFactor(), question.getProbabilityMultiplier());
        } else {
            formattedProbabilityValues = String.format("%.1f->%.1f/%.2f->%.2f",
                    question.getPreviousProbabilityFactor(),
                    question.getProbabilityFactor(),
                    question.getPreviousProbabilityMultiplier(),
                    question.getProbabilityMultiplier()
            );
        }
        holder.textViewRecyclerItem.setText(formattedWords + " " + formattedProbabilityValues);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
