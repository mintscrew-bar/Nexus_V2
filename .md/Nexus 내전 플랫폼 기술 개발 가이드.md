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
RIOT_API_KEY=your_key
DATABASE_URL=jdbc:postgresql://localhost:5432/nexus
REDIS_URL=redis://localhost:6379
JWT_SECRET=your_jwt_secret
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
-   아키텍처 및 모듈 구성 (보완)
    text
                        [User]
                            ↓
        [Next.js/React] ←→ [Keycloak] ←→ [Spring Boot API]
                    소셜로그인/OAuth   인증 검증 및 API 접근제어
    인증 서버(Keycloak) 및 소셜 로그인 외부 인증 구조, API 키 관리 보강

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
    - `application.yml`에 DB·Redis 설정
2. 보안
    - Spring Security + JWT 필터
3. REST API 컨트롤러
    - `@RestController` 분리(인증, 게임, 게시판, 관리자)
4. 서비스 레이어
    - 트랜잭션 관리(`@Transactional`)
5. 리포지토리
    - Spring Data JPA 인터페이스

***

## 6. 프론트엔드 애플리케이션 구현

1. Next.js 프로젝트 구조
    - `pages/`, `components/`, `stores/`, `services/`
2. 인증 흐름
    - OAuth 연동 팝업 → 토큰 저장 → 전역 상태 관리
3. UI 컴포넌트
    - MUI 테마 커스터마이징
4. API 호출
    - Axios 인스턴스, 인터셉터로 JWT 헤더 부착

5. Keycloak JS SDK 또는 커스텀 OIDC 인증 모듈 사용

    - Google/Discord 소셜 버튼 제공 → Keycloak broker 엔드포인트로 리디렉션

    - 로그인 성공 후 JWT를 글로벌 상태(Zustand 등)에 저장, API 요청시 헤더에 포함

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

- **Keycloak 기반 통합 인증/권한 관리 도입**
  - Keycloak은 OAuth2, OpenID Connect를 지원하는 오픈소스 인증 서버
  - 자체 계정 인증과 함께 Google, Discord 등의 소셜 로그인 연동
  - 관리자 콘솔에서 Realm과 Client를 생성 및 설정하여 인증 정책 관리

- **소셜 로그인 설정 및 플로우**
  1. Keycloak 관리자 콘솔에서 Identity Providers에 Google, Discord 등록
  2. 각 소셜 로그인 서비스에서 클라이언트 ID 및 클라이언트 시크릿을 발급 받아 설정
  3. 프론트엔드 로그인 시 소셜 로그인 버튼 클릭 → Keycloak OAuth 엔드포인트로 리다이렉션
  4. 사용자가 소셜 서비스 인증 완료 후 Keycloak이 콜백 처리
  5. 액세스 토큰(JWT)과 사용자 프로필 정보(이메일, 닉네임 등) 획득 → 프론트엔드 및 백엔드 인증

- **프론트엔드 연동**
  - Keycloak JS SDK 혹은 OIDC 클라이언트 라이브러리를 사용하여 로그인, 토큰 관리 구현
  - 성공적인 인증 후 받은 JWT 토큰을 전역 상태에 저장하고 API 호출 시 Authorization 헤더에 설정

- **백엔드 연동**
  - Spring Boot 환경에서는 Keycloak Spring Boot Starter 모듈을 사용해 JWT 토큰 검증 및 권한 관리를 수행
  - `application.yml`에 Keycloak 서버 URL과 Realm 정보를 설정하여 보호된 API 구현
  - 역할 기반 접근 제어(`@PreAuthorize`) 적용

- **기타 보안 설정**
  - CORS, CSRF, Rate Limiting은 기존 방식 유지
  - Keycloak을 통한 2차 인증(MFA), 계정 잠금, 비밀번호 정책 등 확장 가능

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
- Keycloak Realm 별 세부 정책 설정/로그/계정관리 및 장애 시 복구 방법 추가
- 신속하게 신규 인증(예: Kakao, Github) 추가·해제 가능

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

