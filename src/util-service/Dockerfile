FROM gradle:8.0-jdk17 AS build
WORKDIR /app

COPY settings.gradle build.gradle gradlew gradlew.bat ./
COPY gradle ./gradle
COPY util-service ./util-service

RUN ls -al /app && ./gradlew :util-service:clean :util-service:build --build-cache --parallel --daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/util-service/build/libs/*.jar util-service.jar