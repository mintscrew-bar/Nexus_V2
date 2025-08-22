<img src="https://r2cdn.perplexity.ai/pplx-full-logo-primary-dark%402x.png" style="height:64px;margin-right:32px"/>

# Nexus 플랫폼 데이터베이스 세부 설계

## 설계 원칙

- 이상(Anomaly) 방지를 위한 3차 정규화
- 확장성 및 모듈화: 신규 기능 추가 시 테이블 확장 용이
- 성능 최적화: 조회 빈도가 높은 컬럼에 인덱스 적용
- 무결성 보장: 외래키, 제약조건, 트랜잭션 관리

***

## 1. 사용자 및 인증

### users

- id                : BIGSERIAL   PRIMARY KEY
- username          : VARCHAR(50) NOT NULL UNIQUE
- email             : VARCHAR(100) NOT NULL UNIQUE
- riot_puuid        : VARCHAR(100)
- profile_image_url : VARCHAR(500)
- preferred_positions: VARCHAR(100)
- user_level        : INTEGER DEFAULT 1
- experience_points : INTEGER DEFAULT 0
- role              : VARCHAR(20) DEFAULT 'USER' CHECK(role IN ('USER','MODERATOR','ADMIN'))
- status            : VARCHAR(20) DEFAULT 'ACTIVE' CHECK(status IN ('ACTIVE','SUSPENDED','BANNED'))
- created_at        : TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at        : TIMESTAMP DEFAULT CURRENT_TIMESTAMP

인덱스:

- idx_users_username (username)
- idx_users_email (email)
- idx_users_status (status)


### oauth_accounts

- id           : BIGSERIAL   PRIMARY KEY
- user_id      : BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE
- provider     : VARCHAR(20) NOT NULL  -- 'google','discord','riot'
- provider_id  : VARCHAR(100) NOT NULL
- access_token : TEXT
- refresh_token: TEXT
- created_at   : TIMESTAMP DEFAULT CURRENT_TIMESTAMP

인덱스:

- idx_oauth_user (user_id, provider)

***

## 2. 게임 방 및 참가자

### game_rooms

- id               : BIGSERIAL   PRIMARY KEY
- room_code        : VARCHAR(10) NOT NULL UNIQUE
- title            : VARCHAR(100) NOT NULL
- description      : TEXT
- game_mode        : VARCHAR(20) NOT NULL  -- '5v5','ARAM','URF'
- max_participants : INTEGER DEFAULT 10
- is_private       : BOOLEAN DEFAULT FALSE
- password         : VARCHAR(100)
- host_user_id     : BIGINT   REFERENCES users(id) ON DELETE SET NULL
- status           : VARCHAR(20) DEFAULT 'WAITING' CHECK(status IN ('WAITING','IN_PROGRESS','COMPLETED'))
- created_at       : TIMESTAMP DEFAULT CURRENT_TIMESTAMP

인덱스:

- idx_game_rooms_status (status)
- idx_game_rooms_created (created_at DESC)


### game_participants

- id           : BIGSERIAL   PRIMARY KEY
- room_id      : BIGINT      NOT NULL REFERENCES game_rooms(id) ON DELETE CASCADE
- user_id      : BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE
- position     : VARCHAR(20) -- 'TOP','JUNGLE','MID','ADC','SUPPORT'
- team         : INTEGER     -- 1 or 2
- joined_at    : TIMESTAMP   DEFAULT CURRENT_TIMESTAMP

복합 인덱스:

- idx_participants_room_user (room_id, user_id)


### game_records

- id             : BIGSERIAL   PRIMARY KEY
- room_id        : BIGINT      NOT NULL REFERENCES game_rooms(id) ON DELETE CASCADE
- participant_id : BIGINT      NOT NULL REFERENCES game_participants(id) ON DELETE CASCADE
- champion_name  : VARCHAR(50)
- kills          : INTEGER DEFAULT 0
- deaths         : INTEGER DEFAULT 0
- assists        : INTEGER DEFAULT 0
- is_winner      : BOOLEAN
- match_duration : INTEGER     -- seconds
- created_at     : TIMESTAMP   DEFAULT CURRENT_TIMESTAMP

인덱스:

- idx_records_room (room_id)
- idx_records_participant (participant_id)

***

## 3. 게시판 및 커뮤니티

### board_categories

- id                : BIGSERIAL   PRIMARY KEY
- name              : VARCHAR(50) NOT NULL
- description       : TEXT
- sort_order        : INTEGER DEFAULT 0
- is_active         : BOOLEAN DEFAULT TRUE
- read_permission   : VARCHAR(20) DEFAULT 'USER'
- write_permission  : VARCHAR(20) DEFAULT 'USER'
- created_at        : TIMESTAMP DEFAULT CURRENT_TIMESTAMP


### board_posts

- id            : BIGSERIAL   PRIMARY KEY
- category_id   : BIGINT      NOT NULL REFERENCES board_categories(id) ON DELETE RESTRICT
- user_id       : BIGINT      NOT NULL REFERENCES users(id) ON DELETE SET NULL
- title         : VARCHAR(200) NOT NULL
- content       : TEXT NOT NULL
- view_count    : INTEGER DEFAULT 0
- like_count    : INTEGER DEFAULT 0
- dislike_count : INTEGER DEFAULT 0
- comment_count : INTEGER DEFAULT 0
- is_pinned     : BOOLEAN DEFAULT FALSE
- is_hidden     : BOOLEAN DEFAULT FALSE
- tags          : VARCHAR(500)
- ip_address    : INET
- created_at    : TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at    : TIMESTAMP DEFAULT CURRENT_TIMESTAMP

인덱스:

- idx_posts_category (category_id)
- idx_posts_created (created_at DESC)
- idx_posts_tags USING gin(tags gin_trgm_ops)


### board_comments

- id            : BIGSERIAL   PRIMARY KEY
- post_id       : BIGINT      NOT NULL REFERENCES board_posts(id) ON DELETE CASCADE
- user_id       : BIGINT      NOT NULL REFERENCES users(id) ON DELETE SET NULL
- parent_id     : BIGINT      REFERENCES board_comments(id) ON DELETE CASCADE
- content       : TEXT NOT NULL
- like_count    : INTEGER DEFAULT 0
- is_hidden     : BOOLEAN DEFAULT FALSE
- ip_address    : INET
- created_at    : TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at    : TIMESTAMP DEFAULT CURRENT_TIMESTAMP

인덱스:

- idx_comments_post (post_id)


### board_attachments

- id            : BIGSERIAL   PRIMARY KEY
- post_id       : BIGINT      NOT NULL REFERENCES board_posts(id) ON DELETE CASCADE
- original_name : VARCHAR(255) NOT NULL
- stored_name   : VARCHAR(255) NOT NULL
- file_size     : BIGINT      NOT NULL
- mime_type     : VARCHAR(100)
- download_count: INTEGER DEFAULT 0
- created_at    : TIMESTAMP DEFAULT CURRENT_TIMESTAMP

***

## 4. 신고 및 관리자 로그

### reports

- id            : BIGSERIAL   PRIMARY KEY
- reporter_id   : BIGINT      NOT NULL REFERENCES users(id) ON DELETE SET NULL
- target_type   : VARCHAR(20) NOT NULL  -- 'POST','COMMENT','USER'
- target_id     : BIGINT      NOT NULL
- report_type   : VARCHAR(20) NOT NULL  -- 'SPAM','ABUSE','INAPPROPRIATE'
- content       : TEXT
- status        : VARCHAR(20) DEFAULT 'PENDING' CHECK(status IN ('PENDING','REVIEWING','RESOLVED','REJECTED'))
- admin_comment : TEXT
- processed_by  : BIGINT      REFERENCES users(id)
- processed_at  : TIMESTAMP
- created_at    : TIMESTAMP   DEFAULT CURRENT_TIMESTAMP

인덱스:

- idx_reports_status (status)


### admin_logs

- id            : BIGSERIAL   PRIMARY KEY
- admin_id      : BIGINT      NOT NULL REFERENCES users(id) ON DELETE SET NULL
- action        : VARCHAR(50) NOT NULL
- target_type   : VARCHAR(20)
- target_id     : BIGINT
- details       : JSONB
- ip_address    : INET
- created_at    : TIMESTAMP   DEFAULT CURRENT_TIMESTAMP

***

## 5. 파티셔닝 및 아카이빙 (확장 제안)

- `game_records`, `board_posts`, `board_comments` 등 대용량 예상 테이블: 연 단위 또는 월 단위 파티셔닝
- 1년 이상 데이터 아카이빙 테이블로 이관 스케줄러

***

## 6. 무결성 및 트랜잭션

- 모든 DML 작업은 Spring `@Transactional` 사용
- 낙관적 잠금(버전 컬럼)으로 동시성 충돌 방지

***

## 7. 백업 및 복구

- 일일 전체 덤프, 주간 테이블별 덤프
- WAL 기반 PITR 구성
- 정기 복구 테스트

***

## 8. 보안 및 권한

- 민감컬럼(AES-256) 암호화
- DB 사용자 최소 권한 부여
- 감사 로그(admin_logs)로 변경 이력 기록

***


<img src="https://r2cdn.perplexity.ai/pplx-full-logo-primary-dark%402x.png" style="height:64px;margin-right:32px"/>

# 맵핑도

아래는 주요 테이블과 Java 엔티티(ORM) 매핑 예시입니다. Spring Data JPA를 기준으로 작성했습니다.

***

## User 엔티티

```java
@Entity
@Table(name = "users")
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  private String riotPuuid;
  private String profileImageUrl;
  private String preferredPositions;
  private Integer userLevel = 1;
  private Integer experiencePoints = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role = Role.USER;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status = Status.ACTIVE;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  // OAuth 계정
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<OAuthAccount> oauthAccounts = new ArrayList<>();

  // 참가 기록
  @OneToMany(mappedBy = "user", cascade = CascadeType.SET_NULL)
  private List<GameParticipant> participants = new ArrayList<>();

  // 게시판 글
  @OneToMany(mappedBy = "user", cascade = CascadeType.SET_NULL)
  private List<BoardPost> posts = new ArrayList<>();

  // 댓글
  @OneToMany(mappedBy = "user", cascade = CascadeType.SET_NULL)
  private List<BoardComment> comments = new ArrayList<>();

  // 신고
  @OneToMany(mappedBy = "reporter", cascade = CascadeType.SET_NULL)
  private List<Report> reports = new ArrayList<>();
}
```


***

## GameRoom 엔티티

```java
@Entity
@Table(name = "game_rooms")
public class GameRoom {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 10)
  private String roomCode;

  private String title;
  private String description;
  private String gameMode;
  private Integer maxParticipants = 10;
  private Boolean isPrivate = false;
  private String password;

  @ManyToOne
  @JoinColumn(name = "host_user_id")
  private User host;

  @Enumerated(EnumType.STRING)
  private RoomStatus status = RoomStatus.WAITING;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
  private List<GameParticipant> participants = new ArrayList<>();

  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
  private List<GameRecord> records = new ArrayList<>();
}
```


***

## GameParticipant 엔티티

```java
@Entity
@Table(name = "game_participants")
public class GameParticipant {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "room_id", nullable = false)
  private GameRoom room;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private String position;
  private Integer team;

  @CreationTimestamp
  private LocalDateTime joinedAt;

  @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
  private List<GameRecord> records = new ArrayList<>();
}
```


***

## GameRecord 엔티티

```java
@Entity
@Table(name = "game_records")
public class GameRecord {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "room_id", nullable = false)
  private GameRoom room;

  @ManyToOne
  @JoinColumn(name = "participant_id", nullable = false)
  private GameParticipant participant;

  private String championName;
  private Integer kills = 0;
  private Integer deaths = 0;
  private Integer assists = 0;
  private Boolean isWinner;
  private Integer matchDuration;

  @CreationTimestamp
  private LocalDateTime createdAt;
}
```


***

## BoardCategory 엔티티

```java
@Entity
@Table(name = "board_categories")
public class BoardCategory {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;
  private Integer sortOrder = 0;
  private Boolean isActive = true;
  private String readPermission = "USER";
  private String writePermission = "USER";

  @CreationTimestamp
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  private List<BoardPost> posts = new ArrayList<>();
}
```


***

## BoardPost 엔티티

```java
@Entity
@Table(name = "board_posts")
public class BoardPost {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private BoardCategory category;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private String title;
  @Column(columnDefinition = "TEXT")
  private String content;
  private Integer viewCount = 0;
  private Integer likeCount = 0;
  private Integer dislikeCount = 0;
  private Integer commentCount = 0;
  private Boolean isPinned = false;
  private Boolean isHidden = false;
  private String tags;
  private InetAddress ipAddress;

  @CreationTimestamp
  private LocalDateTime createdAt;
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
  private List<BoardComment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
  private List<BoardAttachment> attachments = new ArrayList<>();
}
```


***

## BoardComment 엔티티

```java
@Entity
@Table(name = "board_comments")
public class BoardComment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private BoardPost post;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private BoardComment parent;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.CASCADE)
  private List<BoardComment> replies = new ArrayList<>();

  @Column(columnDefinition = "TEXT")
  private String content;
  private Integer likeCount = 0;
  private Boolean isHidden = false;
  private InetAddress ipAddress;

  @CreationTimestamp
  private LocalDateTime createdAt;
  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
```


***

## BoardAttachment 엔티티

```java
@Entity
@Table(name = "board_attachments")
public class BoardAttachment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private BoardPost post;

  private String originalName;
  private String storedName;
  private Long fileSize;
  private String mimeType;
  private Integer downloadCount = 0;

  @CreationTimestamp
  private LocalDateTime createdAt;
}
```


***

## Report 엔티티

```java
@Entity
@Table(name = "reports")
public class Report {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "reporter_id")
  private User reporter;

  private String targetType; // POST, COMMENT, USER
  private Long targetId;
  private String reportType; // SPAM, ABUSE, INAPPROPRIATE
  private String content;
  private String status = "PENDING";
  private String adminComment;

  @ManyToOne
  @JoinColumn(name = "processed_by")
  private User processedBy;

  private LocalDateTime processedAt;
  @CreationTimestamp
  private LocalDateTime createdAt;
}
```


***

## AdminLog 엔티티

```java
@Entity
@Table(name = "admin_logs")
public class AdminLog {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "admin_id")
  private User admin;

  private String action;
  private String targetType;
  private Long targetId;

  @Column(columnDefinition = "JSONB")
  private String details;

  private InetAddress ipAddress;

  @CreationTimestamp
  private LocalDateTime createdAt;
}
```


***

**위 매핑 예시를 참고하여 각 도메인 모델에 맞춰 Repository와 Service 계층을 구현하십시오.**



**작성자**: Nexus 개발팀
**최종 수정일**: 2025년 8월 19일

