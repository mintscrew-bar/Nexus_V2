package com.nexus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_room_participants")
@Getter
@Setter
public class GameRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 게임 방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_room_id", nullable = false)
    private GameRoom gameRoom;

    // 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 팀 번호
    private Integer teamNumber;

    // 참가 시간    
    @Column(nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();
}