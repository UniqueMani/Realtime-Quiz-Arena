package com.demo.quizarena.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateQuestionRequest {
    @NotBlank
    public String stem;

    @NotNull
    public List<String> options;

    @NotBlank
    public String correctAnswer; // value must be one of options

    public String explanation; // 题目解释

    public String category; // 题目分类：如"趣味知识"、"JAVA知识"等

    public int timeLimitSec = 15;
    public int basePoints = 1000;
}
