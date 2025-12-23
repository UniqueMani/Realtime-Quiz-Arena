package com.demo.quizarena.realtime;

public class LeaderboardEntry {
    public String playerId;
    public String nickname;
    public int totalScore;

    public LeaderboardEntry() {}
    public LeaderboardEntry(String playerId, String nickname, int totalScore) {
        this.playerId = playerId;
        this.nickname = nickname;
        this.totalScore = totalScore;
    }
}
