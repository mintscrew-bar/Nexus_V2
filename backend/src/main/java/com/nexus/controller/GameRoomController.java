package com.nexus.controller;

import com.nexus.dto.GameRoomDto;
import com.nexus.service.GameRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameRoomController {

    private final GameRoomService gameRoomService;

    /**
     * 새로운 게임 로비(내전 방)를 생성하는 API입니다.
     */
    @PostMapping
    public ResponseEntity<GameRoomDto.Response> createGameRoom(
            @Valid @RequestBody GameRoomDto.CreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userEmail = jwt.getClaimAsString("email"); 
        GameRoomDto.Response createdRoom = gameRoomService.createGameRoom(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    /**
     * 현재 생성된 모든 게임 로비 목록을 조회하는 API입니다.
     */
    @GetMapping
    public ResponseEntity<List<GameRoomDto.Response>> getAllGameRooms() {
        List<GameRoomDto.Response> allRooms = gameRoomService.getAllGameRooms();
        return ResponseEntity.ok(allRooms);
    }

    /**
     * 팀 구성을 시작하는 API (방장만 가능)
     * @param roomCode 로비의 고유 코드
     * @param request 팀 구성 방식(경매/자동)을 담은 DTO
     * @param jwt 현재 로그인한 사용자 정보
     * @return 작업 완료 후 HTTP 상태 코드 200 (OK)
     */
    @PostMapping("/{roomCode}/team-composition")
    public ResponseEntity<Void> startTeamComposition(
            @PathVariable String roomCode,
            @Valid @RequestBody GameRoomDto.StartTeamCompositionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userEmail = jwt.getClaimAsString("email");
        gameRoomService.startTeamComposition(roomCode, request, userEmail);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{roomCode}/join")
    public ResponseEntity<Void> joinGameRoom(
            @PathVariable String roomCode,
            @AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaimAsString("email");
        gameRoomService.joinGameRoom(roomCode, userEmail);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 게임 로비의 상세 정보 조회 api
     * @param   roomCode 로비의 고유 코드
     * @return GameRoomDto.Response
     */
    @GetMapping("/{roomCode}")
    public ResponseEntity<GameRoomDto.Response> getGameRoomDetails(@PathVariable String roomCode) {
        // GameRoomService에 roomCode로 방을 찾는 메서드를 구현해야 합니다.
        GameRoomDto.Response roomDetails = gameRoomService.getGameRoomByCode(roomCode);
        return ResponseEntity.ok(roomDetails);
    }
}