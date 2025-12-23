package com.demo.quizarena.realtime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AnswerSubmitMessage {
    @NotBlank
    public String playerId;

    @NotBlank
    public String answer;

    @NotNull
    public Long questionId;

    public long clientTimestampMs;
}
