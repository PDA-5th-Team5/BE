FROM gradle:8.0-jdk17 AS build
WORKDIR /app

COPY settings.gradle build.gradle gradlew gradlew.bat ./
COPY gradle ./gradle
COPY eureka-server ./eureka-server
COPY util-service ./util-service

RUN ls -al /app && ./gradlew :eureka-server:clean :eureka-server:build --build-cache --parallel --daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/eureka-server/build/libs/*.jar app.jar


EXPOSE 8761
CMD ["java", "-jar", "app.jar"]
