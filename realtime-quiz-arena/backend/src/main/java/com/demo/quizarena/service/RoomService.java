package com.demo.quizarena.service;

import com.demo.quizarena.api.dto.QuestionWithAnswerResponse;
import com.demo.quizarena.domain.Question;
import com.demo.quizarena.repo.QuestionRepository;
import com.demo.quizarena.realtime.LeaderboardEntry;
import com.demo.quizarena.realtime.LeaderboardPush;
import com.demo.quizarena.realtime.QuestionPush;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RoomService {

    public enum RoomStatus { LOBBY, IN_GAME, FINISHED }

    public static class Player {
        public final String playerId;
        public final String nickname;
        public int totalScore = 0;

        public Player(String playerId, String nickname) {
            this.playerId = playerId;
            this.nickname = nickname;
        }
    }

    public static class Room {
        public final String code;
        public final String hostToken;
        public RoomStatus status = RoomStatus.LOBBY;

        public final Map<String, Player> players = new ConcurrentHashMap<>();

        // 当前房间的题目列表
        public List<Question> questionList = new ArrayList<>();
        // 当前题目索引（初始为 -1，表示未开始）
        public int currentQuestionIndex = -1;

        // current question window
        public Question currentQuestion;
        // last question payload we pushed to clients (so late joiners can fetch via REST)
        public QuestionPush currentQuestionPush;
        public long openedAtMs;
        public long closedAtMs;

        public Room(String code, String hostToken) {
            this.code = code;
            this.hostToken = hostToken;
        }
    }

    private final QuestionRepository questionRepository;

    // in-memory rooms (demo)
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public RoomService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Room createRoom() {
        System.out.println("[RoomService.createRoom] 开始创建房间");
        String code = randomCode(6);
        String hostToken = UUID.randomUUID().toString();
        Room room = new Room(code, hostToken);
        rooms.put(code, room);
        System.out.println("[RoomService.createRoom] 房间创建成功: code=" + code + ", 当前房间数=" + rooms.size());
        return room;
    }

    public Room getRoomOrThrow(String code) {
        System.out.println("[RoomService.getRoomOrThrow] 查找房间: code=" + code + ", 当前房间数=" + rooms.size());
        Room room = rooms.get(code);
        if (room == null) {
            System.err.println("[RoomService.getRoomOrThrow] 房间未找到: code=" + code + ", 可用房间: " + rooms.keySet());
            throw new NoSuchElementException("房间未找到: " + code);
        }
        return room;
    }

    public Player joinRoom(String code, String nickname) {
        System.out.println("[RoomService.joinRoom] 开始加入房间: code=" + code + ", nickname=" + nickname);
        Room room = getRoomOrThrow(code);
        String playerId = UUID.randomUUID().toString();
        Player p = new Player(playerId, nickname);
        room.players.put(playerId, p);
        System.out.println("[RoomService.joinRoom] 加入房间成功: playerId=" + playerId + ", 房间当前人数=" + room.players.size());
        return p;
    }

    public QuestionPush startGameAndOpenFirstQuestion(String code, String hostToken) {
        System.out.println("[RoomService.startGameAndOpenFirstQuestion] 开始启动游戏: code=" + code);
        Room room = getRoomOrThrow(code);
        System.out.println("[RoomService.startGameAndOpenFirstQuestion] 房间找到: code=" + room.code + ", status=" + room.status);
        requireHost(room, hostToken);
        System.out.println("[RoomService.startGameAndOpenFirstQuestion] 主机令牌验证通过");

        room.status = RoomStatus.IN_GAME;

        // 先检查数据库中的题目总数
        long totalQuestions = questionRepository.count();
        System.out.println("[RoomService.startGameAndOpenFirstQuestion] 数据库中总共有 " + totalQuestions + " 道题目");
        
        if (totalQuestions == 0) {
            System.err.println("[RoomService.startGameAndOpenFirstQuestion] 数据库中没有题目！请检查 QuestionDataInitializer 是否正常执行");
            throw new IllegalStateException("数据库中没有题目，请先初始化题目数据。请重启后端应用以确保数据初始化。");
        }
        
        // 调用随机抽题获取20道题目
        List<Question> questions;
        try {
            System.out.println("[RoomService.startGameAndOpenFirstQuestion] 尝试获取20道随机题目");
            questions = questionRepository.findRandomQuestions(20);
            System.out.println("[RoomService.startGameAndOpenFirstQuestion] 成功获取 " + questions.size() + " 道题目");
            // 打印第一道题的信息用于调试
            if (!questions.isEmpty()) {
                Question first = questions.get(0);
                System.out.println("[RoomService.startGameAndOpenFirstQuestion] 第一道题: ID=" + first.getId() + ", stem=" + first.getStem());
            }
        } catch (Exception e) {
            // 如果随机抽题失败，尝试获取所有题目
            System.err.println("[RoomService.startGameAndOpenFirstQuestion] 随机抽题失败，尝试获取所有题目: " + e.getMessage());
            e.printStackTrace();
            questions = questionRepository.findAll();
            System.out.println("[RoomService.startGameAndOpenFirstQuestion] 获取到 " + questions.size() + " 道题目");
        }
        
        // 确保至少有题目
        if (questions.isEmpty()) {
            System.err.println("[RoomService.startGameAndOpenFirstQuestion] 数据库中没有题目！");
            throw new IllegalStateException("数据库中没有题目，请先初始化题目数据");
        }
        
        // 如果题目数量少于20道，使用所有题目；如果超过20道，随机选择20道
        if (questions.size() < 20) {
            System.out.println("[RoomService.startGameAndOpenFirstQuestion] 题目数量(" + questions.size() + ")少于20道，使用所有题目");
        } else if (questions.size() > 20) {
            // 如果题目超过20道，随机选择20道
            java.util.Collections.shuffle(questions);
            questions = questions.subList(0, 20);
            System.out.println("[RoomService.startGameAndOpenFirstQuestion] 题目数量超过20道，随机选择20道");
        } else {
            System.out.println("[RoomService.startGameAndOpenFirstQuestion] 题目数量正好20道");
        }

        // 存储题目列表到房间
        room.questionList = questions;
        room.currentQuestionIndex = 0;
        
        // 获取第一道题目
        Question q = questions.get(0);
        room.currentQuestion = q;
        room.openedAtMs = System.currentTimeMillis();
        room.closedAtMs = room.openedAtMs + (q.getTimeLimitSec() * 1000L);
        QuestionPush push = toQuestionPush(q, room.openedAtMs, room.closedAtMs, room);
        room.currentQuestionPush = push;
        System.out.println("[RoomService.startGameAndOpenFirstQuestion] 游戏启动成功，第一题ID=" + q.getId());
        return push;
    }

    public boolean canAcceptAnswer(Room room, long nowMs, Long questionId) {
        if (room.status != RoomStatus.IN_GAME) return false;
        // Synthetic question (DB empty)
        if (room.currentQuestion == null) {
            if (!Objects.equals(questionId, -1L)) return false;
            return nowMs >= room.openedAtMs && nowMs <= room.closedAtMs;
        }

        if (!Objects.equals(room.currentQuestion.getId(), questionId)) return false;
        return nowMs >= room.openedAtMs && nowMs <= room.closedAtMs;
    }

    /**
     * Current question payload for REST polling / late joiners.
     * Returns null if the game hasn't started yet.
     */
    public QuestionPush getCurrentQuestionPush(String code) {
        Room room = getRoomOrThrow(code);
        return room.currentQuestionPush;
    }

    public LeaderboardPush leaderboard(Room room) {
        List<LeaderboardEntry> list = room.players.values().stream()
                .sorted(Comparator.comparingInt((Player p) -> p.totalScore).reversed())
                .map(p -> new LeaderboardEntry(p.playerId, p.nickname, p.totalScore))
                .collect(Collectors.toList());
        return new LeaderboardPush(list, System.currentTimeMillis());
    }

    public Player getPlayer(Room room, String playerId) {
        Player p = room.players.get(playerId);
        if (p == null) throw new NoSuchElementException("Player not found");
        return p;
    }

    public QuestionPush nextQuestion(String code, String hostToken) {
        Room room = getRoomOrThrow(code);
        requireHost(room, hostToken);

        if (room.currentQuestionIndex < 0 || room.questionList.isEmpty()) {
            throw new IllegalStateException("Game not started yet");
        }

        if (room.currentQuestionIndex >= room.questionList.size() - 1) {
            throw new IllegalStateException("No more questions");
        }

        room.currentQuestionIndex++;
        Question q = room.questionList.get(room.currentQuestionIndex);
        room.currentQuestion = q;
        room.openedAtMs = System.currentTimeMillis();
        room.closedAtMs = room.openedAtMs + (q.getTimeLimitSec() * 1000L);
        QuestionPush push = toQuestionPush(q, room.openedAtMs, room.closedAtMs, room);
        room.currentQuestionPush = push;
        return push;
    }

    public List<QuestionWithAnswerResponse> getQuestionsWithAnswers(String code) {
        Room room = getRoomOrThrow(code);
        if (room.questionList.isEmpty()) {
            return Collections.emptyList();
        }

        return room.questionList.stream()
                .map(this::toQuestionWithAnswerResponse)
                .collect(Collectors.toList());
    }

    private void requireHost(Room room, String token) {
        if (token == null || !room.hostToken.equals(token)) {
            System.err.println("[RoomService.requireHost] 主机令牌验证失败: roomCode=" + room.code);
            throw new SecurityException("无效的主机令牌");
        }
    }

    private static String randomCode(int len) {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i=0;i<len;i++) sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        return sb.toString();
    }

    private QuestionPush toQuestionPush(Question q, long openedAt, long closedAt, Room room) {
        QuestionPush push = new QuestionPush();
        push.questionId = q.getId();
        push.stem = q.getStem();
        push.options = parseOptions(q.getOptionsJson());
        push.openedAtEpochMs = openedAt;
        push.closedAtEpochMs = closedAt;
        push.currentIndex = room.currentQuestionIndex + 1;
        push.totalCount = room.questionList.size();
        return push;
    }

    private QuestionWithAnswerResponse toQuestionWithAnswerResponse(Question q) {
        List<String> options = parseOptions(q.getOptionsJson());
        return new QuestionWithAnswerResponse(
                q.getId(),
                q.getStem(),
                options,
                q.getCorrectAnswer(),
                q.getExplanation(),
                q.getCategory(),
                q.getCreatedAt(),
                q.getUpdatedAt()
        );
    }

    private List<String> parseOptions(String jsonArrayLike) {
        // Demo parser: very naive, expects ["A","B",...]
        String s = jsonArrayLike.trim();
        if (s.startsWith("[")) s = s.substring(1);
        if (s.endsWith("]")) s = s.substring(0, s.length()-1);
        if (s.isBlank()) return List.of();
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .map(x -> x.replaceAll("^\"|\"$", ""))
                .collect(Collectors.toList());
    }

    private QuestionPush openSyntheticQuestion(Room room) {
        room.currentQuestion = null; // indicates synthetic
        room.openedAtMs = System.currentTimeMillis();
        room.closedAtMs = room.openedAtMs + 15000L;

        QuestionPush push = new QuestionPush();
        push.questionId = -1L;
        push.stem = "Demo question: Which planet is known as the Red Planet?";
        push.options = List.of("Earth", "Mars", "Jupiter", "Venus");
        push.openedAtEpochMs = room.openedAtMs;
        push.closedAtEpochMs = room.closedAtMs;
        push.currentIndex = 1;
        push.totalCount = 1;
        return push;
    }
}
