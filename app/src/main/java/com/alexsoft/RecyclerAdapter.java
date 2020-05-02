package com.alexsoft;

/**
 * Created by Aleks on 20.03.2017.
 */

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexsoft.datamodel.Question;
import com.alexsoft.datamodel.Word;
import com.kuzko.aleksey.alexsoft.R;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Question> questions = new LinkedList<>();
    private boolean previousSelected;

    RecyclerAdapter() {
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRecyclerItem;

        ViewHolder(View view) {
            super(view);
            textViewRecyclerItem = view.findViewById(R.id.textViewRecyclerItem);
        }
    }

    void addItem(Question question) {
        if (question != null) {
            questions.add(0, question);
        }
    }

    Question getCurrent() {
        if (previousSelected) {
            return questions.size() > 1 ? questions.get(1) : null;
        } else {
            return questions.size() > 0 ? questions.get(0) : null;
        }
    }

    Question setCurrent(Question question) {
        if (previousSelected) {
            return questions.set(1, question);
        } else {
            return questions.set(0, question);
        }
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Question question = questions.get(position);
        String foreignWords = toString(question.getWords().stream().filter(word -> word.getLanguage().equals("en"))
                .collect(Collectors.toList()));
        String nativeWords = toString(question.getWords().stream().filter(word -> word.getLanguage().equals("ru"))
                .collect(Collectors.toList()));
        String words = question.getAnsweredCorrectly() == null ? foreignWords : foreignWords + " - " + nativeWords;
        String formattedWords = String.format("%d. %s", questions.size() - position, words);
        String formattedProbabilityValues;
        if (question.getAnsweredCorrectly() == null) {
            formattedProbabilityValues = String.format("[ %.1f/%.2f ]", question.getProbabilityFactor(),
                    question.getProbabilityMultiplier());
        } else {
            formattedProbabilityValues = String.format("[ %.1f>%.1f / %.2f>%.2f ]",
                    question.getPreviousProbabilityFactor(),
                    question.getProbabilityFactor(),
                    question.getPreviousProbabilityMultiplier(),
                    question.getProbabilityMultiplier()
            );
        }
        holder.textViewRecyclerItem.setText(formattedWords + " " + formattedProbabilityValues);
        if (question.getAnsweredCorrectly() != null) {
            if (question.getAnsweredCorrectly()) {
                holder.textViewRecyclerItem.setTextColor(Color.parseColor("#FF81C784"));
            } else {
                holder.textViewRecyclerItem.setTextColor(Color.parseColor("#FFE57373"));
            }
        } else {
            holder.textViewRecyclerItem.setTextColor(Color.GRAY);
        }
        if ((previousSelected && position == 1) || (!previousSelected && position == 0)) {
            holder.textViewRecyclerItem.setTypeface(null, Typeface.BOLD);
        } else {
            holder.textViewRecyclerItem.setTypeface(null, Typeface.NORMAL);
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    private String toString(List<Word> words) {
        return words.stream().map(Word::getWord).collect(Collectors.joining(", "));
    }

    public void setPreviousSelected(boolean previousSelected) {
        if (questions.size() > 1) {
            this.previousSelected = previousSelected;
        }
    }
}
