package com.demo.quizarena.api.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateQuizRequest {
    @NotBlank
    public String title;

    public boolean isPublic = false;
}
