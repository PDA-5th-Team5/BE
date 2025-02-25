# Ubuntu 24.04 + OpenJDK 17 사용
FROM ubuntu:24.04

RUN apt update && apt install -y openjdk-17-jdk

# 작업 디렉터리 설정
WORKDIR /app

COPY build/libs/app.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]