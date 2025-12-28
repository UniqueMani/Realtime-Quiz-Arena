package com.demo.quizarena.api.dto;

public class SpeedGameStartResponse {
    public String sessionId;
    public QuestionResponse firstQuestion;
    public int totalQuestions;

    public SpeedGameStartResponse(String sessionId, QuestionResponse firstQuestion, int totalQuestions) {
        this.sessionId = sessionId;
        this.firstQuestion = firstQuestion;
        this.totalQuestions = totalQuestions;
    }
}