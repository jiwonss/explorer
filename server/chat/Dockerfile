FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY build/libs/chat-0.0.1-SNAPSHOT.jar chat.jar
ENV TZ Asia/Seoul
ENTRYPOINT ["java", "-jar", "chat.jar"]