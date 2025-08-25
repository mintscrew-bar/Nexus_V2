package com.nexus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_rooms")
@Getter
@Setter
public class GameRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 방 코드는 랜덤으로 생성
    @Column(unique = true, nullable = false)
    private String roomCode;

    // 방 제목
    @Column(nullable = false)
    private String title;

    // 최대 참가 인원
    @Column(nullable = false)
    private int maxParticipants;

    // 방장
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_user_id")
    private User host;

    // 방 상태
    @Column(nullable = false)
    private String status = "WAITING";

    // 팀 구성 방식
    @Enumerated(EnumType.STRING)
    private TeamCompositionMethod teamCompositionMethod;

    // 방 생성 시간
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    
    // 참가자 목록
    @OneToMany(mappedBy = "gameRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameRoomParticipant> participants = new ArrayList<>();

    // 게임 매치 목록
    @OneToMany(mappedBy = "gameRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameMatch> matches = new ArrayList<>();
}