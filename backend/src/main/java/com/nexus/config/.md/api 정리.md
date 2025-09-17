# Nexus 플랫폼 API 정리 업데이트

아래는 최신 아키텍처를 반영한 하이브리드 인증 시스템(로컬 JWT + OAuth2 소셜 로그인)이 포함된 Nexus 플랫폼의 전체 API 목록입니다.

---

## 핵심 API 엔드포인트 테이블

| 분류         | 메서드   | 엔드포인트                                 | 설명                                                                                          |
|--------------|----------|--------------------------------------------|-----------------------------------------------------------------------------------------------|
| 인증         | POST     | /api/auth/register                         | 사용자 회원가입 (이메일/패스워드/닉네임/롤태그)                                                |
| 인증         | POST     | /api/auth/login                            | 로그인 (이메일/패스워드) → JWT 토큰 발급                                                      |
| 인증         | POST     | /api/auth/logout                           | 로그아웃 처리                                                                                  |
| 인증         | POST     | /api/auth/email/code                       | 이메일 인증코드 발송                                                                           |
| 인증         | POST     | /api/auth/email/verify                     | 이메일 인증코드 확인                                                                           |
| 인증         | GET      | /api/auth/profile                          | 현재 사용자 정보 조회                                                                          |
| 인증         | PUT      | /api/auth/profile                          | 사용자 프로필 정보 수정                                                                       |
| 인증         | GET      | /api/auth/check/nickname                   | 닉네임 중복 확인                                                                               |
| 인증         | GET      | /api/auth/check/loltag                     | LoL 태그 검증                                                                                  |
| OAuth2       | GET      | /oauth2/authorization/google               | Google OAuth2 로그인 리다이렉트                                                               |
| OAuth2       | GET      | /oauth2/authorization/discord              | Discord OAuth2 로그인 리다이렉트                                                              |
| OAuth2       | GET      | /api/auth/oauth2/callback/google           | Google OAuth2 콜백 처리                                                                        |
| OAuth2       | GET      | /api/auth/oauth2/callback/discord          | Discord OAuth2 콜백 처리                                                                       |
| OAuth2       | GET      | /api/auth/oauth2/user                      | 현재 OAuth2 사용자 정보 조회                                                                   |
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

## API 사용 가이드

### 인증 방식
- **로컬 인증**: 이메일/패스워드 로그인 → JWT 토큰 발급
- **OAuth2 인증**: Google/Discord 소셜 로그인 → 자동 계정 생성 → JWT 토큰 발급
- **API 인증**: JWT Bearer 토큰 기반 (Authorization: Bearer {token})

### 토큰 및 보안
- **토큰 만료**: 7일 (재로그인 필요)
- **보안 기능**: Rate Limiting, XSS 방지, 입력 검증, 감사 로깅
- **에러 응답**: 표준화된 ApiResponse 형식

### OAuth2 플로우
1. 프론트엔드에서 `/oauth2/authorization/{provider}` 호출
2. Spring Security가 자동으로 OAuth2 제공자로 리다이렉트
3. 사용자 인증 완료 후 `/api/auth/oauth2/callback/{provider}`로 콜백
4. JWT 토큰과 함께 프론트엔드 `/oauth/callback`으로 리다이렉트

### 이메일 기능
- **Gmail SMTP**: 이메일 인증 코드 및 비밀번호 재설정 이메일 발송
- **HTML 템플릿**: 브랜딩된 이메일 디자인 제공

관리자/운영용 API는 역할(Role) 및 권한(Permission) 검증이 추가 적용됩니다.
API 요청 및 응답에 대한 세부 스펙은 별도 문서 또는 Swagger/OpenAPI 문서를 참고하세요.

