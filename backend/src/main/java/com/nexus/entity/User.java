package com.nexus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // 데이터베이스 테이블 이름을 'users'로 지정
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- 아래 필드가 Keycloak 연동을 위해 추가/수정되었습니다 ---
    @Column(unique = true, nullable = false)
    private String keycloakId;   // Keycloak 사용자의 고유 ID (UUID)

    @Column(unique = true, nullable = false)
    private String email;

    // 'password' 필드는 Keycloak이 관리하므로 제거되었습니다.

    @Column(unique = true, nullable = false)
    private String nickname;

    // --- 아래는 애플리케이션 고유 정보입니다 ---
    private String summonerName; // 소환사 이름
    private String riotId;       // Riot ID (태그라인 포함)

    @Column(unique = true, nullable = false)
    private String puuid;        // Riot API에서 사용하는 영구적인 고유 식별자

    private int profileIconId;   // 소환사 아이콘 ID
    private long summonerLevel;  // 소환사 레벨
    private String avatarUrl;    // OAuth 또는 자체 업로드 프로필 이미지 URL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;           // 사용자의 권한 (USER, ADMIN 등)

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
