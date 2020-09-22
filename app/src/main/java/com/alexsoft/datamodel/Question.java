package com.alexsoft.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question implements Serializable {

    public static final String ENGLISH_KEY = "en";
    public static final String RUSSIAN_KEY = "ru";

    private Integer id;

    private Double studyPoints;

    private Double studyPointsMultiplier;

    @JsonIgnore
    private Double previousStudyPoints;

    @JsonIgnore
    private Double previousStudyPointsMultiplier;

    @JsonIgnore
    private Boolean answeredCorrectly;

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
}
