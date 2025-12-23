package com.demo.quizarena.api.dto;

public class JoinRoomResponse {
    public String playerId;
    public String nickname;

    public JoinRoomResponse() {}
    public JoinRoomResponse(String playerId, String nickname) {
        this.playerId = playerId;
        this.nickname = nickname;
    }
}
