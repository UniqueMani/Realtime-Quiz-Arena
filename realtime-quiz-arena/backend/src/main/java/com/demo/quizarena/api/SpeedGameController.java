package com.demo.quizarena.api;

import com.demo.quizarena.api.dto.*;
import com.demo.quizarena.service.SpeedGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/speed")
@Tag(name = "Speed Mode (Highlights)", description = "快问快答模式接口")
public class SpeedGameController {

    private final SpeedGameService speedGameService;

    public SpeedGameController(SpeedGameService speedGameService) {
        this.speedGameService = speedGameService;
    }

    @Operation(summary = "开始快问快答", description = "创建新会话，随机抽取10题，返回第一题")
    @PostMapping("/start")
    public ResponseEntity<SpeedGameStartResponse> startGame(@RequestParam String nickname) {
        return ResponseEntity.ok(speedGameService.start(nickname));
    }

    @Operation(summary = "提交答案", description = "提交当前题目答案，自动流转到下一题")
    @PostMapping("/{sessionId}/submit")
    public ResponseEntity<SpeedGameSubmitResponse> submitAnswer(
            @PathVariable String sessionId,
            @RequestBody SpeedGameSubmitRequest req) {
        return ResponseEntity.ok(speedGameService.submit(sessionId, req));
    }

    @Operation(summary = "获取结算结果", description = "获取游戏结束后的详细战报")
    @GetMapping("/{sessionId}/result")
    public ResponseEntity<SpeedGameResultResponse> getResult(@PathVariable String sessionId) {
        return ResponseEntity.ok(speedGameService.getResult(sessionId));
    }
}