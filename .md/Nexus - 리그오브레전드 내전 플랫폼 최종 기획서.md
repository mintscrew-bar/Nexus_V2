<img src="https://r2cdn.perplexity.ai/pplx-full-logo-primary-dark%402x.png" style="height:64px;margin-right:32px"/>

# Nexus - 리그오브레전드 내전 플랫폼 최종 기획서

**프로젝트명**: Nexus
**버전**: 1.0
**작성일**: 2025년 8월
**대상**: 투자자, 개발팀, 운영팀

***

## 📋 목차

1. [프로젝트 개요](#1-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B0%9C%EC%9A%94)
2. [시장 분석](#2-%EC%8B%9C%EC%9E%A5-%EB%B6%84%EC%84%9D)
3. [UI/UX 설계](#3-uiux-%EC%84%A4%EA%B3%84)
4. [핵심 기능](#4-%ED%95%B5%EC%8B%AC-%EA%B8%B0%EB%8A%A5)
5. [기술 스택](#5-%EA%B8%B0%EC%88%A0-%EC%8A%A4%ED%83%9D)
6. [시스템 아키텍처](#6-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98)
7. [데이터베이스 설계](#7-%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4-%EC%84%A4%EA%B3%84)
8. [API 설계](#8-api-%EC%84%A4%EA%B3%84)
9. [보안 설계](#9-%EB%B3%B4%EC%95%88-%EC%84%A4%EA%B3%84)
10. [배포 전략](#10-%EB%B0%B0%ED%8F%AC-%EC%A0%84%EB%9E%B5)
11. [개발 로드맵](#11-%EA%B0%9C%EB%B0%9C-%EB%A1%9C%EB%93%9C%EB%A7%B5)
12. [수익 모델](#12-%EC%88%98%EC%9D%B5-%EB%AA%A8%EB%8D%B8)
13. [예산 및 비용](#13-%EC%98%88%EC%82%B0-%EB%B0%8F-%EB%B9%84%EC%9A%A9)
14. [위험 요소 및 대응책](#14-%EC%9C%84%ED%97%98-%EC%9A%94%EC%86%8C-%EB%B0%8F-%EB%8C%80%EC%9D%91%EC%B1%85)
15. [성공 지표](#15-%EC%84%B1%EA%B3%B5-%EC%A7%80%ED%91%9C)

***

## 1. 프로젝트 개요

### 1.1 서비스 컨셉

**Nexus**는 리그오브레전드 사용자들이 쉽고 편리하게 내전(커스텀 게임)을 생성하고 참가할 수 있는 통합 플랫폼입니다. Discord의 직관적인 네비게이션과 OP.GG의 게임 특화 UI를 결합하여, 게이머들에게 최적화된 사용자 경험을 제공합니다.

### 1.2 핵심 가치 제안

- **간편한 내전 생성 및 참가**: 클릭 몇 번으로 게임 생성/참가
- **전적 통합 관리**: Riot API 연동으로 실시간 전적 추적
- **커뮤니티 중심**: 게이머들 간 소통과 정보 공유
- **무료 서비스**: 모든 핵심 기능을 비용 부담 없이 제공


### 1.3 목표 시장

- **1차 타겟**: 국내 리그오브레전드 활성 유저 (약 300만명)
- **2차 타겷**: 국내 e스포츠 관심층 및 게임 커뮤니티 (약 500만명)
- **3차 타겟**: 해외 리그오브레전드 유저 (글로벌 확장)

***

## 2. 시장 분석

### 2.1 시장 규모

- **국내 PC게임 시장**: 약 4조원 (2024년 기준)
- **리그오브레전드 국내 MAU**: 약 300만명
- **e스포츠 시장 성장률**: 연평균 15% 증가


### 2.2 경쟁사 분석

| 서비스 | 장점 | 단점 | 차별화 포인트 |
| :-- | :-- | :-- | :-- |
| OP.GG | 상세한 전적 분석 | 내전 기능 부재 | 내전 전용 플랫폼 |
| 인벤 | 강력한 커뮤니티 | 복잡한 UI | 직관적 Discord 스타일 |
| 롤체지지 | 챔피언 정보 풍부 | 실시간 매칭 부족 | 실시간 포지션 매칭 |

### 2.3 시장 기회

- 기존 서비스들이 전적 조회나 정보 제공에 집중
- 내전 생성/관리에 특화된 플랫폼 부재
- 커뮤니티와 게임을 연결하는 통합 서비스 필요성 증가

***

## 3. UI/UX 설계

### 3.1 전체 레이아웃 구조

```
┌─────────────┬──────────────────────────────────────┐
│             │                                      │
│   사이드바   │           메인 콘텐츠 영역            │
│  네비게이션  │                                      │
│             │                                      │
│   - Nexus   │  ┌─────────────────────────────────┐  │
│   - 내전    │  │          검색창 (고정)          │  │
│   - 커뮤니티 │  ├─────────────────────────────────┤  │
│   - 분석    │  │                                 │  │
│   - 패치노트 │  │        페이지별 콘텐츠          │  │
│   - 클랜    │  │                                 │  │
│             │  │                                 │  │
│   --------  │  └─────────────────────────────────┘  │
│   - 다크모드 │                                      │
│   - 친구    │                                      │
│   - 프로필  │                                      │
│   - 설정    │                                      │
└─────────────┴──────────────────────────────────────┘
```


### 3.2 홈 화면 구성

**OP.GG Desktop 스타일 대시보드**

```
┌────────────────────────────────────────────────────────────┐
│                   전적 검색창 (전체 너비)                │
├──────┬─────────────┬───────────────┬────────┬────────────┤
│ 패치 │    내전     │   진행중인    │커뮤니티│ 리더보드   │
│ 노트 │ 생성/관리   │   내전 목록   │ 탭     │ 위젯       │
│ 카드 │   카드      │               │        │            │
└──────┴─────────────┴───────────────┴────────┴────────────┘
```


### 3.3 내전 페이지 구성

**상단 액션 바**

- 검색창: 방 제목, 호스트명 실시간 검색
- 빠른 참가 버튼: 포지션 매칭 모달
- 내전 생성 버튼: 상세 설정 모달

**방 리스트 테이블**


| 상태 | 방 제목 | 모드 | 인원 | 호스트 | 액션 |
| :-- | :-- | :-- | :-- | :-- | :-- |
| 🟢 모집중 | 롤드컵 예선전 | 5v5 | 8/10 | 프로게이머A | 참가 |
| 🟡 대기중 | 칼바람 내전 | ARAM | 4/10 | 유저B | 참가 |

### 3.4 포지션 선택 시스템 (롤 클라이언트 스타일)

```
주 포지션 선택        부 포지션 선택
┌─────────────┐      ┌─────────────┐
│     탑      │      │   (선택됨)   │
│   [아이콘]  │      │             │
└─────────────┘      └─────────────┘

포지션 선택창 (버튼 클릭시 표시)
┌───┬───┬───┬───┬───┐
│탑 │정글│미드│원딜│서폿│
└───┴───┴───┴───┴───┘
```


***

## 4. 핵심 기능

### 4.1 내전 관리 시스템

- **내전 생성**: 게임 모드, 인원수, 공개/비공개 설정
- **빠른 참가**: 포지션 기반 자동 매칭
- **실시간 업데이트**: WebSocket 기반 실시간 상태 동기화
- **방 관리**: 호스트 권한, 참가자 관리, 게임 시작


### 4.2 전적 관리 시스템

- **Riot API 연동**: 실시간 전적 조회
- **내전 전적**: 플랫폼 내 게임 기록 관리
- **통계 분석**: 승률, 포지션별 성과, 챔피언 분석
- **리더보드**: 실시간 랭킹 시스템


### 4.3 커뮤니티 기능

- **게시판**: 자유게시판, 팀 모집, 공략 공유
- **실시간 채팅**: 게임룸별 채팅, 전체 채팅
- **알림 시스템**: 게임 시작, 댓글, 친구 요청 알림


### 4.4 사용자 관리

- **OAuth 로그인**: Google, Discord, Riot 계정 연동
- **프로필 관리**: 아바타, 선호 포지션, 전적 공개 설정
- **친구 시스템**: 친구 추가, 온라인 상태, 게임 초대

***

## 5. 기술 스택

### 5.1 백엔드

- **언어**: Java 17
- **프레임워크**: Spring Boot 3.2, Spring Security, Spring Data JPA
- **데이터베이스**: PostgreSQL 15 (주 DB), Redis 7 (캐시/세션)
- **실시간 통신**: WebSocket + STOMP
- **외부 API**: Riot Games API, Tournament API


### 5.2 프론트엔드

- **언어**: TypeScript
- **프레임워크**: React 18, Next.js 14
- **상태 관리**: Zustand
- **UI 라이브러리**: Material-UI (MUI)
- **HTTP 클라이언트**: Axios
- **실시간 통신**: SockJS + STOMP


### 5.3 개발 도구

- **빌드 도구**: Gradle (백엔드), npm/yarn (프론트엔드)
- **코드 품질**: ESLint, Prettier, Checkstyle, SpotBugs
- **테스팅**: JUnit 5, Jest, React Testing Library
- **API 문서**: Swagger/OpenAPI 3


### 5.4 인프라

- **컨테이너**: Docker, Docker Compose
- **웹 서버**: Nginx (리버스 프록시)
- **SSL**: Let's Encrypt (무료 인증서)
- **모니터링**: Grafana, Prometheus

***

## 6. 시스템 아키텍처

### 6.1 전체 아키텍처

```
[인터넷] → [공유기/방화벽] → [개인 컴퓨터 서버]

개인 컴퓨터 서버 구성:
┌─────────────────────────────────────────┐
│                Nginx                    │ ← HTTPS/리버스 프록시
├─────────────────┬───────────────────────┤
│   Next.js       │   Spring Boot         │ ← 애플리케이션 계층
│  (Frontend)     │   (Backend API)       │
├─────────────────┴───────────────────────┤
│           PostgreSQL + Redis            │ ← 데이터 계층
└─────────────────────────────────────────┘
```


### 6.2 네트워크 구성

- **도메인**: nexus.duckdns.org (DDNS 무료 서비스)
- **포트**: 80(HTTP→HTTPS 리다이렉트), 443(HTTPS)
- **SSL**: Let's Encrypt 자동 갱신 인증서


### 6.3 데이터 흐름

```
사용자 → Nginx → Next.js (SSR) → Spring Boot API → PostgreSQL/Redis
                                      ↓
                              Riot Games API
```


***

## 7. 데이터베이스 설계

### 7.1 주요 테이블 구조

```sql
-- 사용자 테이블
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    riot_puuid VARCHAR(100),
    profile_image_url VARCHAR(500),
    preferred_positions VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- OAuth 연동 테이블
CREATE TABLE oauth_accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    provider VARCHAR(20) NOT NULL, -- 'google', 'discord', 'riot'
    provider_id VARCHAR(100) NOT NULL,
    access_token TEXT,
    refresh_token TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 게임 룸 테이블
CREATE TABLE game_rooms (
    id BIGSERIAL PRIMARY KEY,
    room_code VARCHAR(10) UNIQUE NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    game_mode VARCHAR(20) NOT NULL, -- '5v5', 'ARAM', 'URF'
    max_participants INTEGER DEFAULT 10,
    is_private BOOLEAN DEFAULT FALSE,
    password VARCHAR(100),
    host_user_id BIGINT REFERENCES users(id),
    status VARCHAR(20) DEFAULT 'WAITING', -- 'WAITING', 'IN_PROGRESS', 'COMPLETED'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 게임 참가자 테이블
CREATE TABLE game_participants (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT REFERENCES game_rooms(id),
    user_id BIGINT REFERENCES users(id),
    position VARCHAR(20), -- 'TOP', 'JUNGLE', 'MID', 'ADC', 'SUPPORT'
    team INTEGER, -- 1 또는 2
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 게임 기록 테이블
CREATE TABLE game_records (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT REFERENCES game_rooms(id),
    participant_id BIGINT REFERENCES game_participants(id),
    champion_name VARCHAR(50),
    kills INTEGER DEFAULT 0,
    deaths INTEGER DEFAULT 0,
    assists INTEGER DEFAULT 0,
    is_winner BOOLEAN,
    match_duration INTEGER, -- 게임 시간(초)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```


### 7.2 인덱스 설계

```sql
-- 성능 최적화를 위한 인덱스
CREATE INDEX idx_users_riot_puuid ON users(riot_puuid);
CREATE INDEX idx_game_rooms_status ON game_rooms(status);
CREATE INDEX idx_game_rooms_created_at ON game_rooms(created_at DESC);
CREATE INDEX idx_game_participants_room_user ON game_participants(room_id, user_id);
```


***

## 8. API 설계

### 8.1 인증 API

```
POST /api/auth/oauth/{provider}     # OAuth 로그인
POST /api/auth/refresh              # 토큰 갱신
POST /api/auth/logout               # 로그아웃
GET  /api/auth/profile              # 내 프로필 조회
PUT  /api/auth/profile              # 프로필 수정
```


### 8.2 게임 관리 API

```
GET    /api/games                   # 게임 목록 조회
POST   /api/games                   # 게임 생성
GET    /api/games/{roomCode}        # 특정 게임 조회
POST   /api/games/{roomCode}/join   # 게임 참가
DELETE /api/games/{roomCode}/leave  # 게임 나가기
POST   /api/games/{roomCode}/start  # 게임 시작
```


### 8.3 전적 관리 API

```
GET /api/records/user/{userId}      # 사용자 전적 조회
GET /api/records/leaderboard        # 리더보드
GET /api/records/champion-stats     # 챔피언 통계
POST /api/records/game-result       # 게임 결과 등록
```


### 8.4 WebSocket 채널

```
/app/games/{roomCode}/chat          # 게임룸 채팅
/app/games/{roomCode}/updates       # 게임 상태 업데이트
/app/notifications                  # 전체 알림
```


***

## 9. 보안 설계

### 9.1 네트워크 보안

- **방화벽**: UFW로 80, 443 포트만 외부 접근 허용
- **SSL/TLS**: Let's Encrypt HTTPS 강제, TLS 1.2+ 적용
- **포트 포워딩**: 공유기에서 최소 포트만 개방


### 9.2 애플리케이션 보안

- **인증**: OAuth2 + JWT (액세스 토큰 1시간, 리프레시 토큰 30일)
- **권한 관리**: Spring Security로 API별 권한 검증
- **입력 검증**: @Valid 어노테이션, SQL Injection 방지
- **XSS 방지**: React 기본 이스케이프, CSP 헤더 적용
- **Rate Limiting**: IP별 100회/분 제한


### 9.3 데이터 보안

- **민감정보 암호화**: AES-256으로 개인정보 암호화
- **환경변수**: Docker Secrets로 API 키, DB 패스워드 관리
- **백업 암호화**: 정기 백업 파일 암호화 저장


### 9.4 시스템 보안

- **컨테이너**: 비루트 사용자로 실행, 최소 권한 적용
- **모니터링**: 실패한 로그인, 권한 오류 실시간 감지
- **업데이트**: 월 1회 보안 패치 적용

***

## 10. 배포 전략

### 10.1 개인 컴퓨터 서버 구성

**하드웨어 요구사항**

- CPU: 4코어 이상
- RAM: 16GB 이상
- 저장공간: SSD 500GB 이상
- 네트워크: 업로드 10Mbps 이상

**소프트웨어 스택**

```yaml
# docker-compose.yml
version: '3.9'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: nexus
      POSTGRES_USER: nexus
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    restart: unless-stopped

  backend:
    build: ./backend
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/nexus
      - RIOT_API_KEY=${RIOT_API_KEY}
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      - postgres
      - redis
    restart: unless-stopped

  frontend:
    build: ./frontend
    environment:
      - NEXT_PUBLIC_API_URL=https://nexus.duckdns.org/api
    depends_on:
      - backend
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      - frontend
      - backend
    restart: unless-stopped
```


### 10.2 배포 자동화

```bash
#!/bin/bash
# deploy.sh
echo "🚀 Nexus 배포 시작..."
git pull origin main
docker-compose build
docker-compose down
docker-compose up -d
echo "✅ 배포 완료!"
```


### 10.3 모니터링 및 백업

- **일일 모니터링**: 시스템 리소스, 서비스 상태
- **주간 백업**: PostgreSQL 덤프, 환경변수 백업
- **월간 점검**: 보안 업데이트, 성능 최적화

***

## 11. 개발 로드맵

### Phase 1: 기반 구축 (1-2주)

- [x] 개발 환경 설정 (Docker, DB, 기본 프로젝트)
- [x] 사용자 인증 시스템 (OAuth + JWT)
- [ ] 기본 UI 레이아웃 (사이드바, 라우팅)
- [ ] 데이터베이스 스키마 구현


### Phase 2: 내전 시스템 (3-4주)

- [ ] 게임 룸 생성/관리 API
- [ ] 포지션 선택 UI (롤 클라이언트 스타일)
- [ ] 빠른 참가 매칭 시스템
- [ ] 실시간 게임 상태 업데이트 (WebSocket)


### Phase 3: 전적 시스템 (5-6주)

- [ ] Riot API 연동
- [ ] 개인 전적 조회 및 분석
- [ ] 리더보드 시스템
- [ ] 챔피언 통계 및 분석


### Phase 4: 커뮤니티 기능 (7주)

- [ ] 게시판 시스템
- [ ] 실시간 채팅
- [ ] 알림 시스템
- [ ] 친구 관리


### Phase 5: 최적화 및 배포 (8주)

- [ ] 성능 최적화
- [ ] 보안 강화
- [ ] 테스트 및 버그 수정
- [ ] 실제 서버 배포

***




### 13.4 ROI 분석 (2년차 기준)

- **월 수익**: 400만원
- **월 비용**: 108만원 (운영비 + 인건비)
- **월 순이익**: 292만원
- **연 순이익**: 3,504만원

***

## 14. 위험 요소 및 대응책

### 14.1 기술적 위험

**위험**: 개인 서버 장애, 하드웨어 고장

- **대응**: 정기 백업, 클라우드 마이그레이션 준비

**위험**: Riot API 정책 변경, 사용량 제한

- **대응**: API 사용량 모니터링, 대안 데이터 소스 확보

**위험**: 보안 취약점, 개인정보 유출

- **대응**: 정기 보안 점검, 암호화 강화, 침입 탐지 시스템


### 14.2 사업적 위험

**위험**: 경쟁사 등장, 시장 포화

- **대응**: 지속적 기능 개선, 사용자 피드백 반영

**위험**: 사용자 확보 어려움

- **대응**: 커뮤니티 마케팅, 인플루언서 협력

**위험**: 수익화 실패

- **대응**: 다양한 수익 모델 실험, B2B 전환


### 14.3 운영적 위험

**위험**: 개발자 이탈, 인력 부족

- **대응**: 문서화 철저, 외주 파트너 확보

**위험**: 법적 이슈, 저작권 문제

- **대응**: 법무 검토, Riot Games 가이드라인 준수

***

## 15. 성공 지표 (KPI)

### 15.1 사용자 지표

- **DAU (일 활성 사용자)**: 목표 1,000명 (6개월), 5,000명 (1년)
- **MAU (월 활성 사용자)**: 목표 10,000명 (1년)
- **사용자 유지율**: 1주일 30%, 1개월 15%
- **평균 세션 시간**: 30분 이상


### 15.2 서비스 지표

- **게임 생성 수**: 일 100개 이상
- **게임 완료율**: 80% 이상
- **매칭 성공률**: 90% 이상 (빠른 참가)
- **평균 대기 시간**: 3분 이하


### 15.3 기술 지표

- **서버 응답 시간**: 평균 500ms 이하
- **서버 가동률**: 99.5% 이상
- **동시 접속자**: 500명까지 안정적 처리
- **API 에러율**: 1% 이하




## 📊 결론 및 기대효과

**Nexus**는 리그오브레전드 커뮤니티의 실질적 니즈를 해결하는 차별화된 플랫폼입니다.

### 핵심 경쟁우위

1. **완전 무료 운영**: 모든 소프트웨어를 오픈소스로 구성하여 초기 비용 최소화
2. **게임 특화 UX**: Discord + OP.GG + 롤 클라이언트의 장점을 결합한 직관적 인터페이스
3. **실시간 매칭**: 포지션 기반 빠른 참가로 대기시간 최소화
4. **통합 전적 관리**: Riot API 연동으로 신뢰할 수 있는 데이터 제공


**Nexus**는 기술적 실현 가능성이 높고, 명확한 시장 수요가 존재하는 혁신적인 프로젝트입니다. 최소한의 초기 투자로 최대한의 성과를 창출할 수 있는 최적의 비즈니스 모델을 제시합니다.

***

**문서 버전**: 1.0
**최종 수정일**: 2025년 8월 18일
**작성자**: Nexus 개발자 

