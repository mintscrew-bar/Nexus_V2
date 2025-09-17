<img src="https://r2cdn.perplexity.ai/pplx-full-logo-primary-dark%402x.png" style="height:64px;margin-right:32px"/>

# Nexus 내전 플랫폼 기술 개발 가이드

## 목차

1. 개요
2. 개발 환경 설정
3. 아키텍처 및 모듈 구성
4. 데이터베이스 설계 및 마이그레이션
5. 백엔드 서비스 구현
6. 프론트엔드 애플리케이션 구현
7. 실시간 통신(채팅·상태 동기화)
8. 외부 API 연동(Riot Games)
9. 인증·인가 및 보안
10. 커뮤니티·게시판 시스템
11. 관리자 대시보드
12. 배포 및 CI/CD 파이프라인
13. 운영·모니터링 가이드
14. 테스트 전략
15. 장애 대응 및 백업

***

## 1. 개요

본 가이드는 Nexus 내전 플랫폼의 기술 스택, 아키텍처, 개발·배포 절차를 상세히 설명합니다. 각 단계별 설정과 코드 구조, 주요 모듈 작성법을 포함하여 개발팀이 일관된 품질로 서비스를 구현하도록 돕습니다.

***

## 2. 개발 환경 설정

1. 필수 도구
    - Java 17, Maven/Gradle
    - Node.js 18, npm 또는 yarn
    - Docker, Docker Compose
    - PostgreSQL 15, Redis 7
2. 로컬 개발 환경
    - `.env` 파일 생성

```
# 외부 API
RIOT_API_KEY=your_key

# 데이터베이스
DATABASE_URL=jdbc:postgresql://localhost:5432/nexus
REDIS_URL=redis://localhost:6379

# JWT 인증
JWT_SECRET=your_jwt_secret

# OAuth2 소셜 로그인
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
DISCORD_CLIENT_ID=your_discord_client_id
DISCORD_CLIENT_SECRET=your_discord_client_secret

# 이메일 (Gmail SMTP)
GMAIL_USERNAME=your_gmail@gmail.com
GMAIL_APP_PASSWORD=your_16_character_app_password
EMAIL_FROM=noreply@nexus.com
```

    - Docker Compose

```
docker-compose up -d
```

3. IDE 플러그인
    - Spring Boot, Lombok, ESLint, Prettier

***

## 3. 아키텍처 및 모듈 구성

```
Frontend (Next.js) ↔ Backend (Spring Boot) ↔ PostgreSQL/Redis
                                 ↓
                          Riot Games API
```

- `frontend/`: Next.js 페이지, Zustand 상태 관리, MUI 컴포넌트
- `backend/`: Spring Boot 애플리케이션
    - `auth/`, `games/`, `records/`, `boards/`, `admin/` 패키지 분리
- `common/`: 공유 모델, 유틸리티, 예외 처리
- `infra/`: Docker, CI/CD 스크립트, 모니터링 설정

### 로컬 인증 아키텍처 구성
```
                        [User]
                            ↓
        [Next.js/React] ←→ [Spring Boot API + JWT]
                    이메일/패스워드 인증   JWT 토큰 검증 및 API 접근제어
```

로컬 인증 서버 및 JWT 기반 인증 구조, 이메일 검증 시스템 포함

***

## 4. 데이터베이스 설계 및 마이그레이션

- Flyway 또는 Liquibase 사용 추천
- 주요 테이블
    - `users`, `game_rooms`, `game_participants`, `game_records`
    - `board_categories`, `board_posts`, `board_comments`
    - `reports`, `admin_logs`
- 샘플 Flyway 마이그레이션 파일

```sql
-- V1__Create_users.sql
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  ...
);
```


***

## 5. 백엔드 서비스 구현

1. Spring Boot 설정
    - `application.yml`에 DB·Redis·JWT 설정
2. 보안
    - Spring Security + JWT 필터
    - BCrypt 패스워드 암호화
    - Rate Limiting 및 보안 헤더 필터
3. REST API 컨트롤러
    - `@RestController` 분리(인증, 게임, 게시판, 관리자)
    - 글로벌 예외 처리 (`@ControllerAdvice`)
4. 서비스 레이어
    - 트랜잭션 관리(`@Transactional`)
    - 이메일 검증 서비스
    - JWT 토큰 관리자
5. 리포지토리
    - Spring Data JPA 인터페이스
6. 보안 기능
    - SecurityValidator: 입력 검증 및 XSS 방지
    - AuditService: 사용자 액션 로그 기록

***

## 6. 프론트엔드 애플리케이션 구현

1. Next.js 프로젝트 구조
    - App Router 기반 (`app/` 디렉토리)
    - `components/`, `stores/`, `services/` 구조
2. 로컬 인증 흐름
    - 이메일/패스워드 로그인 → JWT 토큰 저장 → 전역 상태 관리
    - 회원가입 시 이메일 검증 프로세스
    - 자동 로그인 상태 유지
3. UI 컴포넌트
    - Material-UI (MUI) 테마 커스터마이징
    - SCSS 모듈을 활용한 컴포넌트 스타일링
4. API 호출
    - Axios 인스턴스, 인터셉터로 JWT 헤더 자동 부착
    - 토큰 만료 시 자동 갱신 또는 로그아웃 처리
5. 상태 관리
    - Zustand를 활용한 인증 상태 관리
    - 사용자 정보 및 토큰 영구 저장

***
## 7. 실시간 통신(채팅·상태 동기화)

- STOMP over WebSocket 설정
- Spring

```java
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { ... }
```

- Frontend

```js
import { Client } from '@stomp/stompjs';
const client = new Client({ brokerURL: 'ws://localhost:8080/ws', ... });
```


***

## 8. 외부 API 연동(Riot Games)

- Riot API 키 관리(환경변수 + Redis 캐싱)
- 호출 서비스

```java
RestTemplate rest = new RestTemplate();
String url = "https://api.riotgames.com/lol/match/v4/matches/" + matchId + "?api_key=" + apiKey;
```

- 에러 핸들링, 레이트 리밋 대처 로직

***
## 9. 인증·인가 및 보안

- **하이브리드 인증 시스템**
  - 로컬 JWT 인증: 이메일/패스워드 기반 회원가입 및 로그인
  - OAuth2 소셜 로그인: Google, Discord 연동
  - Spring Security + JWT를 활용한 통합 인증 서버 구현
  - BCrypt 패스워드 해싱 (강도 12)으로 보안 강화

- **인증 플로우**
  1. 로컬 회원가입: 이메일 검증 → 패스워드 설정 → 계정 생성
  2. 로컬 로그인: 이메일/패스워드 검증 → JWT 토큰 발급 (7일 만료)
  3. OAuth2 로그인: 소셜 로그인 → 자동 계정 생성/로그인 → JWT 토큰 발급
  4. API 인증: Authorization Bearer 헤더로 JWT 토큰 검증
  5. 토큰 갱신: 리프레시 토큰 없이 재로그인 방식 채택

- **프론트엔드 연동**
  - 로컬 인증 API (`/api/auth/login`, `/api/auth/register`) 활용
  - OAuth2 인증: Spring Security OAuth2 Client 자동 리다이렉트 (`/oauth2/authorization/{provider}`)
  - 소셜 로그인 콜백 처리 (`/oauth/callback`)
  - Zustand 스토어로 JWT 토큰 및 사용자 정보 통합 관리
  - Axios 인터셉터로 API 요청 시 자동 토큰 부착

- **백엔드 보안**
  - Spring Security OAuth2 Resource Server로 JWT 검증
  - `SecurityConfig`에서 엔드포인트별 인증 정책 설정
  - 역할 기반 접근 제어 (`@PreAuthorize`) 적용
  - 보안 필터: Rate Limiting, Security Headers

- **추가 보안 설정**
  - CORS 설정으로 프론트엔드-백엔드 통신 제한
  - CSRF 비활성화 (JWT 토큰 방식이므로 불필요)
  - 입력 검증 및 XSS 방지 (`SecurityValidator`)
  - 사용자 액션 로깅 (`AuditService`)

***

## 10. 커뮤니티·게시판 시스템

- 카테고리, 게시글, 댓글 서비스
- WYSIWYG 에디터 통합 (React-Quill)
- 파일 업로드: S3 또는 서버 디스크, 용량·타입 검사
- 검색 인덱싱: PostgreSQL full-text search

***

## 11. 관리자 대시보드

- 통계 조회 서비스(DB 집계 쿼리 최적화)
- Spring MVC 기반 관리자용 UI(Thymeleaf 또는 React Admin)
- 로그 테이블 연동, 액션 기록

***

## 12. 배포 및 CI/CD 파이프라인

- GitHub Actions
    - 빌드 → 테스트 → Docker 이미지 빌드 → 레지스트리 푸시
- 서버: Docker Compose, 스왑 업데이트(무중단 배포)
- SSL 인증서 자동 갱신(Certbot)

***

## 13. 운영·모니터링 가이드

- Prometheus 메트릭 노출(Actuator + Micrometer)
- Grafana 대시보드: JVM, HTTP 응답, DB 커넥션
- Sentry 연동: 예외 모니터링
- 로컬 인증 시스템 모니터링: 로그인 실패율, 회원가입 통계
- 보안 이벤트 모니터링: Rate Limiting, 비정상 접근 탐지
- JWT 토큰 관련 메트릭: 토큰 만료, 검증 실패 등
- 이메일 검증 시스템 모니터링 및 장애 대응

***

## 14. 테스트 전략

- 단위 테스트: JUnit, Mockito
- 통합 테스트: SpringBootTest, Testcontainers
- E2E 테스트: Cypress로 UI 흐름 검증

***

## 15. 장애 대응 및 백업

- 자동화된 DB 백업 스크립트(일일 dump)
- 장애 시 롤백 플레이북(버전별 Docker Compose)
- 로그 수집(ELK 스택)로 원인 분석

***

**이 가이드를 기반으로 Nexus 플랫폼을 구현·운영하십시오.**

