package com.demo.quizarena;

import com.demo.quizarena.api.dto.SpeedGameStartResponse;
import com.demo.quizarena.api.dto.SpeedGameSubmitRequest;
import com.demo.quizarena.api.dto.SpeedGameSubmitResponse;
import com.demo.quizarena.domain.Question;
import com.demo.quizarena.repo.QuestionRepository;
import com.demo.quizarena.service.ScoreService;
import com.demo.quizarena.service.SpeedGameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class SpeedGameServiceTest {

    @Test
    void testFullGameFlow() {
        // 1. Mock 依赖
        QuestionRepository repo = Mockito.mock(QuestionRepository.class);
        ScoreService scoreService = new ScoreService();

        // 构造假数据
        List<Question> mockQuestions = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Question q = new Question();
            q.setStem("Q" + i);
            q.setCorrectAnswer("A");
            q.setOptionsJson("[\"A\",\"B\"]");
            q.setBasePoints(100);
            q.setTimeLimitSec(15);
            // 这里需要通过反射或修改 Question 类使其 ID 可设，或者 mock getId()
            // 为简化，这里假设 Question 存入 List 后我们能引用到它
            mockQuestions.add(q);
        }

        // Mock 仓库行为
        when(repo.findRandomQuestions(anyInt())).thenReturn(mockQuestions);

        SpeedGameService service = new SpeedGameService(repo, scoreService);

        // 2. Start Game
        SpeedGameStartResponse startRes = service.start("Player1");
        Assertions.assertNotNull(startRes.sessionId);
        Assertions.assertEquals(2, startRes.totalQuestions);
        Assertions.assertEquals("Q0", startRes.firstQuestion.stem);

        // 3. Submit Correct Answer for Q1
        SpeedGameSubmitRequest req1 = new SpeedGameSubmitRequest();
        req1.questionId = startRes.firstQuestion.id; // 注意：如果 Question id 为 null 可能需要处理
        req1.answer = "A";

        SpeedGameSubmitResponse res1 = service.submit(startRes.sessionId, req1);
        Assertions.assertTrue(res1.correct);
        Assertions.assertNotNull(res1.nextQuestion);
        Assertions.assertEquals("Q1", res1.nextQuestion.stem);

        // 4. Submit Wrong Answer for Q2
        SpeedGameSubmitRequest req2 = new SpeedGameSubmitRequest();
        req2.answer = "B";
        SpeedGameSubmitResponse res2 = service.submit(startRes.sessionId, req2);
        Assertions.assertFalse(res2.correct);
        Assertions.assertNull(res2.nextQuestion); // Game Finished
        Assertions.assertTrue(res2.finished);
    }
}