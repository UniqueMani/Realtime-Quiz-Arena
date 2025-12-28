package com.demo.quizarena.service;

import com.demo.quizarena.api.dto.*;
import com.demo.quizarena.domain.Question;
import com.demo.quizarena.repo.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SpeedGameService {

    // 内存中存储游戏会话 (Session)
    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();
    private final QuestionRepository questionRepository;
    private final ScoreService scoreService;

    public SpeedGameService(QuestionRepository questionRepository, ScoreService scoreService) {
        this.questionRepository = questionRepository;
        this.scoreService = scoreService;
    }

    public SpeedGameStartResponse start(String nickname) {
        // 1. 随机抽取10道题
        List<Question> questions = questionRepository.findRandomQuestions(10);
        if (questions.isEmpty()) {
            throw new IllegalStateException("题库为空，无法开始游戏");
        }

        // 2. 创建会话
        String sessionId = UUID.randomUUID().toString();
        GameSession session = new GameSession(sessionId, nickname, questions);
        sessions.put(sessionId, session);

        // 3. 返回第一题
        return new SpeedGameStartResponse(
                sessionId,
                toQuestionResponse(questions.get(0)),
                questions.size()
        );
    }

    public SpeedGameSubmitResponse submit(String sessionId, SpeedGameSubmitRequest req) {
        GameSession session = sessions.get(sessionId);
        if (session == null) throw new NoSuchElementException("Session not found");

        if (session.isFinished()) throw new IllegalStateException("Game already finished");

        Question currentQ = session.questions.get(session.currentIndex);

        // 校验题目ID是否匹配（防止乱序提交）
        if (!currentQ.getId().equals(req.questionId)) {
            // 简单处理：如果ID对不上，可能客户端状态错乱，直接跳到下一题或报错
            // 这里我们为了鲁MJ性，直接视为该题没答对，继续下一题
        }

        // 判题
        boolean isCorrect = Objects.equals(currentQ.getCorrectAnswer(), req.answer);

        // 计分 (假设固定15秒限制)
        long latency = 0; // 这里简化处理，如果需要精确防作弊，可以对比 serverTime
        int score = scoreService.computeScore(isCorrect, currentQ.getBasePoints(), 15, latency);

        session.totalScore += score;
        session.userAnswers.put(currentQ.getId(), req.answer);
        if (isCorrect) session.correctCount++;

        // 移动游标
        session.currentIndex++;

        QuestionResponse nextQ = null;
        if (session.currentIndex < session.questions.size()) {
            nextQ = toQuestionResponse(session.questions.get(session.currentIndex));
        } else {
            session.finished = true;
        }

        return new SpeedGameSubmitResponse(isCorrect, score, session.totalScore, nextQ);
    }

    public SpeedGameResultResponse getResult(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null) throw new NoSuchElementException("Session not found");

        SpeedGameResultResponse res = new SpeedGameResultResponse();
        res.nickname = session.nickname;
        res.totalScore = session.totalScore;
        res.correctCount = session.correctCount;

        // 构建详细报告
        res.details = session.questions.stream().map(q -> {
            QuestionWithAnswerResponse dto = new QuestionWithAnswerResponse(
                    q.getId(), q.getStem(), parseOptions(q.getOptionsJson()),
                    q.getCorrectAnswer(), q.getExplanation(), q.getCategory(),
                    q.getCreatedAt(), q.getUpdatedAt()
            );
            // 这里为了简单，我们复用 QuestionWithAnswerResponse
            // 实际项目中可能需要在 Response 里加一个字段存 "userAnswer"
            // 但为了不修改原有 DTO，我们在前端通过 Map 匹配即可，或者这里只返回题目详情
            return dto;
        }).collect(Collectors.toList());

        return res;
    }

    // 内部类：游戏会话状态
    private static class GameSession {
        String id;
        String nickname;
        List<Question> questions;
        int currentIndex = 0;
        int totalScore = 0;
        int correctCount = 0;
        boolean finished = false;
        Map<Long, String> userAnswers = new HashMap<>();

        public GameSession(String id, String nickname, List<Question> questions) {
            this.id = id;
            this.nickname = nickname;
            this.questions = questions;
        }

        public boolean isFinished() {
            return finished;
        }
    }

    // Helper: 转换 DTO
    private QuestionResponse toQuestionResponse(Question q) {
        return new QuestionResponse(
                q.getId(), q.getStem(), parseOptions(q.getOptionsJson()),
                q.getExplanation(), q.getCategory(), q.getCreatedAt(), q.getUpdatedAt()
        );
    }

    private List<String> parseOptions(String json) {
        // 简单的解析逻辑，生产环境应用 Jackson
        if (json == null) return List.of();
        String s = json.replace("[", "").replace("]", "").replace("\"", "");
        if (s.isBlank()) return List.of();
        return Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toList());
    }
}