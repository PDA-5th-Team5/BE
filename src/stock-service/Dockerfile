FROM gradle:8.0-jdk17 AS build
WORKDIR /app

COPY settings.gradle build.gradle gradlew gradlew.bat ./
COPY gradle ./gradle
COPY stock-service ./stock-service
COPY util-service ./util-service

RUN ls -al /app && ./gradlew :stock-service:clean :stock-service:build --build-cache --parallel --daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/stock-service/build/libs/*.jar app.jar


EXPOSE 8084
CMD ["java", "-jar", "app.jar"]
