FROM gradle:8.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build --build-cache --parallel --daemon

FROM openjdk:17-slim  # ✅ Ubuntu 24.04와 무관, 최적화된 실행 환경
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8084
CMD ["java", "-jar", "app.jar"]
