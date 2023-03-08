package com.alexsoft.datamodel;

import com.alexsoft.utils.CustomZonedDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {

    public static final String ENGLISH_KEY = "en";  // todo convert to enum
    public static final String RUSSIAN_KEY = "ru";

    private Integer id;
    private Double studyPoints;
    private Double studyPointsMultiplier;
    @JsonIgnore private Double previousStudyPoints;
    @JsonIgnore private Double previousStudyPointsMultiplier;
    @JsonIgnore private Boolean answeredCorrectly;
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime created;
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    private ZonedDateTime lastAccessed;
    private String tag;
    private List<Word> words = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("%d. prob=%s %s, answered: %s", id, studyPoints, words, String.valueOf(answeredCorrectly));
    }

    public Integer getId() {
        return id;
    }

    public Double getStudyPoints() {
        return studyPoints;
    }

    public Double getStudyPointsMultiplier() {
        return studyPointsMultiplier;
    }

    public Double getPreviousStudyPoints() {
        return previousStudyPoints;
    }

    public Double getPreviousStudyPointsMultiplier() {
        return previousStudyPointsMultiplier;
    }

    public Boolean getAnsweredCorrectly() {
        return answeredCorrectly;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public ZonedDateTime getLastAccessed() {
        return lastAccessed;
    }

    public String getTag() {
        return tag;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStudyPoints(Double studyPoints) {
        this.studyPoints = studyPoints;
    }

    public void setStudyPointsMultiplier(Double studyPointsMultiplier) {
        this.studyPointsMultiplier = studyPointsMultiplier;
    }

    public void setPreviousStudyPoints(Double previousStudyPoints) {
        this.previousStudyPoints = previousStudyPoints;
    }

    public void setPreviousStudyPointsMultiplier(Double previousStudyPointsMultiplier) {
        this.previousStudyPointsMultiplier = previousStudyPointsMultiplier;
    }

    public void setAnsweredCorrectly(Boolean answeredCorrectly) {
        this.answeredCorrectly = answeredCorrectly;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public void setLastAccessed(ZonedDateTime lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }
}
