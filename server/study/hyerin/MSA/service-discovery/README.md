# 개념

- MSA(마이크로서비스 아키텍처)로 구성되어 있는 서비스들은 각자 다른 IP와 Port를 가지고 있다. 서로 다른 서비스들의 IP와 Port 정보에 대해서 저장하고 관리할 필요가 있는데 이것을 Service Discovery라고 한다.

# 역할

- 어떠한 서비스가 어느 위치에 있는지 등록된 정보를 가짐
- 서비스들의 등록과 검색 관련 작업을 해줌
    - 요청 정보가 들어왔을 때 요청 정보에 따라 필요한 서비스 위치 알려줌

### 과정

1. 각각의 MSA가 위치 정보를 Eureka 서버에 등록함
2. Client는 요청 정보를 Load Balancer(API Gateway)에 전달함
3. Service Discovery에 전달되어 서버 검색을 수행하고 Load Balancer에 반환함
4. 사용자 요청 정보가 호출되고 결과값을 반환함

# 구현 방법

## Client-Side Discovery pattern

- 서비스 클라이언트가 Service register에서 서비스의 위치를 찾아서 호출하는 방식
- 대표적으로 Netflix OSS에서 Client-Side Discovery pattern을 제공하는 Netflix Eureka가 Service Registry 역할을 하는 OSS임

### 장점

- 비교적 간단함
- 클라이언트가 사용 가능한 서비스 인스턴스에 대해서 알고 있기 때문에 각 서비스별 로드 밸런싱 방법을 선택할 수 있음

### 단점

- 클라이언트와 서비스 레지스트리가 연결되어 있어 종속적임
- 서비스 클라이언트에서 사용하는 각 프로그래밍 언어 및 프레임 워크에 대해서 클라이언트 측 서비스 검색 로직을 구현해야 함

## Server-Side Discovery pattern

- 호출되는 서비스 앞에 로드밸런서를 넣는 방식이고, 클라이언트는 로드밸런서를 호출하면 로드밸런서가 Service register로부터 등록된 서비스의 위치를 전달하는 방식
- Server-Side Discovery의 예로는 AWS Elastic Load Balancer(ELB), Kubernetes가 있음

### 장점

- discovery의 세부 사항이 클라이언트로부터 분리되어 있음
- 분리되어 있어 클라이언트는 단순히 로드 밸런서에 요청만 함
    - 각 프로그래밍 언어 및 프레임워크에 대한 검색 로직을 구현할 필요가 없음
- 일부 배포 환경에서는 이 기능을 무료로 제공함

# 프로젝트 생성

## Service Discovery

1. Dependencies 설정
    - Eureka Server
    
2. application.yml 설정
    
    ```yaml
    server:
      port: 8761
    
    spring:
      application:
        name: discoveryservice
    
    eureka:
      client:
        register-with-eureka: false #레지스트리에 자신을 등록할지에 대한 여부
        fetch-registry: false #레지스트리에 있는 정보를 가져올지에 대한 여부
    ```
    
3. DiscoveryserviceApplication에 `@EnableEurekaServer` 어노테이션 추가
    

## User Service

- 샘플 MSA 서비스
1. Dependencies 설정
    - Eureka Discovery Client
    - Spring Boot DevTools
    - Lombok
    - Spring Web
    
2. application.yml 설정
    
    ```yaml
    server:
      port: 9001 #인스턴스 다르게 실행할 때마다 port번호 지정
    						 #해당 방식 불편하여 추후 랜덤 port 방법 사용
    
    spring:
      application:
        name: user-service
    
    eureka:
      client:
        register-with-eureka: true
        fetch-registry: true
        service-url:
          defaultZone: http://127.0.0.1:8761/eureka #eureka에 현재 MSA 등록
    ```
    
3. UserServiceApplication에 `@EnableDiscoveryClient` 어노테이션 추가
    

# 참고

[서비스 디스커버리 (Service Discover)란?](https://gimmesome.tistory.com/227)