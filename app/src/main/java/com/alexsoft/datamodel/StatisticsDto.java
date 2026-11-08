package com.alexsoft.datamodel;

public class StatisticsDto {
    private int answeredToday;
    private int studiedToday;
    private int rightAnswerPercentage;

    public int getAnsweredToday() {
        return answeredToday;
    }

    public void setAnsweredToday(int answeredToday) {
        this.answeredToday = answeredToday;
    }

    public int getStudiedToday() {
        return studiedToday;
    }

    public void setStudiedToday(int studiedToday) {
        this.studiedToday = studiedToday;
    }

    public int getRightAnswerPercentage() {
        return rightAnswerPercentage;
    }

    public void setRightAnswerPercentage(int rightAnswerPercentage) {
        this.rightAnswerPercentage = rightAnswerPercentage;
    }
}
