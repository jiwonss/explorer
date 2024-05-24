FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY build/libs/move-0.0.1-SNAPSHOT.jar move.jar
ENV TZ Asia/Seoul
ENTRYPOINT ["java", "-jar", "move.jar"]