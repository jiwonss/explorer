FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY build/libs/realtime-0.0.1-SNAPSHOT.jar realtime.jar
ENV TZ Asia/Seoul
ENTRYPOINT ["java", "-jar", "realtime.jar"]