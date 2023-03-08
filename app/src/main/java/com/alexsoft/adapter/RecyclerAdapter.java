package com.alexsoft.adapter;


import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
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

import static com.alexsoft.GuesswordApplication.getAppContext;
import static com.alexsoft.datamodel.Question.ENGLISH_KEY;
import static com.alexsoft.datamodel.Question.RUSSIAN_KEY;
import static com.alexsoft.utils.QuestionUtils.alreadyStudiedAnsweredWrong;
import static com.alexsoft.utils.QuestionUtils.hasBeenStudied;
import static com.alexsoft.utils.QuestionUtils.isAnswered;


/**
 * Created by Aleks on 20.03.2017.
 */
@Getter
@Setter
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Question> questions = new LinkedList<>();
    private boolean previousSelected;
    private int rightAnswerPercentage = 0;
    private int studiedQuestions = 0;

    public RecyclerAdapter() {
    }

    public Question getQuestion(int id) {
        return questions.get(id);
    }

    public void remove(int id) {
        if (id >= 0 && id < questions.size()) {
            questions.remove(id);
        }
        updateStats();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRecyclerItem;

        ViewHolder(View view) {
            super(view);
            textViewRecyclerItem = view.findViewById(R.id.textViewRecyclerItem);
        }
    }

    public void addItem(Question question) {
        if (question != null) {
            questions.add(0, question);
        }
        updateStats();
    }

    public Question getCurrent() {
            return questions.size() > 0 ? questions.get(0) : null;
    }

    public Question getPrevious() {
        return questions.size() > 1 ? questions.get(1) : null;
    }

    public Question setPrevious(Question question) {
        Question set = questions.set(1, question);
        updateStats();
        return set;
    }

    public Question setCurrent(Question question) {
        Question question1;
        if (previousSelected) {
            question1 = questions.set(1, question);
        } else {
            question1 =  questions.set(0, question);
        }
        updateStats();
        return question1;
    }

    private void updateStats() {
        int rights = 0;
        int total = 0;
        studiedQuestions = 0;
        for(Question question : questions) {
            if(question.getAnsweredCorrectly() != null) {
                if(question.getAnsweredCorrectly()) {
                    rights++;
                }
                total++;
            }
            if (hasBeenStudied(question)) {
                studiedQuestions++;
            } else if (alreadyStudiedAnsweredWrong(question)) {
                studiedQuestions--;
            }
        }
        rightAnswerPercentage = total == 0 ? 0 : rights * 100 / total;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setLongClickable(true);
        Question question = questions.get(position);
        String foreignWords = questionToString(question, ENGLISH_KEY);

        if (question.getAnsweredCorrectly() != null) {
            boolean makeBold;
            if (question.getAnsweredCorrectly()) {
                makeBold = question.getPreviousStudyPoints() > 3 && question.getStudyPoints() <= 3;
                foreignWords = String.format("<font color='%s'>%s</font>",
                        getAppContext().getString(R.string.rightAnsweredQuestionColor),
                        foreignWords
                );
            } else {
                makeBold = question.getPreviousStudyPoints() <= 3 && question.getStudyPoints() > 3;
                foreignWords = String.format("<font color='%s'>%s</font>",
                        getAppContext().getString(R.string.wrongAnsweredQuestionColor),
                        foreignWords
                );
            }
            if(makeBold) {
                foreignWords = "<b>" + foreignWords + "</b>";
            }
        } else {
            foreignWords = String.format("<font color='%s'>%s</font>",
                    getAppContext().getString(R.string.unAnsweredQuestionColor),
                    foreignWords
            );
        }

        String nativeWords = questionToString(question, RUSSIAN_KEY);

        if (question.getStudyPoints() <= 3 && !isAnswered(question)) {
            nativeWords = String.format("<font color='%s'>%s</font>",
                    getAppContext().getString(R.string.rightAnsweredQuestionColor),
                    nativeWords
            );
        }

        String words = question.getAnsweredCorrectly() == null ? nativeWords : nativeWords + " - " + foreignWords;
        String formattedWords = String.format("%d. %s", questions.size() - position, words);
        String formattedProbabilityValues;
        if (question.getAnsweredCorrectly() == null) {
            formattedProbabilityValues = String.format("[ %.1f/%.2f ]", question.getStudyPoints(),
                    question.getStudyPointsMultiplier());
        } else {
            formattedProbabilityValues = String.format("[ %.1f>%.1f / %.2f>%.2f ]",
                    question.getPreviousStudyPoints(),
                    question.getStudyPoints(),
                    question.getPreviousStudyPointsMultiplier(),
                    question.getStudyPointsMultiplier()
            );
        }
        holder.textViewRecyclerItem.setText(Html.fromHtml(formattedWords + " " + formattedProbabilityValues), TextView.BufferType.SPANNABLE);
        holder.textViewRecyclerItem.setTextColor(Color.GRAY);
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

    public static String questionToString(Question question, String language) {
        return String.format("%s - %s",
                question.getWords().stream().filter(word -> word.getLanguage().equals(ENGLISH_KEY.equals(language) ? ENGLISH_KEY : RUSSIAN_KEY)).map(Word::toQuestionString).collect(Collectors.joining(", ")),
                question.getWords().stream().filter(word -> word.getLanguage().equals(RUSSIAN_KEY.equals(language) ? ENGLISH_KEY : RUSSIAN_KEY)).map(Word::toQuestionString).collect(Collectors.joining(", "))
        );
    }

    public void setPreviousSelected(boolean previousSelected) {
        if (questions.size() > 1) {
            this.previousSelected = previousSelected;
        }
    }

    public int getRightAnswerPercentage() {
        return rightAnswerPercentage;
    }

    public int getStudiedQuestions() {
        return studiedQuestions;
    }
}
