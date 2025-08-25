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

    @Column(unique = true, nullable = false)
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