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
@RequestMapping("/api/games") // 이 Controller는 "/api/games" 경로의 요청을 처리합니다.
@RequiredArgsConstructor
public class GameRoomController {

    private final GameRoomService gameRoomService;

    /**
     * 새로운 게임 로비(내전 방)를 생성하는 API입니다.
     * @param request DTO (방 제목, 최대 인원 - @Valid로 검증)
     * @param jwt 현재 로그인한 사용자의 인증 정보
     * @return 생성된 로비 정보와 HTTP 상태 코드 201 (Created)
     */
    @PostMapping
    public ResponseEntity<GameRoomDto.Response> createGameRoom(
            @Valid @RequestBody GameRoomDto.CreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        // JWT 토큰에서 사용자의 이메일을 추출하여 방장을 식별합니다.
        String userEmail = jwt.getClaimAsString("email"); 
        
        // 서비스 로직을 호출하여 로비를 생성합니다.
        GameRoomDto.Response createdRoom = gameRoomService.createGameRoom(request, userEmail);
        
        // 성공적으로 생성되었음을 알리는 응답을 보냅니다.
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    /**
     * 현재 생성된 모든 게임 로비 목록을 조회하는 API입니다.
     * @return 전체 로비 목록과 HTTP 상태 코드 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<GameRoomDto.Response>> getAllGameRooms() {
        List<GameRoomDto.Response> allRooms = gameRoomService.getAllGameRooms();
        return ResponseEntity.ok(allRooms);
    }
}