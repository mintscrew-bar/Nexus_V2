# Nexus 플랫폼 API 정리 업데이트

아래는 최신 아키텍처를 반영한 Keycloak 기반 인증 및 구글·디스코드 소셜 로그인 연동이 포함된 Nexus 플랫폼의 전체 API 목록입니다.

---

## 핵심 API 엔드포인트 테이블

| 분류         | 메서드   | 엔드포인트                                 | 설명                                                                                          |
|--------------|----------|--------------------------------------------|-----------------------------------------------------------------------------------------------|
| 인증         | POST     | /api/auth/keycloak/login                   | Keycloak 인증(소셜로그인 포함) 리다이렉션                                                      |
| 인증         | GET      | /api/auth/keycloak/callback                | Keycloak 인증 콜백 처리, JWT 발급 및 사용자 정보 반환                                           |
| 인증         | POST     | /api/auth/logout                           | 로그아웃 (Keycloak 세션 및 토큰 만료 처리)                                                     |
| 인증         | GET      | /api/auth/profile                          | 현재 사용자 정보 조회, Keycloak 토큰 기반                                                      |
| 인증         | PUT      | /api/auth/profile                          | 사용자 프로필 정보 수정                                                                       |
| 인증         | POST     | /api/auth/refresh                          | 액세스 토큰 갱신                                                                               |
| 인증(소셜)   | GET      | /api/auth/keycloak/google                  | 구글 소셜로그인 리다이렉트(자동 핸들링)                                                        |
| 인증(소셜)   | GET      | /api/auth/keycloak/discord                 | 디스코드 소셜로그인 리다이렉트(자동 핸들링)                                                    |
| 친구 관리    | GET      | /api/friends                               | 친구 목록 조회                                                                                 |
| 친구 관리    | POST     | /api/friends/{username}                    | 친구 요청                                                                                      |
| 친구 관리    | PUT      | /api/friends/{friendId}/accept             | 친구 요청 수락                                                                                 |
| 친구 관리    | DELETE   | /api/friends/{friendId}                    | 친구 삭제                                                                                      |
| 알림         | GET      | /api/notifications                         | 내 알림 목록 조회                                                                              |
| 알림         | PUT      | /api/notifications/{notificationId}/read   | 알림 읽음 처리                                                                                 |
| 알림         | DELETE   | /api/notifications/{notificationId}        | 알림 삭제                                                                                      |
| 게임 관리    | GET      | /api/games                                 | 게임 방 목록 조회                                                                              |
| 게임 관리    | POST     | /api/games                                 | 게임 방 생성                                                                                   |
| 게임 관리    | GET      | /api/games/{roomCode}                      | 게임 방 상세 조회                                                                              |
| 게임 관리    | POST     | /api/games/{roomCode}/join                 | 게임 방 참가                                                                                   |
| 게임 관리    | DELETE   | /api/games/{roomCode}/leave                | 게임 방 나가기                                                                                 |
| 게임 관리    | POST     | /api/games/{roomCode}/start                | 내전 시작                                                                                      |
| 게임 관리    | POST     | /api/games/{roomCode}/invite               | 사용자 초대                                                                                    |
| 자동 매칭    | POST     | /api/matchmaking/auto                      | 자동 매칭 요청 (티어·포지션 기반)                                                              |
| 자동 매칭    | DELETE   | /api/matchmaking/auto                      | 자동 매칭 취소                                                                                 |
| 경매         | GET      | /api/games/{roomCode}/auction              | 진행 중인 경매 상태 조회                                                                       |
| 경매         | POST     | /api/games/{roomCode}/auction/bid          | 경매 입찰                                                                                      |
| 전적         | GET      | /api/records/user/{userId}                 | 사용자 게임 전적 조회                                                                          |
| 전적         | GET      | /api/records/leaderboard                   | 리더보드(랭킹, 통계) 조회                                                                      |
| 전적         | GET      | /api/records/champion-stats                | 챔피언별 통계 조회                                                                             |
| 전적         | POST     | /api/records/game-result                   | 게임 결과 직접 등록                                                                            |
| 점수         | GET      | /api/users/{userId}/stats                  | 티어·포지션 점수 상세 조회                                                                     |
| 커뮤니티     | GET      | /api/boards/categories                     | 게시판 카테고리 목록                                                                           |
| 커뮤니티     | GET      | /api/boards/{categoryId}/posts             | 게시글 목록 조회 (필터/검색/페이징 지원)                                                       |
| 커뮤니티     | POST     | /api/boards/{categoryId}/posts             | 게시글 작성                                                                                    |
| 커뮤니티     | GET      | /api/boards/posts/{postId}                 | 게시글 상세 및 댓글 요약                                                                       |
| 커뮤니티     | PUT      | /api/boards/posts/{postId}                 | 게시글 수정                                                                                    |
| 커뮤니티     | DELETE   | /api/boards/posts/{postId}                 | 게시글 삭제                                                                                    |
| 커뮤니티     | POST     | /api/boards/posts/{postId}/like            | 좋아요/싫어요                                                                                  |
| 댓글         | GET      | /api/boards/posts/{postId}/comments        | 댓글 목록 조회                                                                                 |
| 댓글         | POST     | /api/boards/posts/{postId}/comments        | 댓글 작성                                                                                      |
| 댓글         | DELETE   | /api/boards/comments/{commentId}           | 댓글 삭제                                                                                      |
| 파일업로드   | POST     | /api/uploads/images                        | 이미지 업로드 (프로필·게시판)                                                                  |
| 파일업로드   | POST     | /api/uploads/files                         | 기타 문서/동영상 업로드                                                                        |
| 신고 관리    | POST     | /api/reports                               | 신고 접수                                                                                      |
| 신고 관리    | GET      | /api/reports/my                            | 내 신고 이력 조회                                                                              |
| 관리자      | GET      | /api/admin/dashboard                        | 운영 대시보드 통계                                                                             |
| 관리자      | GET      | /api/admin/users                            | 사용자 목록 조회                                                                               |
| 관리자      | PUT      | /api/admin/users/{userId}/status            | 사용자 상태(권한/정지 등) 변경                                                                 |
| 관리자      | GET      | /api/admin/posts                            | 전체 게시글 관리                                                                               |
| 관리자      | PUT      | /api/admin/posts/{postId}/status            | 게시글 상태 변경                                                                               |
| 관리자      | GET      | /api/admin/reports                          | 신고 목록 조회                                                                                 |
| 관리자      | PUT      | /api/admin/reports/{reportId}               | 신고 처리                                                                                      |
| 관리자      | GET      | /api/admin/logs                             | 관리자 로그 조회                                                                               |
| 헬스체크     | GET      | /api/health                                 | 서비스 상태 확인(DB, Redis, API)                                                               |
| 통계         | GET      | /api/metrics/usage                          | 플랫폼 주요 지표(사용량·매칭·방 생성 등) 통계                                                  |
| 웹훅         | POST     | /api/webhooks/game-start                    | 게임 시작 외부 알림                                                                            |
| 웹훅         | POST     | /api/webhooks/game-end                      | 게임 종료 외부 알림                                                                            |
| 실시간 통신  | -        | /ws                                        | WebSocket 접속Endpoint                                                                         |
| 실시간 통신  | -        | /topic/games/{roomCode}/chat                | 게임 방 채팅 구독                                                                              |
| 실시간 통신  | -        | /topic/games/{roomCode}/updates             | 게임 방 상태/매칭 실시간 구독                                                                  |
| 실시간 통신  | -        | /app/games/{roomCode}/chat.send             | 채팅 메시지 발송                                                                               |
| 실시간 통신  | -        | /app/games/{roomCode}/updates.send          | 게임 방/경기 상태 변경 발송                                                                    |


---

- 모든 엔드포인트는 Keycloak Bearer 토큰 기반 인증을 요구합니다.
- 구글, 디스코드 등 소셜 로그인은 Keycloak OpenID Connect 프로토콜로 처리됩니다.
- 관리자/운영용 API는 역할(Role) 및 권한(Permission) 검증이 추가 적용됩니다.
- API 요청 및 응답에 대한 세부 스펙(파라미터, 응답 예시 등)은 별도 문서 또는 Swagger/OpenAPI 문서를 참고하세요.

