package com.nexus.dto;

import com.nexus.entity.GameRoom;
import com.nexus.entity.GameRoomParticipant;
import com.nexus.entity.TeamCompositionMethod;
import com.nexus.entity.User;
import com.nexus.validation.MultipleOfFive;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// 게임 방 관련 DTO
public class GameRoomDto {

    @Getter
    @Setter
    // 게임 방 생성 요청 DTO
    public static class CreateRequest {
        @NotBlank(message = "방 제목은 필수입니다.")
        @Size(min = 2, max = 50, message = "방 제목은 2자 이상 50자 이하로 입력해주세요.")
        private String title;

        @Min(value = 10, message = "최소 참가 인원은 10명입니다.")
        @Max(value = 50, message = "최대 참가 인원은 50명을 넘을 수 없습니다.")
        @MultipleOfFive
        private int maxParticipants;
    }

    @Getter
    @Setter
    // 게임 방 응답 DTO
    public static class Response {
        private String roomCode;
        private String title;
        private int maxParticipants;
        private int currentParticipants;
        private String hostName;
        private String status;
        private LocalDateTime createdAt;
        private List<ParticipantDto> participants;

        public static Response fromEntity(GameRoom gameRoom) {
            Response dto = new Response();
            dto.setRoomCode(gameRoom.getRoomCode());
            dto.setTitle(gameRoom.getTitle());
            dto.setMaxParticipants(gameRoom.getMaxParticipants());
            dto.setCurrentParticipants(gameRoom.getParticipants().size());
            User host = gameRoom.getHost();
            dto.setHostName(host != null ? host.getNickname() : "알 수 없음");
            dto.setStatus(gameRoom.getStatus());
            dto.setCreatedAt(gameRoom.getCreatedAt());
            dto.setParticipants(gameRoom.getParticipants().stream()
                    .map(ParticipantDto::fromEntity)
                    .collect(Collectors.toList()));
            return dto;
        }
    }

    @Getter
    @Setter
    // 참가자 정보 DTO
    public static class ParticipantDto {
        private String nickname;
        private String summonerName;
        private Integer teamNumber;

        // 참가자 정보를 DTO로 변환
        public static ParticipantDto fromEntity(GameRoomParticipant participant) {
            ParticipantDto dto = new ParticipantDto();
            User user = participant.getUser();
            dto.setNickname(user.getNickname());
            dto.setSummonerName(user.getSummonerName());
            dto.setTeamNumber(participant.getTeamNumber());
            return dto;
        }
    }
    @Getter
    @Setter
    public static class StartTeamCompositionRequest {
        @NotNull(message = "팀 구성 방식을 선택해야 합니다.")
        private TeamCompositionMethod method;
    }
}