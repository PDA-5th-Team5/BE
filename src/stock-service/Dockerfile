FROM gradle:8.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build --build-cache --parallel --daemon

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8084
CMD ["java", "-jar", "app.jar"]
