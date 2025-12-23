package com.demo.quizarena;

import com.demo.quizarena.repo.QuestionRepository;
import com.demo.quizarena.service.RoomService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class RoomServiceTest {

    @Test
    void createAndJoinRoom() {
        QuestionRepository repo = Mockito.mock(QuestionRepository.class);
        RoomService service = new RoomService(repo);

        RoomService.Room room = service.createRoom();
        assertNotNull(room.code);
        assertEquals(RoomService.RoomStatus.LOBBY, room.status);

        RoomService.Player p = service.joinRoom(room.code, "Alice");
        assertNotNull(p.playerId);
        assertEquals("Alice", p.nickname);
        assertEquals(1, room.players.size());
    }
}
