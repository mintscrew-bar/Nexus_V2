# 빌드 스테이지
FROM gradle:8.5-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /build

# Gradle 파일들을 먼저 복사하여 의존성을 캐시
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./
COPY gradlew.bat ./

# Gradle 의존성을 다운로드
RUN gradle dependencies

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드
RUN gradle build -x test

# 실행 스테이지
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 빌더 스테이지에서 생성된 jar 파일을 복사
COPY --from=builder /build/build/libs/*.jar ./app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
