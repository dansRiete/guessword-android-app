package com.alexsoft.fragment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.RelativeLayout.ABOVE;
import static android.widget.RelativeLayout.BELOW;
import static com.alexsoft.datamodel.Question.ENGLISH_KEY;
import static com.alexsoft.datamodel.Question.RUSSIAN_KEY;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.alexsoft.activities.MainActivity;
import com.alexsoft.adapter.RecyclerAdapter;
import com.alexsoft.datamodel.Question;
import com.alexsoft.datamodel.Word;
import com.alexsoft.enums.QuestionEditMode;
import com.kuzko.aleksey.alexsoft.R;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class QuestionEditFragment extends Fragment {

    public static final String QUESTION_KEY = "Question";
    public static final String QUESTION_EDIT_MODE_KEY = "QuestionEditMode";
    private static final Pattern SUGGESTED_RU_PATTERN =
            Pattern.compile("(?im)^\\s*(?:[-•]\\s*)?(?:\\*\\*)?Suggested\\s+RU\\s*:\\s*(?:\\*\\*)?\\s*(.+)$");
    private static final Pattern SUGGESTED_EN_PATTERN =
            Pattern.compile("(?im)^\\s*(?:[-•]\\s*)?(?:\\*\\*)?Suggested\\s+EN\\s*:\\s*(?:\\*\\*)?\\s*(.+)$");

    private Question question;
    private List<Question> allQuestions;
    private QuestionEditMode questionEditMode;
    private RelativeLayout relativeLayout;
    private EditText commentEditText;
    private CheckBox confirmedCheckBox;
    private final List<WordInputField> wordInputFields = new ArrayList<>();

    private static class WordInputField {
        final String language;
        final EditText editText;
        final Word word;

        WordInputField(String language, EditText editText, Word word) {
            this.language = language;
            this.editText = editText;
            this.word = word;
        }
    }

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
        View view = inflater.inflate(R.layout.question_edit_fragment, container, false);
        AtomicInteger maxId = new AtomicInteger(1);
        wordInputFields.clear();

        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(QUESTION_KEY);
            questionEditMode = (QuestionEditMode) getArguments().getSerializable(QUESTION_EDIT_MODE_KEY);
        }
        relativeLayout = view.findViewById(R.id.editQuestionLayout);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.getQuestionService().getAllQuestions()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            questions -> this.allQuestions = questions,
                            Throwable::printStackTrace
                    );
        }

        TextView hintsTextView = new TextView(getContext());

        if (questionEditMode == QuestionEditMode.UPDATE && question != null) {
            // Place Russian (ru) input fields on top of English (en) input fields
            List<Word> sortedWords = new ArrayList<>(question.getWords());
            sortedWords.sort((w1, w2) -> {
                boolean r1 = RUSSIAN_KEY.equals(w1.getLanguage());
                boolean r2 = RUSSIAN_KEY.equals(w2.getLanguage());
                return Boolean.compare(r2, r1);
            });
            for (Word word : sortedWords) {
                EditText editText = addLabeledInputRow(
                        maxId, word.getLanguage(), word.getWord(), hintsTextView);
                wordInputFields.add(new WordInputField(word.getLanguage(), editText, word));
            }
        } else if (questionEditMode == QuestionEditMode.CREATE) {
            // Place Russian (ru) on top of English (en)
            EditText editTextRu = addLabeledInputRow(maxId, RUSSIAN_KEY, "", hintsTextView);
            EditText editTextEn = addLabeledInputRow(maxId, ENGLISH_KEY, "", hintsTextView);
            wordInputFields.add(new WordInputField(RUSSIAN_KEY, editTextRu, null));
            wordInputFields.add(new WordInputField(ENGLISH_KEY, editTextEn, null));
        }

        // Comment input field for LLM context
        commentEditText = addCommentInputRow(maxId);

        // LLM action buttons row
        LinearLayout llmButtonsRow = new LinearLayout(getContext());
        llmButtonsRow.setOrientation(LinearLayout.HORIZONTAL);
        llmButtonsRow.setGravity(Gravity.CENTER_VERTICAL);
        RelativeLayout.LayoutParams llmRowParams = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        llmRowParams.addRule(BELOW, maxId.get());
        llmRowParams.topMargin = dpToPx(6);
        llmButtonsRow.setId(maxId.incrementAndGet());
        llmButtonsRow.setLayoutParams(llmRowParams);

        confirmedCheckBox = new CheckBox(getContext());
        confirmedCheckBox.setText("Confirmed");
        confirmedCheckBox.setChecked(question != null && Boolean.TRUE.equals(question.getConfirmed()));
        LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        checkBoxParams.rightMargin = dpToPx(8);
        confirmedCheckBox.setLayoutParams(checkBoxParams);
        llmButtonsRow.addView(confirmedCheckBox);

        // Bottom buttons bar: Delete (in UPDATE mode) and Save
        LinearLayout bottomBar = new LinearLayout(getContext());
        bottomBar.setOrientation(LinearLayout.HORIZONTAL);
        bottomBar.setGravity(Gravity.END);
        RelativeLayout.LayoutParams bottomBarParams = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        bottomBarParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottomBar.setId(maxId.incrementAndGet());
        bottomBar.setLayoutParams(bottomBarParams);

        if (questionEditMode == QuestionEditMode.UPDATE && question != null) {
            Button deleteButton = new Button(getContext());
            deleteButton.setText("Delete");
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f);
            deleteParams.rightMargin = dpToPx(8);
            deleteButton.setLayoutParams(deleteParams);
            deleteButton.setOnClickListener(this::deleteQuestion);
            bottomBar.addView(deleteButton);
        }

        Button saveButton = new Button(getContext());
        saveButton.setText("Save");
        LinearLayout.LayoutParams saveParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f);
        saveButton.setLayoutParams(saveParams);
        saveButton.setOnClickListener(this::save);

        bottomBar.addView(saveButton);
        relativeLayout.addView(bottomBar);

        // Scrollable LLM check summary box filling all remaining space down to bottomBar
        ScrollView llmResultScroll = new ScrollView(getContext());
        llmResultScroll.setFillViewport(true);
        RelativeLayout.LayoutParams llmScrollParams = new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        llmScrollParams.addRule(BELOW, llmButtonsRow.getId());
        llmScrollParams.addRule(ABOVE, bottomBar.getId());
        llmScrollParams.topMargin = dpToPx(6);
        llmScrollParams.bottomMargin = dpToPx(6);
        llmResultScroll.setId(maxId.incrementAndGet());
        llmResultScroll.setLayoutParams(llmScrollParams);
        llmResultScroll.setVisibility(View.GONE);

        GradientDrawable boxBg = new GradientDrawable();
        boxBg.setColor(Color.parseColor("#F1F8E9"));
        boxBg.setCornerRadius(dpToPx(8));
        boxBg.setStroke(dpToPx(1), Color.parseColor("#AED581"));
        llmResultScroll.setBackground(boxBg);

        LinearLayout llmBoxContent = new LinearLayout(getContext());
        llmBoxContent.setOrientation(LinearLayout.VERTICAL);
        int pad = dpToPx(10);
        llmBoxContent.setPadding(pad, pad, pad, pad);
        llmBoxContent.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        TextView llmResultTextView = new TextView(getContext());
        llmResultTextView.setTextColor(Color.parseColor("#263238"));
        llmResultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        llmResultTextView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        Button acceptSuggestionButton = new Button(getContext());
        acceptSuggestionButton.setText("Accept Suggestion");
        LinearLayout.LayoutParams acceptParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        acceptParams.gravity = Gravity.END;
        acceptParams.topMargin = dpToPx(8);
        acceptSuggestionButton.setLayoutParams(acceptParams);
        acceptSuggestionButton.setVisibility(View.GONE);

        llmBoxContent.addView(llmResultTextView);
        llmBoxContent.addView(acceptSuggestionButton);
        llmResultScroll.addView(llmBoxContent);

        // Matched questions hints area between LLM buttons and bottom bar
        ScrollView hintsScroll = new ScrollView(getContext());
        RelativeLayout.LayoutParams hintsParams = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        hintsParams.addRule(BELOW, llmButtonsRow.getId());
        hintsParams.addRule(ABOVE, bottomBar.getId());
        hintsParams.topMargin = dpToPx(6);
        hintsScroll.setId(maxId.incrementAndGet());
        hintsScroll.setLayoutParams(hintsParams);
        hintsScroll.addView(hintsTextView);

        Button checkByLlmButton = new Button(getContext());
        checkByLlmButton.setText("Check by LLM");
        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f);
        checkByLlmButton.setLayoutParams(checkParams);
        checkByLlmButton.setOnClickListener(v -> {
            hintsScroll.setVisibility(View.GONE);
            checkByLlm(llmResultScroll, llmResultTextView, acceptSuggestionButton);
        });
        llmButtonsRow.addView(checkByLlmButton);

        relativeLayout.addView(llmButtonsRow);
        relativeLayout.addView(hintsScroll);
        relativeLayout.addView(llmResultScroll);

        return view;
    }

    private EditText addLabeledInputRow(AtomicInteger maxId, String language, String initialText, TextView hintsTextView) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        RelativeLayout.LayoutParams rowParams = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        if (maxId.get() > 1) {
            rowParams.addRule(BELOW, maxId.get());
        }
        rowParams.bottomMargin = dpToPx(4);
        row.setId(maxId.incrementAndGet());
        row.setLayoutParams(rowParams);

        TextView label = new TextView(getContext());
        label.setText(language + ":");
        label.setTypeface(null, Typeface.BOLD);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        label.setTextColor(Color.parseColor("#37474F"));
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(dpToPx(38), WRAP_CONTENT);
        label.setLayoutParams(labelParams);

        EditText editText = new EditText(getContext());
        editText.setText(initialText);
        editText.setHint(language);
        editText.setTag(language);
        editText.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setSingleLine(false);
        editText.setHorizontallyScrolling(false);
        editText.setMinLines(1);
        editText.setMaxLines(6);
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f);
        editText.setLayoutParams(editParams);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String text = editable.toString().trim();
                    if (!CollectionUtils.isEmpty(allQuestions) && !text.isEmpty()) {
                        List<Question> matchedQuestions = findMattchedQuestions(editable, language);
                        hintsTextView.setText(getMatchedQuestionsTextView(matchedQuestions, language));
                    } else {
                        hintsTextView.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        row.addView(label);
        row.addView(editText);
        relativeLayout.addView(row);
        return editText;
    }

    private EditText addCommentInputRow(AtomicInteger maxId) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        RelativeLayout.LayoutParams rowParams = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        if (maxId.get() > 1) {
            rowParams.addRule(BELOW, maxId.get());
        }
        rowParams.bottomMargin = dpToPx(4);
        row.setId(maxId.incrementAndGet());
        row.setLayoutParams(rowParams);

        TextView label = new TextView(getContext());
        label.setText("note:");
        label.setTypeface(null, Typeface.BOLD);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        label.setTextColor(Color.parseColor("#546E7A"));
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(dpToPx(44), WRAP_CONTENT);
        label.setLayoutParams(labelParams);

        EditText editText = new EditText(getContext());
        editText.setHint("Comment / context for LLM");
        editText.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setSingleLine(false);
        editText.setHorizontallyScrolling(false);
        editText.setMinLines(1);
        editText.setMaxLines(4);
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f);
        editText.setLayoutParams(editParams);

        row.addView(label);
        row.addView(editText);
        relativeLayout.addView(row);
        return editText;
    }

    private void suggestByLlm() {
        EditText editTextEn = findFirstEditTextByLang(ENGLISH_KEY);
        EditText editTextRu = findFirstEditTextByLang(RUSSIAN_KEY);
        if (editTextEn == null || editTextRu == null) {
            return;
        }
        String enText = editTextEn.getText().toString().trim();
        String ruText = editTextRu.getText().toString().trim();
        String sourceText;
        String sourceLang;
        EditText targetField;
        if (!ruText.isEmpty()) {
            sourceText = ruText;
            sourceLang = RUSSIAN_KEY;
            targetField = editTextEn;
        } else if (!enText.isEmpty()) {
            sourceText = enText;
            sourceLang = ENGLISH_KEY;
            targetField = editTextRu;
        } else {
            Toast.makeText(getContext(), "Enter a word first", Toast.LENGTH_SHORT).show();
            return;
        }
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }
        activity.getQuestionService()
                .getSuggestions(sourceText, sourceLang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dto -> {
                            if (isAdded() && dto.getSuggestions() != null && !dto.getSuggestions().isEmpty()) {
                                targetField.setText(String.join(", ", dto.getSuggestions()));
                            }
                        },
                        error -> Toast.makeText(getContext(), "LLM suggestion failed", Toast.LENGTH_SHORT).show()
                );
    }

    private void checkByLlm(ScrollView llmResultScroll, TextView llmResultTextView, Button acceptSuggestionButton) {
        String enText = collectWordsByLanguage(ENGLISH_KEY);
        String ruText = collectWordsByLanguage(RUSSIAN_KEY);
        String commentText = commentEditText != null ? commentEditText.getText().toString().trim() : "";
        if (enText.isEmpty() && ruText.isEmpty() && commentText.isEmpty()) {
            Toast.makeText(getContext(), "Enter a phrase in ru, en, or comment first", Toast.LENGTH_SHORT).show();
            return;
        }
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }
        llmResultScroll.setVisibility(View.VISIBLE);
        acceptSuggestionButton.setVisibility(View.GONE);
        llmResultTextView.setText("Checking translation with LLM...");

        activity.getQuestionService()
                .checkTranslation(enText, ruText, commentText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        summary -> {
                            if (!isAdded()) {
                                return;
                            }
                            llmResultTextView.setText(formatMarkdown(summary), TextView.BufferType.SPANNABLE);
                            String suggestedRu = extractSuggestion(SUGGESTED_RU_PATTERN, summary);
                            String suggestedEn = extractSuggestion(SUGGESTED_EN_PATTERN, summary);
                            if (suggestedRu != null || suggestedEn != null) {
                                acceptSuggestionButton.setVisibility(View.VISIBLE);
                                acceptSuggestionButton.setOnClickListener(v -> {
                                    applySuggestedPair(suggestedRu, suggestedEn);
                                    if (confirmedCheckBox != null) {
                                        confirmedCheckBox.setChecked(true);
                                    }
                                });
                            } else {
                                acceptSuggestionButton.setVisibility(View.GONE);
                                if (confirmedCheckBox != null) {
                                    confirmedCheckBox.setChecked(true);
                                }
                            }
                        },
                        error -> {
                            if (isAdded()) {
                                acceptSuggestionButton.setVisibility(View.GONE);
                                llmResultTextView.setText("LLM check failed: " + error.getLocalizedMessage());
                            }
                        }
                );
    }

    private String extractSuggestion(Pattern pattern, String text) {
        if (text == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(text);
        String found = null;
        while (matcher.find()) {
            found = matcher.group(1).trim();
        }
        if (found == null || found.isEmpty()) {
            return null;
        }
        found = found.replaceAll("^\\*+|\\*+$", "").trim();
        found = found.replaceAll("^\"|\"$", "").trim();
        return found.isEmpty() ? null : found;
    }

    private void applySuggestedPair(String suggestedRu, String suggestedEn) {
        if (suggestedRu != null) {
            populateLanguageFields(RUSSIAN_KEY, suggestedRu);
        }
        if (suggestedEn != null) {
            populateLanguageFields(ENGLISH_KEY, suggestedEn);
        }
    }

    private void populateLanguageFields(String language, String value) {
        boolean firstPopulated = false;
        for (WordInputField field : wordInputFields) {
            if (language.equals(field.language)) {
                if (!firstPopulated) {
                    field.editText.setText(value);
                    firstPopulated = true;
                } else {
                    field.editText.setText("$del");
                }
            }
        }
    }

    private CharSequence formatMarkdown(String markdown) {
        if (markdown == null) {
            return "";
        }
        String html = markdown
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
        html = html.replaceAll("(?m)^#{1,6}\\s+([^\\n]+)$", "<b>$1</b>");
        html = html.replaceAll("\\*\\*([^*\\n]+?)\\*\\*", "<b>$1</b>");
        html = html.replaceAll("(?<!\\*)\\*([^*\\n]+?)\\*(?!\\*)", "<i>$1</i>");
        html = html.replaceAll("(?m)^\\s*-\\s+", "• ");
        html = html.replace("\n", "<br>");
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
    }

    private EditText findFirstEditTextByLang(String language) {
        for (WordInputField field : wordInputFields) {
            if (language.equals(field.language)) {
                return field.editText;
            }
        }
        return null;
    }

    private String collectWordsByLanguage(String language) {
        return wordInputFields.stream()
                .filter(f -> language.equals(f.language))
                .map(f -> f.editText.getText().toString().trim())
                .filter(s -> !s.isEmpty() && !"$del".equals(s))
                .collect(Collectors.joining(", "));
    }

    private void exitWithoutSaving() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void deleteQuestion(View view) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null || question == null || question.getId() == null) {
            return;
        }
        Context context = getContext();
        activity.getQuestionService().delete(question.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        unused -> {
                            activity.getRecyclerAdapter().removeQuestion(question);
                            Toast.makeText(context, "Question was deleted", Toast.LENGTH_SHORT).show();
                            activity.fillTiles();
                            exitWithoutSaving();
                        },
                        error -> Toast.makeText(context,
                                String.format("Couldn't delete a question <%d>, reason: %s",
                                        question.getId(), error.getLocalizedMessage()),
                                Toast.LENGTH_LONG).show()
                );
    }

    private int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }

    @NonNull
    private String getMatchedQuestionsTextView(List<Question> matchedQuestions, String language) {
        return matchedQuestions.stream()
                .map(question1 -> RecyclerAdapter.answeredQuestionToString(question1, language))
                .collect(Collectors.joining("\n"));
    }

    @NonNull
    private List<Question> findMattchedQuestions(Editable editable, String language) {
        String query = editable.toString().trim();
        return allQuestions.stream().filter(
                q -> q.getWords().stream()
                        .filter(w -> w.getLanguage().equals(language))
                        .anyMatch(w -> w.getWord().contains(query))
        ).collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void save(View view) {
        if (questionEditMode == QuestionEditMode.CREATE) {
            question = new Question();
        }
        if (confirmedCheckBox != null) {
            question.setConfirmed(confirmedCheckBox.isChecked());
        }
        for (WordInputField field : wordInputFields) {
            String editedWord = field.editText.getText().toString().trim();
            if (editedWord.equals("$del")) {
                if (field.word != null) {
                    question.getWords().remove(field.word);
                }
            } else {
                if (questionEditMode == QuestionEditMode.CREATE) {
                    question.getWords().add(new Word(null, editedWord, field.language, null));
                } else if (field.word != null) {
                    field.word.setWord(editedWord);
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
                        savedQuestion -> {
                            if (savedQuestion != null && question != null) {
                                question.setConfirmed(savedQuestion.getConfirmed());
                            }
                            Toast.makeText(context, "Question was saved", Toast.LENGTH_SHORT).show();
                            activity.getRecyclerAdapter().notifyDataSetChanged();
                            exitWithoutSaving();
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
