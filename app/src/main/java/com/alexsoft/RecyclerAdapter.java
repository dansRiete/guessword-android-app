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
import java.util.List;
import java.util.stream.Collectors;


class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Question> questions = new ArrayList<>();

    RecyclerAdapter(List<Question> dataset) {
        questions = dataset == null ? new ArrayList<>() : dataset;
    }

    public RecyclerAdapter() {
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRecyclerItem;
        ViewHolder(View view) {
            super(view);
            textViewRecyclerItem = (TextView) view.findViewById(R.id.textViewRecyclerItem);
        }
    }

    public void addItem(Question question){
        if(question != null) {
            questions.add(question);
        }
    }

    public Question getCurrent() {
        return questions.get(questions.size() - 1);
    }

    public Question setCurrent(Question question) {
        return questions.set(questions.size() - 1, question);
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List<Word> foreignWords = questions.get(position).getWords().stream().filter(word -> word.getLanguage().equals("en")).collect(Collectors.toList());
        holder.textViewRecyclerItem.setText(
                position + ". " + foreignWords + " " + ((int) questions.get(position).getProbabilityFactor().doubleValue())
        );
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
