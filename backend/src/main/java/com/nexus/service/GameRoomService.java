package com.nexus.service;

import com.nexus.dto.GameRoomDto;
import com.nexus.dto.RiotApiDto;
import com.nexus.entity.*;
import com.nexus.exception.RoomNotFoundException;
import com.nexus.exception.UnauthorizedException;
import com.nexus.exception.UserNotFoundException;
import com.nexus.mapper.GameRoomMapper;
import com.nexus.repository.GameMatchRepository;
import com.nexus.repository.GameRoomRepository;
import com.nexus.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameRoomService {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameRoomRepository gameRoomRepository;
    private final GameMatchRepository gameMatchRepository;
    private final UserRepository userRepository;
    private final RiotApiService riotApiService;
    private final GameRoomMapper gameRoomMapper;

    @Transactional
    public GameRoomDto.Response createGameRoom(GameRoomDto.CreateRequest request, String userEmail) {
        User host = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("이메일에 해당하는 사용자를 찾을 수 없습니다: " + userEmail));

        GameRoom gameRoom = new GameRoom();
        gameRoom.setTitle(request.getTitle());
        gameRoom.setMaxParticipants(request.getMaxParticipants());
        gameRoom.setHost(host);
        gameRoom.setRoomCode(generateUniqueRoomCode());
        gameRoom.setStatus(GameRoomStatus.WAITING); // 초기 상태 설정

        GameRoomParticipant hostAsParticipant = new GameRoomParticipant();
        hostAsParticipant.setUser(host);
        hostAsParticipant.setGameRoom(gameRoom);
        gameRoom.getParticipants().add(hostAsParticipant);

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);

        return gameRoomMapper.toResponseDto(savedGameRoom);
    }

    public List<GameRoomDto.Response> getAllGameRooms() {
        return gameRoomRepository.findAll().stream()
                .map(gameRoomMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public GameRoomDto.Response joinGameRoom(String roomCode, String userEmail) {
        GameRoom gameRoom = gameRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RoomNotFoundException("해당 코드를 가진 방을 찾을 수 없습니다: " + roomCode));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        if (gameRoom.getParticipants().size() >= gameRoom.getMaxParticipants()) {
            throw new IllegalStateException("방이 가득 찼습니다.");
        }

        boolean isAlreadyParticipant = gameRoom.getParticipants().stream()
                .anyMatch(p -> p.getUser().equals(user));
        if (isAlreadyParticipant) {
            throw new IllegalStateException("이미 이 방에 참가하고 있습니다.");
        }

        GameRoomParticipant newParticipant = new GameRoomParticipant();
        newParticipant.setUser(user);
        newParticipant.setGameRoom(gameRoom);
        gameRoom.getParticipants().add(newParticipant);

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);
        GameRoomDto.Response responseDto = gameRoomMapper.toResponseDto(savedGameRoom);

        messagingTemplate.convertAndSend("/topic/gameRoom", responseDto);

        return responseDto;
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

        if (gameRoom.getStatus() != GameRoomStatus.WAITING) {
            throw new IllegalStateException("참가자 모집 중에만 팀 구성을 시작할 수 있습니다.");
        }

        gameRoom.setTeamCompositionMethod(request.getMethod());

        if (request.getMethod() == TeamCompositionMethod.AUTO) {
            List<GameRoomParticipant> participants = gameRoom.getParticipants();

            if (participants.size() % 2 != 0) {
                throw new IllegalStateException("팀을  나누기 위한 참가자 수가 홀수입니다.");
            }

            Collections.shuffle(participants);

            int halfSize = participants.size() / 2;
            for (int i = 0; i < participants.size(); i++) {
                GameRoomParticipant participant = participants.get(i);
                if (i < halfSize) {
                    participant.setTeamNumber(1);
                } else {
                    participant.setTeamNumber(2);
                }
            }
            gameRoom.setStatus(GameRoomStatus.AUTO_TEAM_COMPOSITION);
        }else if (request.getMethod() == TeamCompositionMethod.AUCTION) {
            gameRoom.setStatus(GameRoomStatus.AUTO_TEAM_COMPOSITION);
        }

        gameRoomRepository.save(gameRoom);
    }

    @Transactional
    public Mono<Void> startMatches(String roomCode, String userEmail) {
        // 블로킹 DB 조회를 별도 스레드에서 실행하여 리액티브 스레드를 방해하지 않도록 함
        Mono<GameRoom> gameRoomMono = Mono.fromCallable(() ->
                gameRoomRepository.findByRoomCode(roomCode)
                        .orElseThrow(() -> new RoomNotFoundException("해당 코드를 가진 방을 찾을 수 없습니다: " + roomCode))
        ).subscribeOn(Schedulers.boundedElastic());

        Mono<User> userMono = Mono.fromCallable(() ->
                userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."))
        ).subscribeOn(Schedulers.boundedElastic());

        return gameRoomMono.zipWith(userMono)
                .flatMap(tuple -> {
                    GameRoom gameRoom = tuple.getT1();
                    User user = tuple.getT2();

                    if (!gameRoom.getHost().equals(user)) {
                        return Mono.error(new UnauthorizedException("방장만이 게임을 시작할 수 있습니다."));
                    }

                    // 실제 참가자 기준으로 10의 배수인지 확인
                    int currentParticipants = gameRoom.getParticipants().size();
                    if (currentParticipants == 0 || currentParticipants % 10 != 0) {
                        return Mono.error(new IllegalStateException("참가자 수는 10의 배수여야 게임을 시작할 수 있습니다. 현재 참가자: " + currentParticipants));
                    }

                    int numberOfMatches = currentParticipants / 10;

                    RiotApiDto.TournamentCodeRequest tournamentRequest = new RiotApiDto.TournamentCodeRequest();
                    tournamentRequest.setMapType("SUMMONERS_RIFT");
                    tournamentRequest.setPickType("TOURNAMENT_DRAFT");
                    tournamentRequest.setSpectatorType("ALL");
                    tournamentRequest.setTeamSize(5);

                    return riotApiService.createProvider()
                            .flatMap(providerId -> riotApiService.createTournament(providerId, gameRoom.getTitle()))
                            .flatMap(tournamentId ->
                                    Flux.range(0, numberOfMatches)
                                            .flatMap(i -> riotApiService.createTournamentCodes(tournamentRequest, tournamentId))
                                            .flatMap(codes -> Mono.fromRunnable(() -> {
                                                String tournamentCode = codes.get(0);
                                                GameMatch match = new GameMatch();
                                                match.setGameRoom(gameRoom);
                                                match.setTournamentCode(tournamentCode);
                                                match.setStatus("PENDING");
                                                gameMatchRepository.save(match);
                                            }).subscribeOn(Schedulers.boundedElastic()))
                                            .then()
                            )
                            .then(Mono.fromRunnable(() -> {
                                gameRoom.setStatus(GameRoomStatus.IN_PROGRESS);
                                gameRoomRepository.save(gameRoom);
                            }).subscribeOn(Schedulers.boundedElastic()));
                })
                .then();
    }

    private String generateUniqueRoomCode() {
        String roomCode;
        do {
            roomCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (gameRoomRepository.findByRoomCode(roomCode).isPresent());
        return roomCode;
    }
}