FROM gradle:8.0-jdk17 AS build
WORKDIR /app

COPY settings.gradle build.gradle gradlew gradlew.bat ./
COPY gradle ./gradle
COPY user-service ./user-service
COPY util-service ./util-service

RUN ls -al /app && ./gradlew :user-service:clean :user-service:build --build-cache --parallel --daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/user-service/build/libs/*.jar app.jar

EXPOSE 8082
CMD ["java", "-jar", "app.jar"]

