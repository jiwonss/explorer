# 기술 스택


## CI/CD

| Skill | Version |
| --- | --- |
| Ubuntu | 20.04.6 LTS |
| Docker | 26.1.1 |
| Docker compose | 2.27.0 |
| Jenkins | 2.457 |

## Client

| Skill | Version |
| --- | --- |
| Unity | 2022.3.24f |

## Database

| Skill | Version |
| --- | --- |
| MariaDB | 10.3.23 |
| MongoDB | 5.0.26 |
| Redis | 7.2.4-alpine |

## Server

| Skill | Version |
| --- | --- |
| IntelliJ | 23.3.2(Ultimate Edition) |
| JDK | 17 |
| Spring Boot | 3.2.5 |
| Spring Cloud | 2023.0.1 |
| JPA | 3.2.5 |
| RabbitMQ | 3.13.2 |
| reactor-netty | 1.1.18 |

# IP/Port 번호


## EC2 (SSAFY 기본 지급)

- IP : 3.36.60.98

| 구분 | Port |
| --- | --- |
| service-discovery | 8761 |
| api-gateway | 8000 |
| config-service | 8888 |
| user | 8100 |
| rabbitmq | 5672, 15672 |
| redis-user | 6380 |
| redis-gateway | 6381 |
| jenkins | 9090 |

## EC2 (추가)

- IP : 3.36.246.94

| 구분 | Port |
| --- | --- |
| chat | 9002, 1371 |
| realtime | 9001, 1370 |
| logic | 9003, 1379 |
| rabbitmq | 5672, 15672 |
| redis-channel | 6380 |
| redis-game | 6381 |
| redis-staticgame | 6382 |

# 환경 변수


- 서버 환경 변수는 Spring Cloud Config 사용
- `src/main/resources/application.yml` 파일에서 profiles을 변경하여 dev, prod 환경에 맞는 환경 변수 세팅 가능
    - dev

        ```yaml
        spring:
          profiles:
            active: dev
        ```

    - prod

        ```yaml
        spring:
          profiles:
            active: prod
        ```

- Github Private Repository에 환경 변수 저장
    - 파일 구조

        ```
        |--- api-gateway-dev.yml
        |--- api-gateway-prod.yml
        |--- chat-dev.yml
        |--- chat-prod.yml
        |--- logic-dev.yml
        |--- logic-prod.yml
        |--- realtime-dev.yml
        |--- realtime-prod.yml
        |--- user-dev.yml
        |--- user-prod.yml
        ```


# 배포 환경 설정


## Server

---

### Docker

1. **Service-Discovery**
    - Dockerfile

        ```docker
        FROM openjdk:17-ea-11-jdk-slim
        VOLUME /tmp
        COPY build/libs/service-discovery-0.0.1-SNAPSHOT.jar service-discovery.jar
        ENV TZ Asia/Seoul
        ENTRYPOINT ["java", "-jar", "service-discovery.jar"]
        ```

    - 실행
        - `docker run --name service-discovery -d -p 8761:8761 vlwli99/service-discovery`
2. **Config-Service**
    - Dockerfile

        ```docker
        FROM openjdk:17-ea-11-jdk-slim
        VOLUME /tmp
        COPY keystore/apiEncryptionKey.jks apiEncryptionKey.jks
        COPY build/libs/config-service-0.0.1-SNAPSHOT.jar config-service.jar
        ENV TZ Asia/Seoul
        ENTRYPOINT ["java", "-jar", "config-service.jar"]
        ```

    - 실행
        - `docker run --name config-service -d -p 8888:8888 vlwli99/config-service`
3. **API-Gateway**
    - Dockerfile

        ```docker
        FROM openjdk:17-ea-11-jdk-slim
        VOLUME /tmp
        COPY build/libs/api-gateway-0.0.1-SNAPSHOT.jar api-gateway.jar
        ENV TZ Asia/Seoul
        ENTRYPOINT ["java","-jar","api-gateway.jar"]
        ```

    - docker-compose.yml

        ```docker
        services:
          redis-user:
            build:
              context: ./redis #Docker 파일 경로
              dockerfile: ./Dockerfile
            container_name: redis-user
            ports:
              - "6380:6380"
            environment:
              - REDIS_PASSWORD=${REDIS_PASSWORD}
            volumes:
              - ./redis_data:/data
            networks: #같은 네트워크로 묶기
              explorer:
                ipv4_address: 172.17.0.4
        
          redis-gateway:
            build:
              context: ../api-gateway/redis
              dockerfile: ./Dockerfile
            container_name: redis-gateway
            ports:
              - "6381:6381"
            environment:
              - REDIS_PASSWORD=${REDIS_PASSWORD}
            volumes:
              - ./redis_data:/data
            networks:
              explorer:
                ipv4_address: 172.17.0.5
        
          rabbitmq:
            build:
              context: ./rabbitmq
              dockerfile: ./Dockerfile
            container_name: rabbitmq
            ports:
              - "5672:5672"
              - "15672:15672"
            environment:
              - RABBITMQ_DEFAULT_USER=${RABBITMQ_USER}
              - RABBITMQ_DEFAULT_PASS=${RABBITMQ_PASS}
            volumes:
              - ./rabbitmq_data:/var/lib/rabbitmq
            networks:
              explorer:
                ipv4_address: 172.17.0.6
        
          springboot-user:
            image: vlwli99/user
            container_name: user
            depends_on: #의존성
              - redis-user
              - rabbitmq
            build:
              context: ./
              dockerfile: ./Dockerfile
            ports:
              - "9000:9000"
            networks:
              explorer:
                ipv4_address: 172.17.0.7
        
          springboot-gateway:
            image: vlwli99/api-gateway
            container_name: api-gateway
            depends_on:
              - redis-gateway
              - rabbitmq
            build:
              context: ../api-gateway
              dockerfile: ./Dockerfile
            ports:
              - "8000:8000"
            networks:
              explorer:
                ipv4_address: 172.17.0.8
        
        networks:
          explorer:
            ipam:
              driver: default
              config:
                - subnet: "172.17.0.0/16"
        ```

    - 실행
        - redis-user, redis-gateway, rabbitmq, user, api-gateway 같이 실행
        - `docker compose up -d`
4. **User**
    - Dockerfile

        ```docker
        FROM openjdk:17-ea-11-jdk-slim
        VOLUME /tmp
        COPY build/libs/user-0.0.1-SNAPSHOT.jar user.jar
        ENV TZ Asia/Seoul
        ENTRYPOINT ["java", "-jar", "user.jar"]
        ```

5. **Realtime**
    - Dockerfile

        ```docker
        FROM openjdk:17-ea-11-jdk-slim
        VOLUME /tmp
        COPY build/libs/realtime-0.0.1-SNAPSHOT.jar realtime.jar
        ENV TZ Asia/Seoul
        ENTRYPOINT ["java", "-jar", "realtime.jar"]
        ```

    - docker-compose.yml

        ```docker
        services:
          redis-channel:
            build:
              context: ./redis-channel
              dockerfile: ./Dockerfile
            container_name: redis-channel
            ports:
              - "6380:6380"
            environment:
              - REDIS_PASSWORD=${REDIS_PASSWORD}
            volumes:
              - ./redis_channel_data:/data
            networks:
              explorer:
                ipv4_address: 172.21.0.3
        
          redis-game:
            build:
              context: ./redis-game
              dockerfile: ./Dockerfile
            container_name: redis-game
            ports:
              - "6381:6381"
            environment:
              - REDIS_PASSWORD=${REDIS_PASSWORD}
            volumes:
              - ./redis_game_data:/data
            networks:
              explorer:
                ipv4_address: 172.21.0.4
        
          redis-staticgame:
            build:
              context: ../logic/redis-staticgame
              dockerfile: ./Dockerfile
            container_name: redis-staticgame
            ports:
              - "6382:6382"
            environment:
              - REDIS_PASSWORD=${REDIS_PASSWORD}
            volumes:
              - ../logic/redis_staticgame_data:/data
            networks:
              explorer:
                ipv4_address: 172.21.0.5
        
          rabbitmq:
            build:
              context: ../chat/rabbitmq
              dockerfile: ./Dockerfile
            container_name: rabbitmq
            ports:
              - "5672:5672"
              - "15672:15672"
            environment:
              - RABBITMQ_DEFAULT_USER=${RABBITMQ_USER}
              - RABBITMQ_DEFAULT_PASS=${RABBITMQ_PASS}
            volumes:
              - ../chat/rabbitmq_data:/var/lib/rabbitmq
            networks:
              explorer:
                ipv4_address: 172.21.0.6
        
          springboot-realtime:
            image: vlwli99/realtime
            container_name: realtime
            depends_on:
              - redis-channel
              - redis-game
              - redis-staticgame
            ports:
              - "9001:9001"
              - "1370:1370"
            networks:
              explorer:
                ipv4_address: 172.21.0.7
        
          springboot-chat:
            image: vlwli99/chat
            container_name: chat
            depends_on:
              - rabbitmq
              - redis-channel
            ports:
              - "9002:9002"
              - "1371:1371"
            environment:
              - JAVA_OPTS=-Dfile.encoding=UTF-8
            networks:
              explorer:
                ipv4_address: 172.21.0.8
        
          springboot-logic:
            image: vlwli99/logic
            container_name: logic
            depends_on:
              - redis-staticgame
            ports:
              - "9003:9003"
              - "1379:1379"
            networks:
              explorer:
                ipv4_address: 172.21.0.9
        
        networks:
          explorer:
            ipam:
              driver: default
              config:
                - subnet: "172.21.0.0/16"
        ```

    - 실행
        - redis-channel, redis-game, redis-staticgame, rabbitmq, realtime, chat, logic 같이 실행
        - `docker-compose up -d`
6. **Chat**
    - Dockerfile

        ```docker
        FROM openjdk:17-ea-11-jdk-slim
        VOLUME /tmp
        COPY build/libs/chat-0.0.1-SNAPSHOT.jar chat.jar
        ENV TZ Asia/Seoul
        ENTRYPOINT ["java", "-jar", "chat.jar"]
        ```

7. **Logic**
    - Dockerfile

        ```docker
        FROM openjdk:17-ea-11-jdk-slim
        VOLUME /tmp
        COPY build/libs/logic-0.0.1-SNAPSHOT.jar logic.jar
        ENV TZ Asia/Seoul
        ENTRYPOINT ["java", "-jar", "logic.jar"]
        ```


### Jenkins

- Service-Discovery

    ```
    pipeline {
        environment {
            repository = "vlwli99/service-discovery"
            dockerImage = ''
        }
    
        agent any
    
        stages {
            stage('Clone Repository') {
                when {
                    changeset "**/server/service-discovery/**"
                }
                steps {
                    checkout scm
                }
            }
            stage('Build Project') {
                when {
                    changeset "**/server/service-discovery/**"
                }
                steps {
                    dir("./server/service-discovery") {
                        sh "chmod +x ./gradlew"
                        sh "./gradlew clean build"
                    }
                }
            }
            stage('Build Image'){
                when {
                    changeset "**/server/service-discovery/**"
                }
                steps{
                    script{
                        dockerImage = docker.build("${repository}:service-discovery-${BUILD_NUMBER}", "-f server/service-discovery/Dockerfile ./server/service-discovery")
                    }
                }
            }
    
            stage('DockerHub Login'){
                when {
                    changeset "**/server/service-discovery/**"
                }
                steps{
                    script{
                        withCredentials([usernamePassword(credentialsId: 'dockerhub_token', usernameVariable: 'DOCKERHUB_ID', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                            sh """
                                set +x
                                echo $DOCKERHUB_PASSWORD | docker login -u $DOCKERHUB_ID --password-stdin
                                set -x
                            """
                        }
                    }
                }
            }
    
            stage('Push Image'){
                when {
                    changeset "**/server/service-discovery/**"
                }
                steps{
                    script{
                        if (dockerImage) {
                            sh "docker push \${repository}:service-discovery-\${BUILD_NUMBER}"
                        } else {
                            echo "No docker image to push."
                        }
                    }
                }
            }
    
            stage('Clean Image'){
                when {
                    changeset "**/server/service-discovery/**"
                }
                steps{
                    script{
                        def imageExists = sh(script: "docker images -q \${repository}:service-discovery-\${BUILD_NUMBER}", returnStdout: true).trim()
                        if (imageExists) {
                            sh "docker rmi \${repository}:service-discovery-\${BUILD_NUMBER}"
                        } else {
                            echo "No image found to remove."
                        }
                    }
                }
            }
    
            stage("Garbage Collect") {
                when {
                    changeset "**/server/service-discovery/**"
                }
                steps {
                    script {
                        def containers = sh(script: "docker ps -q --filter ancestor=\${repository}:service-discovery-\${BUILD_NUMBER}", returnStdout: true).trim()
                        if (containers) {
                            sh "docker kill \${containers}"
                        }
                        sh "docker system prune -af --volumes"
                    }
                }
            }
    
            stage("Deploy"){
                when {
                    changeset "**/server/service-discovery/**"
                }
                steps{
                    script{
                        def containerExists = sh(script: "docker ps -aq --filter 'name=service-discovery'", returnStdout: true).trim()
                        if (containerExists) {
                            sh "docker rm -f service-discovery"
                        }
                        sh "docker run -d -p 8761:8761 --name service-discovery \${repository}:service-discovery-\${BUILD_NUMBER}"
                    }
                }
            }
        }
        post {
            success {
                script {
                    def Author_ID = sh(script: "git show -s --pretty=%an", returnStdout: true).trim()
                    def Author_Name = sh(script: "git show -s --pretty=%ae", returnStdout: true).trim()
                    mattermostSend (color: 'good',
                    message: "빌드 성공: ${env.JOB_NAME} #${env.BUILD_NUMBER} by ${Author_ID}(${Author_Name})\n(<${env.BUILD_URL}|Details>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/sdq6nmebninyff7pdd9nszymko',
                    channel: 'C201_Jenkins'
                    )
                }
            }
            failure {
                script {
                    def Author_ID = sh(script: "git show -s --pretty=%an", returnStdout: true).trim()
                    def Author_Name = sh(script: "git show -s --pretty=%ae", returnStdout: true).trim()
                    mattermostSend (color: 'danger',
                    message: "빌드 실패: ${env.JOB_NAME} #${env.BUILD_NUMBER} by ${Author_ID}(${Author_Name})\n(<${env.BUILD_URL}|Details>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/sdq6nmebninyff7pdd9nszymko',
                    channel: 'C201_Jenkins'
                    )
                }
            }
        }
    }
    
    ```

- Config-service

    ```
    pipeline {
        environment {
            repository = "vlwli99/config-service"
            dockerImage = ''
        }
    
        agent any
    
        stages {
            stage('Clone Repository') {
                when {
                    changeset "**/server/config-service/**"
                }
                steps {
                    checkout scm
                }
            }
            stage('Build Project') {
                when {
                    changeset "**/server/config-service/**"
                }
                steps {
                    dir("./server/config-service") {
                        sh "chmod +x ./gradlew"
                        sh "./gradlew clean build"
                    }
                }
            }
            stage('Build Image'){
                when {
                    changeset "**/server/config-service/**"
                }
                steps{
                    script{
                        dockerImage = docker.build("${repository}:config-service-${BUILD_NUMBER}", "-f server/config-service/Dockerfile ./server/config-service")
                    }
                }
            }
    
            stage('DockerHub Login'){
                when {
                    changeset "**/server/config-service/**"
                }
                steps{
                    script{
                        withCredentials([usernamePassword(credentialsId: 'dockerhub_token', usernameVariable: 'DOCKERHUB_ID', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                            sh """
                                set +x
                                echo $DOCKERHUB_PASSWORD | docker login -u $DOCKERHUB_ID --password-stdin
                                set -x
                            """
                        }
                    }
                }
            }
    
            stage('Push Image'){
                when {
                    changeset "**/server/config-service/**"
                }
                steps{
                    script{
                        if (dockerImage) {
                            sh "docker push \${repository}:config-service-\${BUILD_NUMBER}"
                        }
                    }
                }
            }
    
            stage('Clean Image'){
                when {
                    changeset "**/server/config-service/**"
                }
                steps{
                    script{
                        def imageExists = sh(script: "docker images -q \${repository}:config-service-\${BUILD_NUMBER}", returnStdout: true).trim()
                        if (imageExists) {
                            sh "docker rmi \${repository}:config-service-\${BUILD_NUMBER}"
                        }
                    }
                }
            }
    
            stage("Garbage Collect") {
                when {
                    changeset "**/server/config-service/**"
                }
                steps {
                    script {
                        def containers = sh(script: "docker ps -q --filter ancestor=\${repository}:config-service-\${BUILD_NUMBER}", returnStdout: true).trim()
                        if (containers) {
                            sh "docker kill \${containers}"
                        }
                        sh "docker system prune -af --volumes"
                    }
                }
            }
    
            stage("Deploy"){
                when {
                    changeset "**/server/config-service/**"
                }
                steps{
                    script{
                        def containerExists = sh(script: "docker ps -aq --filter 'name=config-service'", returnStdout: true).trim()
                        if (containerExists) {
                            sh "docker rm -f config-service"
                        }
                        sh "docker run -d -p 8888:8888 --name config-service \${repository}:config-service-\${BUILD_NUMBER}"
                    }
                }
            }
        }
        post {
            success {
                script {
                    def Author_ID = sh(script: "git show -s --pretty=%an", returnStdout: true).trim()
                    def Author_Name = sh(script: "git show -s --pretty=%ae", returnStdout: true).trim()
                    mattermostSend (color: 'good',
                    message: "빌드 성공: ${env.JOB_NAME} #${env.BUILD_NUMBER} by ${Author_ID}(${Author_Name})\n(<${env.BUILD_URL}|Details>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/sdq6nmebninyff7pdd9nszymko',
                    channel: 'C201_Jenkins'
                    )
                }
            }
            failure {
                script {
                    def Author_ID = sh(script: "git show -s --pretty=%an", returnStdout: true).trim()
                    def Author_Name = sh(script: "git show -s --pretty=%ae", returnStdout: true).trim()
                    mattermostSend (color: 'danger',
                    message: "빌드 실패: ${env.JOB_NAME} #${env.BUILD_NUMBER} by ${Author_ID}(${Author_Name})\n(<${env.BUILD_URL}|Details>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/sdq6nmebninyff7pdd9nszymko',
                    channel: 'C201_Jenkins'
                    )
                }
            }
        }
    }
    
    ```

- API-Gateway (& User)

    ```
    pipeline {
        environment {
            repository = "vlwli99/api-gateway"
            dockerImage = ''
        }
    
        agent any
    
        stages {
            stage('Clone Repository') {
                when {
                    anyOf {
                        changeset "**/server/api-gateway/**"
                        changeset "**/server/user/**"
                    }
                }
                steps {
                    checkout scm
                }
            }
            stage('Build Project') {
                when {
                    anyOf {
                        changeset "**/server/api-gateway/**"
                        changeset "**/server/user/**"
                    }
                }
                steps {
                    dir("./server/api-gateway") {
                        sh "chmod +x ./gradlew"
                        sh "./gradlew clean build"
                    }
                    dir("./server/user") {
                        sh "chmod +x ./gradlew"
                        sh "./gradlew clean build"
                    }
                }
            }
            stage('Build Image'){
                when {
                    anyOf {
                        changeset "**/server/api-gateway/**"
                        changeset "**/server/user/**"
                    }
                }
                steps{
                    script {
                        dir("./server/api-gateway") {
                            sh 'docker-compose -f docker-compose.yml build'
                        }
                    }
                }
            }
    
            stage('DockerHub Login'){
                when {
                    anyOf {
                        changeset "**/server/api-gateway/**"
                        changeset "**/server/user/**"
                    }
                }
                steps{
                    script{
                        withCredentials([usernamePassword(credentialsId: 'dockerhub_token', usernameVariable: 'DOCKERHUB_ID', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                            sh """
                                set +x
                                echo $DOCKERHUB_PASSWORD | docker login -u $DOCKERHUB_ID --password-stdin
                                set -x
                            """
                        }
                    }
                }
            }
    
            stage('Push Image'){
                when {
                    anyOf {
                        changeset "**/server/api-gateway/**"
                        changeset "**/server/user/**"
                    }
                }
                steps{
                    script{
                        dir("./server/api-gateway") {
                            sh 'docker-compose -f docker-compose.yml push'
                        }
                    }
                }
            }
    
            stage('Clean Image'){
                when {
                    anyOf {
                        changeset "**/server/api-gateway/**"
                        changeset "**/server/user/**"
                    }
                }
                steps {
                    script {
                        def imageNames = [
                            "vlwli99/api-gateway",
                            "vlwli99/user",
                            "api-gateway-redis-user",
                            "api-gateway-redis-gateway",
                            "api-gateway-rabbitmq"
                        ]
    
                        imageNames.each { imageName ->
                            def imageIds = sh(script: "docker images -q ${imageName}", returnStdout: true).trim().split()
                            imageIds.each { id ->
                                if (id) {
                                    sh "docker rmi ${id} || true"
                                }
                            }
                        }
    
                        sh 'docker image prune -f --filter until=1h'
                    }
                }
            }
    
            stage('Pull') {
                when {
                    anyOf {
                        changeset "**/server/api-gateway/**"
                        changeset "**/server/user/**"
                    }
                }
                steps {
                    script {
                        dir("./server/api-gateway") {
                            sh "docker-compose pull"
                        }
                    }
                }
            }
    
            stage("Down") {
                when {
                    anyOf {
                        changeset "**/server/api-gateway/**"
                        changeset "**/server/user/**"
                    }
                }
                steps {
                    script {
                        dir("./server/api-gateway") {
                            echo "Attempting to bring down containers..."
                            sh "docker-compose down --remove-orphans --volumes || true"
    
                            echo "Forcing container stop and removal..."
                            sh "docker-compose rm -fsv || true"
    
                        }
                        echo "Attempting to clean up all unused Docker resources..."
                        retry(3) {
                            sleep time: 10, unit: 'SECONDS'
                            sh "docker system prune -af --volumes || true"
                        }
                    }
                }
            }
    
            stage("Up"){
                when {
                    anyOf {
                        changeset "**/server/api-gateway/**"
                        changeset "**/server/user/**"
                    }
                }
                steps{
                    script{
                        dir("./server/api-gateway") {
                            sh "docker-compose up -d --build"
                        }
                    }
                }
            }
        }
        post {
            success {
                script {
                    def Author_ID = sh(script: "git show -s --pretty=%an", returnStdout: true).trim()
                    def Author_Name = sh(script: "git show -s --pretty=%ae", returnStdout: true).trim()
                    mattermostSend (color: 'good',
                    message: "빌드 성공: ${env.JOB_NAME} #${env.BUILD_NUMBER} by ${Author_ID}(${Author_Name})\n(<${env.BUILD_URL}|Details>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/sdq6nmebninyff7pdd9nszymko',
                    channel: 'C201_Jenkins'
                    )
                }
            }
            failure {
                script {
                    def Author_ID = sh(script: "git show -s --pretty=%an", returnStdout: true).trim()
                    def Author_Name = sh(script: "git show -s --pretty=%ae", returnStdout: true).trim()
                    mattermostSend (color: 'danger',
                    message: "빌드 실패: ${env.JOB_NAME} #${env.BUILD_NUMBER} by ${Author_ID}(${Author_Name})\n(<${env.BUILD_URL}|Details>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/sdq6nmebninyff7pdd9nszymko',
                    channel: 'C201_Jenkins'
                    )
                }
            }
        }
    }
    
    ```

- Realtime (& Chat, Logic)

    ```
    pipeline {
        environment {
            repository = "vlwli99/realtime"
            dockerImage = ''
        }
    
        agent any
    
        stages {
            stage('Clone Repository') {
                when {
                    anyOf {
                        changeset "**/server/chat/**"
                        changeset "**/server/realtime/**"
                        changeset "**/server/logic/**"
                    }
                }
                steps {
                    checkout scm
                }
            }
            stage('Build Project') {
                when {
                    anyOf {
                        changeset "**/server/chat/**"
                        changeset "**/server/realtime/**"
                        changeset "**/server/logic/**"
                    }
                }
                steps {
                    dir("./server/chat") {
                        sh "chmod +x ./gradlew"
                        sh "./gradlew clean build"
                    }
                    dir("./server/realtime") {
                        sh "chmod +x ./gradlew"
                        sh "./gradlew clean build"
                    }
                    dir("./server/logic") {
                        sh "chmod +x ./gradlew"
                        sh "./gradlew clean build"
                    }
                }
            }
    
            stage('Build Image'){
                when {
                    anyOf {
                        changeset "**/server/chat/**"
                        changeset "**/server/realtime/**"
                        changeset "**/server/logic/**"
                    }
                }
                steps{
                    script {
                        dir("./server/chat") {
                            sh 'docker build --tag vlwli99/chat .'
                        }
                        dir("./server/realtime") {
                            sh 'docker build --tag vlwli99/realtime .'
                        }
                        dir("./server/logic") {
                            sh 'docker build --tag vlwli99/logic .'
                        }
                    }
                }
            }
    
            stage('DockerHub Login'){
                when {
                    anyOf {
                        changeset "**/server/chat/**"
                        changeset "**/server/realtime/**"
                        changeset "**/server/logic/**"
                    }
                }
                steps{
                    script{
                        withCredentials([usernamePassword(credentialsId: 'dockerhub_token', usernameVariable: 'DOCKERHUB_ID', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                            sh """
                                set +x
                                echo $DOCKERHUB_PASSWORD | docker login -u $DOCKERHUB_ID --password-stdin
                                set -x
                            """
                        }
                    }
                }
            }
    
            stage('Push Image'){
                when {
                    anyOf {
                        changeset "**/server/chat/**"
                        changeset "**/server/realtime/**"
                        changeset "**/server/logic/**"
                    }
                }
                steps{
                    script{
                        dir("./server/chat") {
                            sh 'docker push vlwli99/chat'
                        }
                        dir("./server/realtime") {
                            sh 'docker push vlwli99/realtime'
                        }
                        dir("./server/logic") {
                            sh 'docker push vlwli99/logic'
                        }
                    }
                }
            }
    
            stage('Clean Image'){
                when {
                    anyOf {
                        changeset "**/server/chat/**"
                        changeset "**/server/realtime/**"
                        changeset "**/server/logic/**"
                    }
                }
                steps {
                    script {
                        def imageNames = [
                            "vlwli99/chat",
                            "vlwli99/realtime",
                            "vlwli99/logic",
                            "rabbitmq",
                            "redis-channel",
                            "redis-game",
                            "redis-staticgame",
                        ]
    
                        imageNames.each { imageName ->
                            def imageIds = sh(script: "docker images -q ${imageName}", returnStdout: true).trim().split()
                            imageIds.each { id ->
                                if (id) {
                                    sh "docker rmi ${id} || true"
                                }
                            }
                        }
    
                        sh 'docker image prune -f --filter until=1h'
                    }
                }
            }
    
            stage("Down") {
                when {
                    anyOf {
                        changeset "**/server/chat/**"
                        changeset "**/server/realtime/**"
                        changeset "**/server/logic/**"
                    }
                }
                steps {
                    sshPublisher(
                        continueOnError: false, failOnError: true,
                        publishers: [
                            sshPublisherDesc(
                                configName: "explorer",
                                verbose: true,
                                transfers: [
                                    sshTransfer(
                                        execCommand: """
                                            cd /home/ubuntu/docker/realtime;
                                            echo "Attempting to bring down containers...";
                                            sudo docker-compose down --remove-orphans --volumes || true;
                                            sudo docker system prune -af --volumes || true;
                                        """
                                    )
                                ]
                            )
                        ]
                    )
                }
            }
    
            stage("Up"){
                when {
                    anyOf {
                        changeset "**/server/chat/**"
                        changeset "**/server/realtime/**"
                        changeset "**/server/logic/**"
                    }
                }
                steps {
                    sshPublisher(
                        continueOnError: false, failOnError: true,
                        publishers: [
                            sshPublisherDesc(
                                configName: "explorer",
                                verbose: true,
                                transfers: [
                                    sshTransfer(execCommand: "cd /home/ubuntu/docker/realtime && sudo docker-compose up -d --build")
                                ]
                            )
                        ]
                    )
                }
            }
        }
        post {
            success {
                script {
                    def Author_ID = sh(script: "git show -s --pretty=%an", returnStdout: true).trim()
                    def Author_Name = sh(script: "git show -s --pretty=%ae", returnStdout: true).trim()
                    mattermostSend (color: 'good',
                    message: "빌드 성공: ${env.JOB_NAME} #${env.BUILD_NUMBER} by ${Author_ID}(${Author_Name})\n(<${env.BUILD_URL}|Details>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/sdq6nmebninyff7pdd9nszymko',
                    channel: 'C201_Jenkins'
                    )
                }
            }
            failure {
                script {
                    def Author_ID = sh(script: "git show -s --pretty=%an", returnStdout: true).trim()
                    def Author_Name = sh(script: "git show -s --pretty=%ae", returnStdout: true).trim()
                    mattermostSend (color: 'danger',
                    message: "빌드 실패: ${env.JOB_NAME} #${env.BUILD_NUMBER} by ${Author_ID}(${Author_Name})\n(<${env.BUILD_URL}|Details>)",
                    endpoint: 'https://meeting.ssafy.com/hooks/sdq6nmebninyff7pdd9nszymko',
                    channel: 'C201_Jenkins'
                    )
                }
            }
        }
    }
    ```


# 특이사항


- SERVER : 2개의 인스턴스로 나누어 배포 진행
- SERVER : 배포 시 모든 프로젝트의 application.yml profile을 prod로 설정 후 진행
- SERVER : 로컬 실행 시 모든 프로젝트의 application.yml profile을 dev로 설정 후 진행
