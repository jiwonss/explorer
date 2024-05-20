# 도커란?

- 리눅스 컨테이너에 리눅스 애플리케이션을 프로세스 격리기술을 사용하여 더 쉽게 컨테이너로 실행하고 관리할 수 있게 해주는 오픈소스 프로젝트
    - = Linux 컨테이너를 기반으로 하여 특정한 서비스(애플리케이션)을 패키징하고 배포하는데 유용한 프로그램
    - = 애플리케이션을 **패키징**할 수 있는 도구

# 컨테이너란?

- 실행의 독립성을 확보해주는 운영체계 수준의 격리 기술
    - 애플리케이션이 컴퓨팅 환경 간에 신속하고 신뢰성 있게 실행될 수 있도록 코드와 그 모든 종속성 있는 것들을 패키징하는 소프트웨어의 표준 단위
    - 애플리케이션을 구동하는데 필요한 모든 것들을 담음

## 컨테이너와 가상머신(VM)의 차이

![Untitled](https://github.com/RIN-1011/RIN-1011/assets/60701386/9bbe118b-925d-40dd-acd3-2f83327dc068)

### 가상머신

- 하이퍼바이저를 이용해 여러개의 운영체제를 하나의 호스트에서 생성해서 사용하는 방식
- 각종 시스템의 자원을 가상화하고 독립된 공간을 생성하는 작업은 반드시 하이퍼바이저를 거치기 때문에 일반 호스트에 비해 성능 손실 발생
    - 하이퍼바이저에 의해 생성되고 관리되는 운영체제 = 게스트 운영체제
- 게스트 운영체제를 사용하기 위한 라이브러리, 커널 등을 전부 포함하여 이미지 크기 커짐

### 컨테이너

- 가상화된 공간을 생성할 때 리눅스 자체 기능을 사용하여 프로세스 단위의 격리 환경을 만드므로 성능 손실 없음
- 가상머신과 달리 커널을 공유해서 사용하므로 컨테이너에는 라이브러리 및 실행파일만 존재하여 이미지 용량이 작음
    - 배포 시간 빠름
    - 성능 손실 거의 없음

### 차이점

- VM : 운영체제 포함
- 컨테이너 : 운영체제 미포함

# 아키텍처

![Untitled (1)](https://github.com/RIN-1011/RIN-1011/assets/60701386/1fd01721-8d8b-467d-813a-87db8f9a1ee9)

## Docker 데몬

- Docker API 요청수신, 이미지, 컨테이너, 네트워크와 같은 도커 객체 및 도커 서비스 관리

## Docker 클라이언트

- Docker 사용자가 Docker와 상호작용하기 위한 방법. 기본적인 도커 명령어를 통해서 Docker 데몬과 통신

## **Docker 객체**

- 도커 이미지: 도커 이미지는 컨테이너 실행에 필요한 파일과 설정 값 등을 포함하고 있다.
- 컨테이너 : 컨테이너는 도커 이미지의 실행 가능한 인스턴스이다.

# 도커 이미지

- 동작하고 있는 애플리케이션을 스냅샷하여 템플릿 형태로 이미지 제작
- 이미지를 통해 실제로 애플리케이션이 동작하는 컨테이너 생성 가능
- 도커 이미지는 컨테이너 실행에 필요한 파일과 설정 값 등을 포함하고 있다.
    - 용량이 크다.
- 이미지는 불변 (스냅샷 당시의 프로젝트 상태)
- 컨테이너에 따른 상태 값이 변하지 않으므로 라이브러리의 버전이 의도치 않게 바뀜에 따른 의존성 문제가 발생하지 않는다.

## Docker Layer

![Untitled (2)](https://github.com/RIN-1011/RIN-1011/assets/60701386/fe34b199-1dc3-4d4b-8162-8270d3bb0a0d)

- 도커의 이미지가 변할 때마다 새롭게 다운 받는다고 가정할 때 큰 이미지를 다시 다운로드 받는 것은 매우 비효율적인 방법임
    - 이를 해결하기 위해 Layer라는 개념을 이용
- (그림) 만약 ubuntu 이미지가 기존에 존재하는데 nginx 이미지를 다운 받을 경우 nginx 레이어만 다운받게 됨
- Docker 이미지는 위 그림처럼 여러 레이어로 구성되며,각 레이어는 이전 레이어의 변경 사항을 가지고 있음
    - 여러 개의 레이어에는 읽기 전용인 read only 레이어와 새로 변경되거나 추가된 내용을 담은 새로운 레이어로 구성됨
- 레이어 개념을 통해 기존 레이어는 그대로 둔 채 새로 업데이트된 내용만 담고 있는 레이어만 쌓는 개념으로 관리를 하기 때문에 효율적임
- 도커 컨테이너를 생성할 때도 레이어 방식을 사용함
    - 컨테이너 생성 시, 기존의 읽기 전용 이미지 레이어 위에 읽기/쓰기 전용 레이어(R/W layer) 를 추가

# 과정

![Untitled (3)](https://github.com/RIN-1011/RIN-1011/assets/60701386/75302bd0-8f49-4445-807c-725e51026088)

1. 사용자는 Local에 Docker를 설치한 후 Dockerfile을 만든다.
2. Dockerfile을 build 시켜 Image로 만든다(스냅샷).
3. 만든 Image를 Container Registry(git)에 Push 한다.
4. 실서버에 Docker를 설치 한 후 Container Registry에서 Image를 Pull 한다.
5. Docker에서 Image를 run 한다.

# 명령어

## 설치

1. Docker's apt repository 설정

```powershell
# Add Docker's official GPG key:
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the r0epository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
```

1. Docker 패키지 설치

```powershell
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

1. Docker 엔진 설치 성공 테스트

```powershell
sudo docker run hello-world
```

---

- **`sudo apt-get update`** : 시스템 패키지 목록 업데이트
- **`sudo apt-get install ca-certificates curl`** : `ca-certificates`와 `curl` 패키지 설치
    - `ca-certificates` : SSL/TLS 연결을 인증하기 위한 인증서 관리
    - `curl` : URL을 통해 데이터를 전송하는 도구
- **`sudo install -m 0755 -d /etc/apt/keyrings`** : `/etc/apt/keyrings` 디렉토리 생성 및 권한 설정
    - 해당 디렉토리는 Apt 패키지 관리자가 사용하는 키링(키 저장소)을 저장하기 위한 곳
- **`sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc`** : Docker의 공식 GPG 키를 다운로드하고 `/etc/apt/keyrings/docker.asc` 파일에 저장
- **`echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null`** : Docker의 공식 저장소를 Apt 소스에 추가하는데 사용됨
    - 저장소의 URL과 버전에 대한 정보 포함

- 공식 문서 참고
    - https://docs.docker.com/engine/install/ubuntu/

# 참고

[[Docker] 도커란 무엇인가? 도커 한방 정리!](https://be-developer.tistory.com/18)

[컨테이너 및 도커 개념정리](https://velog.io/@geunwoobaek/컨테이너-및-도커-개념정리)

[[Docker] 도커란? - 도커 개념 정리](https://seosh817.tistory.com/345)

[[Docker] 도커의 레이어(Layer)에 대해 알아보자.](https://hstory0208.tistory.com/entry/Docker-도커의-레이어Layer에-대해-알아보자)
