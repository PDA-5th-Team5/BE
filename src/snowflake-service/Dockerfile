FROM gradle:8.0-jdk17 AS build
WORKDIR /app

COPY settings.gradle build.gradle gradlew gradlew.bat ./
COPY gradle ./gradle
COPY snowflake-service ./snowflake-service
COPY util-service ./util-service

RUN ls -al /app && ./gradlew :snowflake-service:clean :snowflake-service:build --build-cache --parallel --daemon

FROM openjdk:17-slim  # ✅ Ubuntu 24.04와 무관, 최적화된 실행 환경
WORKDIR /app
COPY --from=build /app/snowflake-service/build/libs/*.jar app.jar

EXPOSE 8085
CMD ["java", "-jar", "app.jar"]
