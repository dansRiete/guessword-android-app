package com.alexsoft.utils;

import com.alexsoft.datamodel.Question;

import static com.alexsoft.utils.ApplicationConstants.*;

public class QuestionUtils {
    public static boolean hasBeenStudied(Question question) {
        return isAnswered(question)
                && question.getPreviousStudyPoints() > STUDY_COMPLETION_THRESHOLD
                && question.getStudyPoints() <= STUDY_COMPLETION_THRESHOLD;
    }
    public static boolean alreadyStudiedAnsweredWrong(Question question) {
        return isAnswered(question)
                && question.getPreviousStudyPoints() <= STUDY_COMPLETION_THRESHOLD
                && question.getStudyPoints() > STUDY_COMPLETION_THRESHOLD;
    }
    public static boolean isAnswered(Question question) {
        return question != null && question.getAnsweredCorrectly() != null;
    }

}
