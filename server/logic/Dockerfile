FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY build/libs/logic-0.0.1-SNAPSHOT.jar logic.jar
ENV TZ Asia/Seoul
ENTRYPOINT ["java", "-jar", "logic.jar"]