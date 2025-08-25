
Nexus - 리그 오브 레전드 내전 플랫폼
Nexus는 리그 오브 레전드 플레이어들이 쉽고 재미있게 내전(커스텀 게임)을 조직하고, 팀을 구성하며, 전적을 관리할 수 있는 차세대 소셜 플랫폼입니다. 이제 더 이상 팀원을 구하기 위해 여러 커뮤니티를 방황할 필요가 없습니다. Nexus에서 여러분의 팀과 대회를 직접 만들어보세요!

🚀 주요 기능 (Features)
🔐 간편하고 안전한 로그인: Keycloak을 활용한 통합 인증 시스템으로, 소셜 로그인(Google, Discord)을 지원합니다.

🏟️ 대규모 내전 로비: 10명부터 최대 50명까지, 5명 단위로 인원을 설정할 수 있는 **'로비'**를 생성하여 대규모 이벤트를 조직할 수 있습니다.

🤖 Riot API 연동 (예정): 로비에서 팀 구성이 완료되면, Riot 토너먼트 API를 통해 실제 리그 오브 레전드 사용자 설정 게임을 자동으로 생성합니다.

📊 실시간 전적 및 통계: Riot API와 연동하여 개인 및 팀의 전적을 자동으로 추적하고, 심도 있는 통계를 제공합니다.

💬 활발한 커뮤니티: 자유게시판, 팀원 모집, 공략 공유 등 다양한 커뮤니티 기능을 통해 다른 플레이어들과 소통할 수 있습니다.

🛠️ 기술 스택 (Tech Stack)
<table>
<tr>
<td align="center"><strong>Backend</strong></td>
<td align="center"><strong>Frontend</strong></td>
<td align="center"><strong>Database</strong></td>
<td align="center"><strong>Infra & DevOps</strong></td>
</tr>
<tr>
<td align="center">
<img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white" />
<img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" />
<img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white" />
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" />
</td>
<td align="center">
<img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" />
<img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=white" />
<img src="https://img.shields.io/badge/Next.js-000000?style=for-the-badge&logo=next.js&logoColor=white" />
<img src="https://img.shields.io/badge/Zustand-000000?style=for-the-badge" />
</td>
<td align="center">
<img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" />
<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" />
</td>
<td align="center">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
<img src="https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white" />
<img src="https://img.shields.io/badge/Keycloak-000000?style=for-the-badge&logo=keycloak&logoColor=white" />
</td>
</tr>
</table>

🏗️ 시스템 아키텍처 (Architecture)
코드 스니펫

flowchart LR
  subgraph User
    direction LR
    Client[Browser]
  end

  subgraph Nexus Platform (Docker)
    direction LR
    Nginx --> Frontend[Next.js]
    Nginx --> Backend[Spring Boot]
    
    subgraph Authentication
        Keycloak
    end

    subgraph Database
        PostgreSQL
        Redis
    end

    Frontend -- API Request --> Backend
    Backend -- Data --> PostgreSQL
    Backend -- Cache/Session --> Redis
    Frontend -- Login/OAuth --> Keycloak
    Backend -- Token Validation --> Keycloak
  end

  subgraph Riot Games
    direction LR
    RiotAPI[Riot API]
  end

  Client -- HTTPS --> Nginx
  Backend -- REST API --> RiotAPI

위 다이어그램은 프로젝트의 전체적인 구조를 나타냅니다.

⚙️ 시작하기 (Getting Started)
프로젝트를 로컬 환경에서 실행하기 위한 가이드입니다.

1. 사전 준비 (Prerequisites)
Docker가 설치되어 있어야 합니다.

프로젝트 루트 디렉터리에 .env 파일을 생성해야 합니다.

2. 환경 설정 (.env 파일)
프로젝트 루트 경로에 .env 파일을 생성하고, 제공된 .env.example 파일을 참고하여 아래와 같이 내용을 채워주세요.

코드 스니펫

# .env 파일 예시

# Server Configuration
SERVER_PORT=8080

# Database Configuration
DB_URL=jdbc:postgresql://db:5432/nexusdb
DB_USERNAME=user
DB_PASSWORD=password

# API Keys
RIOT_API_KEY=RGAPI-YOUR-RIOT-API-KEY

# JWT Authentication
JWT_SECRET_KEY=your-very-secret-and-long-jwt-key
JWT_EXPIRATION_MS=86400000

# External Services
DISCORD_WEBHOOK_URL=

# Security Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000

# Keycloak Admin Account
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin
3. 프로젝트 실행
터미널을 열고 프로젝트 루트 디렉터리에서 아래 명령어를 실행하세요.

Bash

docker-compose up --build
이제 모든 서비스(프론트엔드, 백엔드, 데이터베이스, Keycloak)가 Docker 컨테이너로 실행됩니다.

프론트엔드: http://localhost:3000

Keycloak 관리자 콘솔: http://localhost:8180

Username: .env 파일에 설정한 KEYCLOAK_ADMIN

Password: .env 파일에 설정한 KEYCLOAK_ADMIN_PASSWORD

📝 프로젝트 현황 (Project Status)
Phase 1: 기반 구축 (완료)

Docker 기반 개발 환경 구축 완료

Keycloak을 이용한 통합 인증 시스템 구현 완료

기본적인 데이터베이스 스키마 및 Entity 설계 완료

Phase 2: 내전 시스템 (진행 중)

게임 '로비' 생성 및 조회 API 구현 완료

'로비'와 실제 '매치'를 분리하는 설계 적용

더 상세한 내용은 개발 로드맵을 참고해주세요.