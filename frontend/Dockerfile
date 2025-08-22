# frontend/Dockerfile

# ======================================================================
# 1. 의존성(Dependencies) 설치 단계
# ======================================================================
# Node.js 20-alpine 버전을 기반으로 의존성 설치 환경을 구성합니다. 'alpine'은 가벼운 리눅스 버전입니다.
FROM node:20-alpine AS deps

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 먼저 package.json과 package-lock.json을 복사합니다.
# 이렇게 하면 소스 코드가 변경되어도 매번 모든 의존성을 새로 설치하지 않아 빌드 속도가 향상됩니다.
COPY package.json package-lock.json ./

# 프로젝트 의존성을 설치합니다.
RUN npm install

# ======================================================================
# 2. 빌드(Build) 단계
# ======================================================================
# 의존성이 설치된 환경을 기반으로 빌드 환경을 구성합니다.
FROM node:20-alpine AS builder

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 'deps' 단계에서 설치된 node_modules를 복사합니다.
COPY --from=deps /app/node_modules ./node_modules
# 나머지 모든 소스 코드를 복사합니다.
COPY . .

# Next.js 애플리케이션을 프로덕션용으로 빌드합니다.
RUN npm run build

# ======================================================================
# 3. 실행(Runner) 단계
# ======================================================================
# 최종적으로 애플리케이션을 실행할 환경을 구성합니다.
FROM node:20-alpine AS runner

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 프로덕션 실행에 필요한 최소한의 파일들만 'builder' 단계에서 복사해옵니다.
# 이렇게 하면 최종 이미지의 크기가 매우 작아져 효율적입니다.
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json

# 컨테이너의 3000번 포트를 외부에 노출시킵니다.
EXPOSE 3000

# 컨테이너가 시작될 때 실행할 명령어를 정의합니다.
# 'npm start' 명령으로 Next.js 프로덕션 서버를 실행합니다.
CMD ["npm", "start"]
