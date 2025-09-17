package com.nexus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    // ... 기존 id, keycloakId, userCode, email, nickname 등 필드 ...
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Keycloak 제거: 내부 인증으로 전환. 과거 컬럼은 nullable 로 유지(마이그레이션 최소화)
    @Column(unique = true)
    private String keycloakId;

    @Column(unique = true)
    private String userCode;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    private String summonerName;
    private String riotId;

    @Column(unique = true)
    private String puuid;

    private int profileIconId;
    private long summonerLevel;
    private String avatarUrl;

    // 내부 인증용 비밀번호 해시(BCrypt)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // --- 랭크 정보 필드 (새로 추가) ---
    private String soloTier;      // 예: "GOLD", "DIAMOND"
    private String soloRank;      // 예: "I", "II", "III", "IV"
    private int soloLeaguePoints; // 0-100 LP

    private String flexTier;
    private String flexRank;
    private int flexLeaguePoints;
    // --- 여기까지 ---

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}