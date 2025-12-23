package com.demo.quizarena.realtime;

import java.util.List;

public class LeaderboardPush {
    public List<LeaderboardEntry> entries;
    public long serverTimeEpochMs;

    public LeaderboardPush() {}
    public LeaderboardPush(List<LeaderboardEntry> entries, long serverTimeEpochMs) {
        this.entries = entries;
        this.serverTimeEpochMs = serverTimeEpochMs;
    }
}
