package com.demo.quizarena.service;

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

        // current question window
        public Question currentQuestion;
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
        String code = randomCode(6);
        String hostToken = UUID.randomUUID().toString();
        Room room = new Room(code, hostToken);
        rooms.put(code, room);
        return room;
    }

    public Room getRoomOrThrow(String code) {
        Room room = rooms.get(code);
        if (room == null) throw new NoSuchElementException("Room not found: " + code);
        return room;
    }

    public Player joinRoom(String code, String nickname) {
        Room room = getRoomOrThrow(code);
        String playerId = UUID.randomUUID().toString();
        Player p = new Player(playerId, nickname);
        room.players.put(playerId, p);
        return p;
    }

    public QuestionPush startGameAndOpenFirstQuestion(String code, String hostToken) {
        Room room = getRoomOrThrow(code);
        requireHost(room, hostToken);

        room.status = RoomStatus.IN_GAME;

        // Demo: use the first question of the first quiz (or create a built-in default)
        Question q = questionRepository.findAll().stream().findFirst().orElse(null);
        if (q == null) {
            // no questions in DB; we'll open a synthetic question.
            return openSyntheticQuestion(room);
        } else {
            room.currentQuestion = q;
            room.openedAtMs = System.currentTimeMillis();
            room.closedAtMs = room.openedAtMs + (q.getTimeLimitSec() * 1000L);
            return toQuestionPush(q, room.openedAtMs, room.closedAtMs);
        }
    }

    public boolean canAcceptAnswer(Room room, long nowMs, Long questionId) {
        if (room.status != RoomStatus.IN_GAME) return false;
        if (room.currentQuestion == null) return false;
        if (!Objects.equals(room.currentQuestion.getId(), questionId)) return false;
        return nowMs >= room.openedAtMs && nowMs <= room.closedAtMs;
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

    private void requireHost(Room room, String token) {
        if (!room.hostToken.equals(token)) throw new SecurityException("Invalid host token");
    }

    private static String randomCode(int len) {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i=0;i<len;i++) sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        return sb.toString();
    }

    private QuestionPush toQuestionPush(Question q, long openedAt, long closedAt) {
        QuestionPush push = new QuestionPush();
        push.questionId = q.getId();
        push.stem = q.getStem();
        push.options = parseOptions(q.getOptionsJson());
        push.openedAtEpochMs = openedAt;
        push.closedAtEpochMs = closedAt;
        return push;
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
        return push;
    }
}
