package com.demo.quizarena.api.dto;

public class SpeedGameSubmitRequest {
    public Long questionId;
    public String answer; // 可以为空（超时）
    public long clientTimestampMs;
}