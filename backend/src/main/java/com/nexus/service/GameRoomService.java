package com.nexus.service;

import com.nexus.dto.GameRoomDto;
import com.nexus.dto.RiotApiDto;
import com.nexus.entity.GameMatch;
import com.nexus.entity.GameRoom;
import com.nexus.entity.GameRoomParticipant;
import com.nexus.entity.TeamCompositionMethod;
import com.nexus.entity.User;
import com.nexus.exception.RoomNotFoundException;
import com.nexus.exception.UnauthorizedException;
import com.nexus.exception.UserNotFoundException;
import com.nexus.repository.GameMatchRepository; // 이제 이 import가 성공합니다.
import com.nexus.repository.GameRoomRepository;
import com.nexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono; // Mono와 Flux를 import 합니다.

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;
    private final GameMatchRepository gameMatchRepository; // GameMatchRepository를 주입받습니다.
    private final UserRepository userRepository;
    private final RiotApiService riotApiService;

    @Transactional
    public GameRoomDto.Response createGameRoom(GameRoomDto.CreateRequest request, String userEmail) {
        User host = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("이메일에 해당하는 사용자를 찾을 수 없습니다: " + userEmail));

        GameRoom gameRoom = new GameRoom();
        gameRoom.setTitle(request.getTitle());
        gameRoom.setMaxParticipants(request.getMaxParticipants());
        gameRoom.setHost(host);
        gameRoom.setRoomCode(generateUniqueRoomCode());

        GameRoomParticipant hostAsParticipant = new GameRoomParticipant();
        hostAsParticipant.setUser(host);
        hostAsParticipant.setGameRoom(gameRoom);
        gameRoom.getParticipants().add(hostAsParticipant);

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);

        return GameRoomDto.Response.fromEntity(savedGameRoom);
    }

    public List<GameRoomDto.Response> getAllGameRooms() {
        return gameRoomRepository.findAll().stream()
                .map(GameRoomDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void startTeamComposition(String roomCode, GameRoomDto.StartTeamCompositionRequest request, String userEmail) {
        GameRoom gameRoom = gameRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RoomNotFoundException("해당 코드를 가진 방을 찾을 수 없습니다: " + roomCode));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        if (!gameRoom.getHost().equals(user)) {
            throw new UnauthorizedException("방장만이 팀 구성을 시작할 수 있습니다.");
        }
        
        if (!"WAITING".equals(gameRoom.getStatus())) {
            throw new IllegalStateException("참가자 모집 중에만 팀 구성을 시작할 수 있습니다.");
        }

        gameRoom.setTeamCompositionMethod(request.getMethod());
        
        if (request.getMethod() == TeamCompositionMethod.AUCTION) {
            gameRoom.setStatus("AUCTION_IN_PROGRESS");
        } else {
            gameRoom.setStatus("AUTO_TEAM_COMPOSITION");
        }

        gameRoomRepository.save(gameRoom);
    }

    @Transactional
    public Mono<Void> startMatches(String roomCode, String userEmail) {
        GameRoom gameRoom = gameRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RoomNotFoundException("해당 코드를 가진 방을 찾을 수 없습니다: " + roomCode));
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        if (!gameRoom.getHost().equals(user)) {
            throw new UnauthorizedException("방장만이 게임을 시작할 수 있습니다.");
        }

        int numberOfMatches = gameRoom.getMaxParticipants() / 10;
        
        RiotApiDto.TournamentCodeRequest tournamentRequest = new RiotApiDto.TournamentCodeRequest();
        tournamentRequest.setMapType("SUMMONERS_RIFT");
        tournamentRequest.setPickType("TOURNAMENT_DRAFT");
        tournamentRequest.setSpectatorType("ALL");
        tournamentRequest.setTeamSize(5);

        // 1. Stub Provider 등록 -> 2. Stub Tournament 등록 -> 3. Tournament Code 생성
        return riotApiService.createProvider("http://localhost:8080/api/callback") // 콜백 URL은 실제 동작하지 않아도 괜찮습니다.
                .flatMap(providerId -> riotApiService.createTournament(providerId, gameRoom.getTitle()))
                .flatMap(tournamentId -> {
                    // 3. 생성된 tournamentId를 사용하여 토너먼트 코드 생성
                    return Flux.range(0, numberOfMatches)
                            .flatMap(i -> riotApiService.createTournamentCodes(tournamentRequest, tournamentId)) // 하드코딩된 1L 대신 동적으로 받은 tournamentId 사용
                            .flatMap(codes -> {
                                String tournamentCode = codes.get(0);
                                GameMatch match = new GameMatch();
                                match.setGameRoom(gameRoom);
                                match.setTournamentCode(tournamentCode);
                                match.setStatus("PENDING");
                                gameMatchRepository.save(match);
                                return Mono.empty();
                            })
                            .then();
                })
                .then(Mono.fromRunnable(() -> {
                    gameRoom.setStatus("IN_PROGRESS");
                    gameRoomRepository.save(gameRoom);
                }));
    }

    private String generateUniqueRoomCode() {
        String roomCode;
        do {
            roomCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (gameRoomRepository.findByRoomCode(roomCode).isPresent());
        return roomCode;
    }
}