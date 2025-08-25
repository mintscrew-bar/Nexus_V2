package com.nexus.service;

import com.nexus.dto.GameRoomDto;
import com.nexus.entity.GameRoom;
import com.nexus.entity.GameRoomParticipant;
import com.nexus.entity.User;
import com.nexus.exception.UserNotFoundException; // 커스텀 예외를 import 합니다.
import com.nexus.repository.GameRoomRepository;
import com.nexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;
    private final UserRepository userRepository;

    /**
     * 새로운 게임 로비를 생성합니다.
     */
    @Transactional
    public GameRoomDto.Response createGameRoom(GameRoomDto.CreateRequest request, String userEmail) {
        // 1. 방장(User) 정보 조회 및 예외 처리
        User host = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("이메일에 해당하는 사용자를 찾을 수 없습니다: " + userEmail));

        // 2. GameRoom 엔티티 생성 및 정보 설정
        GameRoom gameRoom = new GameRoom();
        gameRoom.setTitle(request.getTitle());
        gameRoom.setMaxParticipants(request.getMaxParticipants());
        gameRoom.setHost(host);
        gameRoom.setRoomCode(generateUniqueRoomCode());

        // 3. 방장을 첫 번째 참가자로 자동 추가
        GameRoomParticipant hostAsParticipant = new GameRoomParticipant();
        hostAsParticipant.setUser(host);
        hostAsParticipant.setGameRoom(gameRoom);
        gameRoom.getParticipants().add(hostAsParticipant);

        // 4. DB에 저장
        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);
        
        // 5. DTO로 변환하여 반환
        return GameRoomDto.Response.fromEntity(savedGameRoom);
    }
    
    /**
     * 현재 활성화된 모든 게임 로비 목록을 조회합니다.
     */
    public List<GameRoomDto.Response> getAllGameRooms() {
        return gameRoomRepository.findAll().stream()
                .map(GameRoomDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * DB에서 중복을 확인하며 고유한 roomCode를 생성합니다.
     */
    private String generateUniqueRoomCode() {
        String roomCode;
        do {
            roomCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (gameRoomRepository.findByRoomCode(roomCode).isPresent());
        return roomCode;
    }
}