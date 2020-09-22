package com.alexsoft.datamodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.alexsoft.datamodel.Question.ENGLISH_KEY;
import static com.alexsoft.datamodel.Question.RUSSIAN_KEY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public
class Word {
    private Integer id;
    private String word;
    private String language;
    private String transcription;

    @Override
    public String toString() {
        return String.format("%s%s", word, transcription != null && language.equals(RUSSIAN_KEY) ? String.format(" [%s]", transcription) : "");
    }

    public String toQuestionString() {
        return String.format("%s%s", word, transcription != null && language.equals(ENGLISH_KEY) ? String.format(" [%s]", transcription) : "");
    }
}
