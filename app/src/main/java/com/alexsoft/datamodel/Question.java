package com.alexsoft.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    private Integer id;

    private Double studyPoints;

    private Double studyPointsMultiplier;

    @JsonIgnore
    private Double previousStudyPoints;

    @JsonIgnore
    private Double previousStudyPointsMultiplier;

    @JsonIgnore
    private Boolean answeredCorrectly;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime created;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastAccessed;

    private String tag;

    private List<Word> words;

    @Override
    public String toString() {
        return String.format("%d. prob=%s %s, answered: %s", id, studyPoints, words, String.valueOf(answeredCorrectly));
    }
}
