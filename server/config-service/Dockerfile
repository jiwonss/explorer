FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY ./apiEncryptionKey.jks apiEncryptionKey.jks
COPY build/libs/config-service-0.0.1-SNAPSHOT.jar config-service.jar
ENV TZ Asia/Seoul
ENTRYPOINT ["java", "-jar", "config-service.jar"]