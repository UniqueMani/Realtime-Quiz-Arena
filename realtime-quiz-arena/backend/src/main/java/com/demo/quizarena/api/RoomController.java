package com.demo.quizarena.api;

import com.demo.quizarena.api.dto.JoinRoomRequest;
import com.demo.quizarena.api.dto.JoinRoomResponse;
import com.demo.quizarena.api.dto.RoomCreateResponse;
import com.demo.quizarena.realtime.QuestionPush;
import com.demo.quizarena.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Room")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
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
        return ResponseEntity.ok(push);
    }
}
