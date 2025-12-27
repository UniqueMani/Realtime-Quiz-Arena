package com.demo.quizarena.api;

import com.demo.quizarena.api.dto.JoinRoomRequest;
import com.demo.quizarena.api.dto.JoinRoomResponse;
import com.demo.quizarena.api.dto.QuestionWithAnswerResponse;
import com.demo.quizarena.api.dto.RoomCreateResponse;
import com.demo.quizarena.realtime.QuestionPush;
import com.demo.quizarena.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Room")
public class RoomController {

    private final RoomService roomService;
    private final SimpMessagingTemplate messaging;

    public RoomController(RoomService roomService, SimpMessagingTemplate messaging) {
        this.roomService = roomService;
        this.messaging = messaging;
    }

    @PostMapping
    public RoomCreateResponse createRoom() {
        try {
            System.out.println("[RoomController.createRoom] 收到创建房间请求");
            RoomService.Room room = roomService.createRoom();
            System.out.println("[RoomController.createRoom] 房间创建成功: code=" + room.code);
            return new RoomCreateResponse(room.code, room.hostToken);
        } catch (Exception e) {
            System.err.println("[RoomController.createRoom] 创建房间时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw e; // 让全局异常处理器处理
        }
    }

    @PostMapping("/{code}/join")
    public JoinRoomResponse join(@PathVariable String code, @Valid @RequestBody JoinRoomRequest req) {
        try {
            System.out.println("[RoomController.join] 收到加入房间请求: code=" + code + ", nickname=" + req.nickname);
            String normalizedCode = code.toUpperCase();
            RoomService.Player p = roomService.joinRoom(normalizedCode, req.nickname);
            System.out.println("[RoomController.join] 加入房间成功: playerId=" + p.playerId);
            return new JoinRoomResponse(p.playerId, p.nickname);
        } catch (Exception e) {
            System.err.println("[RoomController.join] 加入房间时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw e; // 让全局异常处理器处理
        }
    }

    @PostMapping("/{code}/start")
    public ResponseEntity<QuestionPush> start(@PathVariable String code, @RequestHeader("X-Host-Token") String hostToken) {
        try {
            System.out.println("[RoomController.start] 收到开始游戏请求: code=" + code + ", hostToken=" + (hostToken != null ? "已提供" : "缺失"));
            String normalizedCode = code.toUpperCase();
            QuestionPush push = roomService.startGameAndOpenFirstQuestion(normalizedCode, hostToken);
            System.out.println("[RoomController.start] 游戏启动成功，题目ID=" + push.questionId);
            // Broadcast the same question to all players so they start in sync
            messaging.convertAndSend("/topic/room/" + normalizedCode + "/question", push);
            // Also broadcast current leaderboard (clears/initializes on clients)
            messaging.convertAndSend("/topic/room/" + normalizedCode + "/leaderboard", roomService.leaderboard(roomService.getRoomOrThrow(normalizedCode)));
            return ResponseEntity.ok(push);
        } catch (Exception e) {
            System.err.println("[RoomController.start] 启动游戏时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw e; // 让全局异常处理器处理
        }
    }

    /**
     * For late joiners / page refresh: fetch the current question if the game has started.
     * Returns 204 No Content if not started yet.
     */
    @GetMapping("/{code}/current")
    public ResponseEntity<QuestionPush> current(@PathVariable String code) {
        QuestionPush push = roomService.getCurrentQuestionPush(code.toUpperCase());
        if (push == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(push);
    }

    @PostMapping("/{code}/next")
    public ResponseEntity<QuestionPush> next(@PathVariable String code, @RequestHeader("X-Host-Token") String hostToken) {
        QuestionPush push = roomService.nextQuestion(code.toUpperCase(), hostToken);
        // Broadcast the next question to all players
        messaging.convertAndSend("/topic/room/" + code.toUpperCase() + "/question", push);
        return ResponseEntity.ok(push);
    }

    @GetMapping("/{code}/questions")
    public ResponseEntity<List<QuestionWithAnswerResponse>> getQuestions(@PathVariable String code) {
        try {
            System.out.println("[RoomController.getQuestions] 收到获取题目请求: code=" + code);
            String normalizedCode = code.toUpperCase();
            List<QuestionWithAnswerResponse> questions = roomService.getQuestionsWithAnswers(normalizedCode);
            System.out.println("[RoomController.getQuestions] 成功获取 " + questions.size() + " 道题目");
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            System.err.println("[RoomController.getQuestions] 获取题目时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw e; // 让全局异常处理器处理
        }
    }
}
