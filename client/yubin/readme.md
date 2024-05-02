# Work log

## 24.04.23

- Plastic SCM 학습 및 프로젝트 설정
  - [Plastic SCM](https://daily-polo-dee.notion.site/Plastic-SCM-8b2be6e862a14983a8b1704397e89958)
- 플레이어 이동 구현
  - CharacterMove.cs

## 24.04.24

- 플레이어 이동 대시 기능 구현
  - CharacterMove.cs
- 플레이어 화면 이동 기능 구현
  - MouseMove.cs
- 플레이어 레이캐스트 기능 구현
  - Raycast.cs

## 24.04.25

- 프로그래스 바 구현
  - ProgressBar.cs
- 타이머 구현
  - Timer.cs

## 24.04.26

- 에너지 연구소 UI 구현
  - 연구실
    - 연구 설명 페이지
    - 시설 연구 테크트리 페이지
    - 에너지 연구 테크트리 페이지
  - 저장소
    - 연구소 인벤토리 페이지

## 24.04.27

- 에너지 연구소 페이지 전환 구현
  - EnergyLabRender.cs
- 생존 연구소 UI 구현
- 생존 연구소 페이지 전환 구현
  - SurvivalLabRender.cs

## 24.04.28

- 원소연구소 페이지 구현
  - 원소연구소 UI 구현
  - 원소연구소 페이지 전환 구현
    - ElementLabRender.cs

## 24.04.29

- 플레이어 이동 정보 TCP 통신 구현
  - 서버 연결 정보 config 파일 작성 및 연결
    - ServerConfigLoader.cs
  - TCP 연결 및 연결 확인 스크립트 작성
    - TCPClientManager.cs
  - 이동 시 TCP 서버로 전송 기능 적용
    - Charactermove.cs 수정

## 24.04.30

- 레이캐스트 방식 수정
  - RayCast.cs
- TCP 연결 방식 수정
  - TCPClientManager.cs
- 플레이어 인게임 이동 TCP 통신 구현
  - CharacterMove.cs
- 플레이어 인게임 드롭 아이템 획득 TCP 통신 구현
  - GetMapItem.cs
- 원소 추출, 합성, 분해 TCP 통신 구현
  - ElementLabRender.cs

## 24.05.01

- 우주선 페이지 연결 구현
  - SpaceShipPageRender.cs
  - SpaceShipPageStart.cs
  - RayCast.cs 기능 추가

## 24.05.02

- 우주선 탐사 이벤트 기능 구현
  - 카운트 다운 기능 적용
  - SpaceshipPageRender.cs 수정
  - Countdown.cs 수정
