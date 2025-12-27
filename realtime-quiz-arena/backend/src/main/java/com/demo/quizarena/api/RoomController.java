package com.demo.quizarena.api;

import com.demo.quizarena.api.dto.JoinRoomRequest;
import com.demo.quizarena.api.dto.JoinRoomResponse;
import com.demo.quizarena.api.dto.RoomCreateResponse;
import com.demo.quizarena.realtime.QuestionPush;
import com.demo.quizarena.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

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
        RoomService.Room room = roomService.createRoom();
        return new RoomCreateResponse(room.code, room.hostToken);
    }

    @PostMapping("/{code}/join")
    public JoinRoomResponse join(@PathVariable String code, @Valid @RequestBody JoinRoomRequest req) {
        RoomService.Player p = roomService.joinRoom(code, req.nickname);
        return new JoinRoomResponse(p.playerId, p.nickname);
    }

    @PostMapping("/{code}/start")
    public ResponseEntity<QuestionPush> start(@PathVariable String code, @RequestHeader("X-Host-Token") String hostToken) {
        QuestionPush push = roomService.startGameAndOpenFirstQuestion(code, hostToken);
        // Broadcast the same question to all players so they start in sync
        messaging.convertAndSend("/topic/room/" + code.toUpperCase() + "/question", push);
        // Also broadcast current leaderboard (clears/initializes on clients)
        messaging.convertAndSend("/topic/room/" + code.toUpperCase() + "/leaderboard", roomService.leaderboard(roomService.getRoomOrThrow(code.toUpperCase())));
        return ResponseEntity.ok(push);
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
}
