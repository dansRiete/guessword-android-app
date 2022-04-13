package com.alexsoft.datamodel;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.alexsoft.datamodel.Question.ENGLISH_KEY;
import static com.alexsoft.datamodel.Question.RUSSIAN_KEY;

public class Word implements Serializable {

    private Integer id;
    private String word;
    private String language;
    private String transcription;

    public Word() {
    }

    public Word(Integer id, String word, String language, String transcription) {
        this.id = id;
        this.word = word;
        this.language = language;
        this.transcription = transcription;
    }

    @Override
    public String toString() {
        return String.format("%s%s", word, transcription != null && language.equals(RUSSIAN_KEY) ? String.format(" [%s]", transcription) : "");
    }

    public String toQuestionString() {
        return String.format("%s%s", word, transcription != null && language.equals(ENGLISH_KEY) ? String.format(" [%s]", transcription) : "");
    }

    public Integer getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getLanguage() {
        return language;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }
}
