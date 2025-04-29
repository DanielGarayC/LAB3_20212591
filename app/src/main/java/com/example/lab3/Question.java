package com.example.lab3;

import com.google.gson.annotations.SerializedName;

public class Question {
    @SerializedName("question")
    private String questionText;

    @SerializedName("correct_answer")
    private String correctAnswer;

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
