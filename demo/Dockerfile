# ---- 빌드 단계 ----
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# 의존성 캐시를 위해 소스 복사 전에 한 번 받아둠 (실패해도 무시)
RUN ./gradlew dependencies --no-daemon || true

COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# ---- 실행 단계 ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

# 업로드 파일 저장 폴더 (플랫폼에 볼륨을 붙이지 않으면 재배포 시 초기화됨)
RUN mkdir -p /app/uploads

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
