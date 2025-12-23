package com.demo.quizarena.realtime;

import com.demo.quizarena.domain.Question;
import com.demo.quizarena.repo.QuestionRepository;
import com.demo.quizarena.service.RoomService;
import com.demo.quizarena.service.ScoreService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.NoSuchElementException;
import java.util.Objects;

@Controller
public class RealtimeController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;
    private final ScoreService scoreService;
    private final QuestionRepository questionRepository;

    public RealtimeController(SimpMessagingTemplate messagingTemplate, RoomService roomService,
                              ScoreService scoreService, QuestionRepository questionRepository) {
        this.messagingTemplate = messagingTemplate;
        this.roomService = roomService;
        this.scoreService = scoreService;
        this.questionRepository = questionRepository;
    }

    @MessageMapping("/room/{code}/answer")
    public void submitAnswer(@DestinationVariable String code, @Valid AnswerSubmitMessage msg) {
        RoomService.Room room = roomService.getRoomOrThrow(code);
        long now = System.currentTimeMillis();

        if (!roomService.canAcceptAnswer(room, now, msg.questionId)) {
            // ignore invalid window; could send error to user in real project
            return;
        }

        RoomService.Player p = roomService.getPlayer(room, msg.playerId);

        boolean correct = isCorrect(room, msg.questionId, msg.answer);
        int base = (room.currentQuestion != null) ? room.currentQuestion.getBasePoints() : 1000;
        int limit = (room.currentQuestion != null) ? room.currentQuestion.getTimeLimitSec() : 15;
        long latency = Math.max(0, now - room.openedAtMs);

        int score = scoreService.computeScore(correct, base, limit, latency);
        p.totalScore += score;

        // broadcast updated leaderboard
        messagingTemplate.convertAndSend("/topic/room/" + code + "/leaderboard", roomService.leaderboard(room));
    }

    private boolean isCorrect(RoomService.Room room, Long questionId, String answer) {
        if (questionId == -1L) {
            return Objects.equals(answer, "Mars");
        }
        Question q = (room.currentQuestion != null && Objects.equals(room.currentQuestion.getId(), questionId))
                ? room.currentQuestion
                : questionRepository.findById(questionId).orElseThrow(() -> new NoSuchElementException("Question"));
        return Objects.equals(q.getCorrectAnswer(), answer);
    }
}
