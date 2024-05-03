FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY build/libs/user-0.0.1-SNAPSHOT.jar user.jar
ENV TZ Asia/Seoul
ENTRYPOINT ["java", "-jar", "user.jar"]