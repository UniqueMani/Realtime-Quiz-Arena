package com.demo.quizarena.service;

import org.springframework.stereotype.Service;

@Service
public class ScoreService {

    /**
     * Demo scoring rule:
     * - If correct: score = basePoints * max(0.3, 1 - latencyMs / (timeLimitSec*1000))
     * - If wrong: 0
     */
    public int computeScore(boolean correct, int basePoints, int timeLimitSec, long latencyMs) {
        if (!correct) return 0;
        double limitMs = Math.max(1, timeLimitSec) * 1000.0;
        double factor = 1.0 - (latencyMs / limitMs);
        factor = Math.max(0.3, factor);
        return (int) Math.round(basePoints * factor);
    }
}
