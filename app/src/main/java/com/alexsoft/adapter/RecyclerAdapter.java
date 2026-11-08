package com.alexsoft.adapter;

import static com.alexsoft.GuesswordApplication.getAppContext;
import static com.alexsoft.datamodel.Question.ENGLISH_KEY;
import static com.alexsoft.datamodel.Question.RUSSIAN_KEY;
import static com.alexsoft.utils.QuestionUtils.alreadyStudiedAnsweredWrong;
import static com.alexsoft.utils.QuestionUtils.hasBeenStudied;
import static com.alexsoft.utils.QuestionUtils.isAnswered;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsoft.datamodel.Question;
import com.alexsoft.datamodel.Word;
import com.kuzko.aleksey.alexsoft.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Aleks on 20.03.2017.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public static final int TILE_COUNT = 10;

    private final List<Question> questions = new LinkedList<>();
    private final List<Question> answeredQuestions = new LinkedList<>();
    private int recyclerViewHeight = 0;
    private int totalAnswered = 0;
    private int rightAnswerPercentage = 0;
    private int studiedQuestions = 0;

    public RecyclerAdapter() {
    }

    public Question getQuestion(int position) {
        if (position >= 0 && position < questions.size()) {
            return questions.get(position);
        }
        return null;
    }

    public void remove(int position) {
        if (position >= 0 && position < questions.size()) {
            questions.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeQuestion(Question target) {
        if (target == null) {
            return;
        }
        for (int i = 0; i < questions.size(); i++) {
            Question current = questions.get(i);
            if (current == target || (target.getId() != null && Objects.equals(current.getId(), target.getId()))) {
                questions.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRecyclerItem;
        TextView confirmedCheckmark;

        ViewHolder(View view) {
            super(view);
            textViewRecyclerItem = view.findViewById(R.id.textViewRecyclerItem);
            confirmedCheckmark = view.findViewById(R.id.confirmedCheckmark);
        }
    }

    public void addItem(Question question) {
        if (question != null) {
            questions.add(question);
            notifyItemInserted(questions.size() - 1);
        }
    }

    public boolean containsQuestionId(Integer id) {
        if (id == null) {
            return false;
        }
        return questions.stream().anyMatch(q -> Objects.equals(q.getId(), id));
    }

    public boolean hasAnsweredQuestions() {
        return questions.stream().anyMatch(q -> q.getAnsweredCorrectly() != null);
    }

    public int removeAnsweredQuestions() {
        int removedCount = 0;
        for (int i = questions.size() - 1; i >= 0; i--) {
            if (questions.get(i).getAnsweredCorrectly() != null) {
                questions.remove(i);
                notifyItemRemoved(i);
                removedCount++;
            }
        }
        return removedCount;
    }

    public int updateQuestion(Question target, Question updated) {
        for (int i = 0; i < questions.size(); i++) {
            Question current = questions.get(i);
            if (current == target || (updated.getId() != null && Objects.equals(current.getId(), updated.getId()))) {
                questions.set(i, updated);
                notifyItemChanged(i);
                return i;
            }
        }
        return -1;
    }

    public void recordAnswer(Question target, Question updated) {
        if (updated == null || updated.getAnsweredCorrectly() == null) {
            return;
        }
        boolean replaced = false;
        for (int i = answeredQuestions.size() - 1; i >= 0; i--) {
            Question existing = answeredQuestions.get(i);
            if (existing == target || (updated.getId() != null && Objects.equals(existing.getId(), updated.getId()))) {
                answeredQuestions.set(i, updated);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            answeredQuestions.add(updated);
        }
        updateStats();
    }

    private void updateStats() {
        int rights = 0;
        int total = 0;
        studiedQuestions = 0;
        for (Question question : answeredQuestions) {
            if (question.getAnsweredCorrectly() != null) {
                if (question.getAnsweredCorrectly()) {
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
        totalAnswered = total;
        rightAnswerPercentage = total == 0 ? 0 : rights * 100 / total;
    }

    public void setRecyclerViewHeight(int height) {
        if (height > 0 && this.recyclerViewHeight != height) {
            this.recyclerViewHeight = height;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (parent.getHeight() > 0 && recyclerViewHeight == 0) {
            recyclerViewHeight = parent.getHeight();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setLongClickable(true);

        int availableHeight = recyclerViewHeight > 0 ? recyclerViewHeight :
                (holder.itemView.getParent() instanceof View ? ((View) holder.itemView.getParent()).getHeight() : 0);
        if (availableHeight > 0) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            int verticalMargin = 0;
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                verticalMargin = mlp.topMargin + mlp.bottomMargin;
            }
            int targetHeight = Math.max(1, (availableHeight / TILE_COUNT) - verticalMargin);
            if (lp.height != targetHeight) {
                lp.height = targetHeight;
                holder.itemView.setLayoutParams(lp);
            }
        }

        Question question = questions.get(position);
        applyTileBackground(holder.itemView, question.getAnsweredCorrectly());

        String foreignWords = questionToString(question, ENGLISH_KEY);
        double studyPoints = question.getStudyPoints() != null ? question.getStudyPoints() : 0.0;
        double studyMultiplier = question.getStudyPointsMultiplier() != null ? question.getStudyPointsMultiplier() : 0.0;
        double prevStudyPoints = question.getPreviousStudyPoints() != null ? question.getPreviousStudyPoints() : studyPoints;
        double prevStudyMultiplier = question.getPreviousStudyPointsMultiplier() != null ? question.getPreviousStudyPointsMultiplier() : studyMultiplier;

        if (question.getAnsweredCorrectly() != null) {
            boolean makeBold;
            if (question.getAnsweredCorrectly()) {
                makeBold = prevStudyPoints > 3 && studyPoints <= 3;
                foreignWords = String.format("<font color='%s'>%s</font>",
                        getAppContext().getString(R.string.rightAnsweredQuestionColor),
                        foreignWords
                );
            } else {
                makeBold = prevStudyPoints <= 3 && studyPoints > 3;
                foreignWords = String.format("<font color='%s'>%s</font>",
                        getAppContext().getString(R.string.wrongAnsweredQuestionColor),
                        foreignWords
                );
            }
            if (makeBold) {
                foreignWords = "<b>" + foreignWords + "</b>";
            }
        } else {
            foreignWords = String.format("<font color='%s'>%s</font>",
                    getAppContext().getString(R.string.unAnsweredQuestionColor),
                    foreignWords
            );
        }

        String nativeWords = questionToString(question, RUSSIAN_KEY);

        if (studyPoints <= 3 && !isAnswered(question)) {
            nativeWords = String.format("<font color='%s'>%s</font>",
                    getAppContext().getString(R.string.rightAnsweredQuestionColor),
                    nativeWords
            );
        }

        String words = question.getAnsweredCorrectly() == null ? nativeWords : nativeWords + " - " + foreignWords;
        holder.textViewRecyclerItem.setText(
                Html.fromHtml(words, Html.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.SPANNABLE
        );
        holder.textViewRecyclerItem.setTextColor(Color.parseColor("#37474F"));
        holder.textViewRecyclerItem.setTypeface(null, question.getAnsweredCorrectly() != null ? Typeface.BOLD : Typeface.NORMAL);
        if (holder.confirmedCheckmark != null) {
            holder.confirmedCheckmark.setVisibility(
                    Boolean.TRUE.equals(question.getConfirmed()) ? View.VISIBLE : View.GONE);
        }
    }

    private void applyTileBackground(View itemView, Boolean answeredCorrectly) {
        GradientDrawable drawable = new GradientDrawable();
        float cornerRadiusPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8f, itemView.getResources().getDisplayMetrics());
        int strokeWidthPx = Math.max(1, Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1.5f, itemView.getResources().getDisplayMetrics())));
        drawable.setCornerRadius(cornerRadiusPx);

        if (answeredCorrectly == null) {
            drawable.setColor(Color.parseColor("#FAFAFA"));
            drawable.setStroke(strokeWidthPx, Color.parseColor("#CFD8DC"));
        } else if (answeredCorrectly) {
            drawable.setColor(Color.parseColor("#E8F5E9"));
            drawable.setStroke(strokeWidthPx, Color.parseColor("#66BB6A"));
        } else {
            drawable.setColor(Color.parseColor("#FFEBEE"));
            drawable.setStroke(strokeWidthPx, Color.parseColor("#EF5350"));
        }
        itemView.setBackground(drawable);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static String questionToString(Question question, String language) {
        return question.getWords().stream()
                .filter(word -> word.getLanguage().equals(ENGLISH_KEY.equals(language) ? ENGLISH_KEY : RUSSIAN_KEY))
                .map(Word::toQuestionString)
                .collect(Collectors.joining(", "));
    }

    public static String answeredQuestionToString(Question question, String language) {
        return String.format("%s - %s",
                question.getWords().stream()
                        .filter(word -> word.getLanguage().equals(ENGLISH_KEY.equals(language) ? ENGLISH_KEY : RUSSIAN_KEY))
                        .map(Word::toQuestionString)
                        .collect(Collectors.joining(", ")),
                question.getWords().stream()
                        .filter(word -> word.getLanguage().equals(RUSSIAN_KEY.equals(language) ? ENGLISH_KEY : RUSSIAN_KEY))
                        .map(Word::toQuestionString)
                        .collect(Collectors.joining(", "))
        );
    }

    public int getTotalAnswered() {
        return totalAnswered;
    }

    public int getRightAnswerPercentage() {
        return rightAnswerPercentage;
    }

    public int getStudiedQuestions() {
        return studiedQuestions;
    }
}
