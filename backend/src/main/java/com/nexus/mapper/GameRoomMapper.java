
package com.nexus.mapper;

import com.nexus.dto.GameRoomDto;
import com.nexus.entity.GameRoom;
import com.nexus.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class GameRoomMapper {

    public GameRoomDto.Response toResponseDto(GameRoom gameRoom) {
        if (gameRoom == null) {
            return null;
        }

        GameRoomDto.Response dto = new GameRoomDto.Response();
        dto.setRoomCode(gameRoom.getRoomCode());
        dto.setTitle(gameRoom.getTitle());
        dto.setMaxParticipants(gameRoom.getMaxParticipants());
        dto.setCurrentParticipants(gameRoom.getParticipants().size());
        User host = gameRoom.getHost();
        dto.setHostName(host != null ? host.getNickname() : "알 수 없음");

        dto.setStatus(gameRoom.getStatus().name()); // Enum to String
        
        dto.setCreatedAt(gameRoom.getCreatedAt());
        dto.setParticipants(gameRoom.getParticipants().stream()
                .map(GameRoomDto.ParticipantDto::fromEntity)
                .collect(Collectors.toList()));
        return dto;
    }
}