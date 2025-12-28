package com.demo.quizarena.api.dto;

public class SpeedGameSubmitResponse {
    public boolean correct;
    public int scoreEarned;
    public int totalScore;
    public QuestionResponse nextQuestion; // 如果为null，表示游戏结束
    public boolean finished;

    public SpeedGameSubmitResponse(boolean correct, int scoreEarned, int totalScore, QuestionResponse nextQuestion) {
        this.correct = correct;
        this.scoreEarned = scoreEarned;
        this.totalScore = totalScore;
        this.nextQuestion = nextQuestion;
        this.finished = (nextQuestion == null);
    }
}