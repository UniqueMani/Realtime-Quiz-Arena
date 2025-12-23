package com.demo.quizarena.api.dto;

import jakarta.validation.constraints.NotBlank;

public class JoinRoomRequest {
    @NotBlank
    public String nickname;
}
