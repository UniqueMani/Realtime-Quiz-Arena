package com.demo.quizarena.api.dto;

public class RoomCreateResponse {
    public String roomCode;
    public String hostToken; // demo token for host operations (not secure)

    public RoomCreateResponse() {}
    public RoomCreateResponse(String roomCode, String hostToken) {
        this.roomCode = roomCode;
        this.hostToken = hostToken;
    }
}
