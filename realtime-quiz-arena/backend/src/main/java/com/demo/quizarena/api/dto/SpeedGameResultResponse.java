package com.demo.quizarena.api.dto;

import java.util.List;

public class SpeedGameResultResponse {
    public String nickname;
    public int totalScore;
    public int correctCount;
    public List<QuestionWithAnswerResponse> details; // 包含题目、正确答案、用户答案等
}