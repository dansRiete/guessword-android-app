package com.alexsoft.datamodel;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public
class Word implements Serializable {
    private Integer id;
    private String word;
    private String language;
    private String transcription;

    @Override
    public String toString() {
        return String.format("%s%s", word, transcription != null && language.equals("ru") ? String.format(" [%s]", transcription) : "");
    }

    public String toQuestionString() {
        return String.format("%s%s", word, transcription != null && language.equals("en") ? String.format(" [%s]", transcription) : "");
    }
}
