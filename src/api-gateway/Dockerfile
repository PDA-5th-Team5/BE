FROM gradle:8.0-jdk17 AS build
WORKDIR /app

COPY settings.gradle build.gradle gradlew gradlew.bat ./
COPY gradle ./gradle
COPY api-gateway ./api-gateway
COPY util-service ./util-service

RUN ls -al /app && ./gradlew :api-gateway:clean :api-gateway:build --build-cache --parallel --daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/api-gateway/build/libs/*.jar app.jar

EXPOSE 8081
CMD ["java", "-jar", "app.jar"]
