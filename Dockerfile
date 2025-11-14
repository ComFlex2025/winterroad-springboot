# 1. JDK 이미지 사용
FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app

# 2. Gradle wrapper 포함해서 전체 복사
COPY . .

# 3. 빌드 (테스트는 제외)
RUN ./gradlew bootJar -x test

# 4. 실행 단계용 JDK 이미지
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 5. builder 단계에서 jar 복사
COPY --from=builder /app/build/libs/app.jar app.jar

# 6. Render가 제공하는 PORT 환경변수 사용
EXPOSE 8080

# 7. 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
