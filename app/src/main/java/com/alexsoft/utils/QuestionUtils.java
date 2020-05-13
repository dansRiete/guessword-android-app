package com.alexsoft.utils;

import com.alexsoft.datamodel.Question;

import static com.alexsoft.utils.ApplicationConstants.*;

public class QuestionUtils {
    public static boolean isStudied(Question question) {
        return question != null && question.getStudyPoints() <= STUDY_COMPLETION_THRESHOLD;
    }
    public static boolean isAnswered(Question question) {
        return question != null && question.getAnsweredCorrectly() != null;
    }
}
