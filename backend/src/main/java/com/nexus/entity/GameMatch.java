package com.nexus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_matches")
@Getter
@Setter
public class GameMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 게임 방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_room_id", nullable = false)
    private GameRoom gameRoom;

    // 토너먼트 코드
    @Column(nullable = false, unique = true)
    private String tournamentCode;
    
    // 리그 오브 레전드 매치 아이디
    private String riotMatchId; 

    // 매치 상태
    private String status = "PENDING";

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}