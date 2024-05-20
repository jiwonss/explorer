# API Gateway (or Load Balancer)

규모에 상관없이 API 생성, 유지 관리, 모니터링과 보호를 할 수 있게 해주는 서비스

- 모든 클라이언트는 각 서비스의 엔드포인트 대신 API Gateway로 요청을 전달하여 관리가 용이해짐
    - **Reverse Proxy** 처럼 클라이언트 앞 단에 위치하며 모든 서버로의 요청을 단일지점을 거쳐서 처리하도록 함
- 사용자가 설정한 라우팅 설정에 따라 각 엔드포인트로 클라이언트를 대리하여 요청하고 응답을 받으면 다시 클라이언트에게 전달하는 프록시(proxy) 역할을 함
- 시스템의 내부 구조는 숨기고 외부 요청에 대해 적절한 형태로 가공해서 응답할 수 있다는 장점을 가짐

## 역할

- 인증 및 권한 부여 단일 작업 가능
- 클라이언트 요청이 들어온 MS에 문제가 생겼을 경우 요청을 넘겨주지 않는 회로 차단
- ELK와 같은 Log처리 시스템도 있지만 API Gateway에서 단일화 처리할 수 있음

## Netflix Ribbon(maintenece)

- Spring Cloud에서의 MSA간 통신
    
    1) RestTemplate
    
    2) Feign Client

Ribbon: **Client side** Load Balancer

- 서비스 이름으로 호출
- Health Check : 정상 작동 확인

## Netflix Zuul(maintenece)

- API Gateway 역할을 함

# Spring Cloud Gateway

## 개념

- MSA 환경에서 사용하는 API Gateway중 하나로 Spring5, Spring Boot2, Project Reactor로 구축된 Spring 환경의 API Gateway
- API 라우팅 및 보안, 모니터링/메트릭 등의 기능을 간단하고 효과적인 방법으로 제공

## 장점

- 오픈소스 기반으로 라이선스 투자 비용이 없다.
- Spring에서 제공하는 Boot, Security, OAuth2 등 다양한 컴포넌트와 조합하여 효율적인 개발이 가능하다.
- WebFlux 기반의 Non-Blocking을 사용한다. 이는 I/O 작업이 완료되기를 기다리지 않고 다른 작업을 수행할 수 있으며 빠른 응답을 확보할 수 있음을 의미한다.
- yaml 파일로 간단하게 라우팅이 가능하다.

## 단점

- 다른 API 게이트웨이에 비해 기능들이 미숙하다. 특정한 UI 기반의 관리 도구가 필요한 경우, 추가적인 구성을 활용하여 이를 보완해야 한다.
- 모든 요청은 추가적인 오버헤드를 갖게 되어 간단한 요청에 있어서는 비효율적일 수 있다.

## 동작 과정

1. Client의 HTTP 요청이 Gateway로 들어온다.

2. Gateway Handler Mapping에서 라우팅 기능을 사용하여 이 요청을 어떤 라우트로 보낼지 결정한다.

3. 해당 라우트에 대응하는 WebHandler가 실행된다. 이는 필터 체인을 실행하여 요청 전송, 응답 생성 등의 작업을 수행하도록 한다.

4. 라우트에 정의된 작업과 필터를 완료한 후 서비스에 요청을 보낸다.

5. 서비스는 응답을 생성하고 다시 API Gateway로 보낸다.

6. 같은 방식으로 필터를 거치고 라우팅 되어 Client에게 응답이 돌아간다.

## 적용 방법

1. Dependencies 설정
    - Eureka Discovery Client
    - Lombok
    - Gateway (MVC 제거!!)
    
2. application.yml 설정
    
    ```yaml
    server:
      port: 8000
    
    eureka:
      client:
        register-with-eureka: false
        fetch-registry: false
        service-url:
          defaultZone: http://127.0.0.1:8761/eureka
    
    spring:
      application:
        name: apigateway-service
      cloud:
        gateway:
          routes:
            - id: first-service
              uri: http://localhost:8081/
              predicates:
                - Path=/first-service/**
    
            - id: second-service
              uri: http://localhost:8082/
              predicates:
                - Path=/second-service/**
    ```
    

### TEST
- 127.0.0.1:8081/first-service/welcome ⇒ 정상작동
- 127.0.0.1:8082/second-service/welcome ⇒ 정상작동

# Filter

- Predicate : 요청 정보 판단
- Pre Filter : 처리가 일어나기 전에 수행됨
- Post Filter : 처리가 일어난 후에 수행됨

## 개념

- 디스패처 서블릿에 요청이 전달되기 전/후에 url 패턴에 맞는 모든 요청에 대해 부가작업을 처리할 수 있는 기능 제공
- 작업 전에 수행되는 Pre Filter와 처리 이후 수행되는 Post Filter로 나누어짐

## 적용 방법

### JavaCode

```java
@Configuration
public class FilterConfig {
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/first-service/**")
                        .filters(f -> f.addRequestHeader("first-request", "first-request-header") //Pre Filter
                                .addResponseHeader("first-response", "first-response-header")) //Post Filter
                        .uri("http://localhost:8081"))
                        
                .route(r -> r.path("/second-service/**")
                        .filters(f -> f.addRequestHeader("second-request", "second-request-header")
                                .addResponseHeader("second-response", "second-response-header"))
                        .uri("http://localhost:8082"))
                .build();
    }
}
```

### **application.yml**

```yaml
spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      routes:
        - id: first-service
          uri: http://localhost:8081/
          predicates:
            - Path=/first-service/**
          filters:
            - AddRequestHeader=first-request, first-request-header2 #Pre Filter
            - AddResponseHeader=first-response, first-response-header2 #Post Filter
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/second-service/**
          filters:
            - AddRequestHeader=second-request, second-request-header2
            - AddResponseHeader=second-response, second-response-header2
```

### Test

- first-service
    
    ```java
    @Slf4j
    @RestController
    @RequestMapping("/first-service")
    public class FirstServiceController {
    
        @GetMapping("/welcome")
        public String welcome() {
            return "Welcome to the First service.";
        }
    
        @GetMapping("/message")
        public String message(@RequestHeader("first-request") String header) {
            log.info(header);
            return "Welcome to the Second service.";
        }
    }
    ```
    
- second-service
    
    ```java
    @Slf4j
    @RestController
    @RequestMapping("/second-service")
    public class SecondServiceController {
    
        @GetMapping("/welcome")
        public String welcome() {
            return "Welcome to the Second service.";
        }
    
        @GetMapping("/message")
        public String message(@RequestHeader("second-request") String header) {
            log.info(header);
            return "Welcome to the Second service.";
        }
    }
    ```
    

⇒ 이외에도 Custom Filter, Global Filter를 정의할 수 있으며 filter 실행 우선순위를 정할 수 있다.

# Load Balancer

## 개념

- 서버에 들어오는 요청(트래픽)을 다른 서버들에 적절히 분산하여 해당 서버에 가해지는 부하를 방지하기 위해 사용하는 네트워크 기술

## 서버 증설 방법

### **Scale-Up**

- 현재 서버 자체를 증강시켜 해당 서버가 대량의 트래픽을 관리할 수 있는 처리 능력을 향상시킨다. Scale-Up 방법으로 할 경우, 서버를 증강시키는 시점에 서버가 중지된다.

### **Scale-Out**

- 서버 자체를 증강시키는 것이 아니라, 같은 사양을 가진 장비 또는 비슷한 사양을 가진 장비의 대수를 증가시켜 처리 능력을 향상시키도록 하는 것을 뜻한다. 서버의 대수를 증가시키기에 Load Balancer를 통해 트래픽을 분산시켜주는 작업이 필수적으로 필요하다.또한 서버 대수가 증가하기에 각각의 서버마다 개별적인 도메인이 필요하다.

## 알고리즘

- **Round Robin (RR)** : 이 방법이 가장 기본적인 방법. 서버에 들어온 요청을 순서대로 배분하는 방법이다. 예를 들어서 첫번째 요청은 첫번째 서버에, 두번째 요청은 두번째 서버에, 세번째 요청은 세번째 순서… 이러한 방법으로 분배한다.
- **Weighted Round Robin (WRR)** : 각 서버에 가중치를 매긴다. 이 가중치를 매긴다는 것은 각 서버의 사양이 일정하지 않을 수도 있기에 가중치를 매긴다. 각 서버의 사양이 동일하다면 가중치를 매길 필요가 없지만 A서버와 B서버를 비교했을 때, A서버의 사양이 좋아 더 많은 트래픽을 할당받을 수 있을 경우, A서버에 높은 가중치를 매겨 B서버에는 완만한 트래픽을 분배시켜 부하를 방지할 수 있다.
- **IP Hash** : 클라이언트 IP 주소를 해싱하여 분배시키는 것을 뜻한다. 해싱 함수를 활용하여 임의의 길이를 가진 데이터를 고정된 길이의 데이터로 매핑한 후, 서버에 분배시킨다. 해싱은 고정된 길이의 데이터를 반환하기에 다른 Load Balancer Algorithm과는 다르게 매번 같은 서버에 연결시키는 것을 보장한다.
- **Leact Connection** : 서버에 요청이 들어온 시점에 가장 적은 연결 상태를 가진 서버에 분배하는 방법이다.
- **Least Response Time** : 각 서버의 현재 연결 상태와 응답시간 등을 모두 고려하여 최적의 서버에 분배하는 방법이다.

## 적용 방법

[gateway-service]

1. Dependencies 설정
    - Eureka Discovery Client
    - first-service, second-service 동일하게 설정
2. application.yml 설정
    
    ```yaml
    server:
      port: 8000
    
    eureka:
      client:
        register-with-eureka: true
        fetch-registry: true
        service-url:
          defaultZone: http://localhost:8761/eureka
          
    spring:
      application:
        name: gateway-service
      cloud:
        gateway:
          routes:
            - id : first-service
              uri: lb://MY-FIRST-SERVICE
              predicates:
                - Path=/first-service/**
            - id: second-service
              uri: lb://MY-SECOND-SERVICE
              predicates:
                - Path=/second-service/**
    ```
    

### TEST

- first-service
    - application.yml 설정
        
        ```yaml
        server:
          port: 0 #랜덤 포트 설정
        
        spring:
          application:
            name: my-first-service
        
        eureka:
          client:
            register-with-eureka: true
            fetch-registry: true
            service-url:
              defaultZone: http://localhost:8761/eureka
          instance:
            #Eureka에 0으로 중복되는걸 방지하기 위해 랜덤 포트 id 설정
            instance-id: ${spring.application.name}:${spring.application.instance.id:${random.value}}
        ```
        
- first-service 코드
    
    - 어떤 서비스에 요청을 보내는지 확인 가능
    - Gateway가 RR 방식으로 번갈아가며 호출해 Load Balancer 기능을 수행함

# 참고

[[Spring Cloud Gateway] 스프링 클라우드 게이트웨이란?](https://yoonchang.tistory.com/86)

[[Spring Boot] Spring Cloud Gateway에서 filter 사용하기-MSA(3)](https://velog.io/@korea3611/Spring-Boot-Spring-Cloud-Gateway에서-filter-사용하기-MSA3)

[Load Balancer란?](https://phsun102.tistory.com/93)