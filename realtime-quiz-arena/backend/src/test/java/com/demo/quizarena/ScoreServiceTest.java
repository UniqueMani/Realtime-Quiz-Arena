package com.demo.quizarena;

import com.demo.quizarena.service.ScoreService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreServiceTest {

    @Test
    void scoreIsZeroWhenWrong() {
        ScoreService s = new ScoreService();
        assertEquals(0, s.computeScore(false, 1000, 10, 100));
    }

    @Test
    void scoreDecreasesWithLatencyButHasFloor() {
        ScoreService s = new ScoreService();
        int fast = s.computeScore(true, 1000, 10, 100);
        int slow = s.computeScore(true, 1000, 10, 9000);
        int verySlow = s.computeScore(true, 1000, 10, 999999);
        assertTrue(fast > slow);
        assertEquals(300, verySlow); // 0.3 floor
    }
}
