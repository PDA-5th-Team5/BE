FROM ubuntu:24.04

RUN apt update && apt install -y openjdk-17-jdk

WORKDIR /app
COPY build/libs/app.jar app.jar

EXPOSE 8085

CMD ["java", "-jar", "app.jar"]