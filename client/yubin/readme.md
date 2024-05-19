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
- 오브젝트 파밍 기능 구현
  - RayCase.cs 수정
  - ObjectFarming.cs
  - ProgressBar.cs 수정
    [컴포넌트정의서](https://daily-polo-dee.notion.site/4a68a54ad2614bd3b6985c9415c5a624?v=b414b68538f6438e944f98921aa3daeb&pvs=74)
- 연구소 오브젝트 구현
  - 원소연구소 lv1, lv2, lv3
  - 에너지 연구소 lv1, lv2, lv3
  - 생존 연구소 lv1, lv2, lv3
  - 연구소 토대
- 원소, 화합물 아이콘 제작
- 재배모듈 오브젝트 구현
  - 재배모듈(초기)
  - 재배모듈(배양토)
  - 재배모듈(묘목)
  - 재배모듈(나무)
  - 재배모듈(감자하나)
  - 재배모듈(감자들)

## 24.05.03

- 브로드캐스팅 기능 구현 중
  - NetworkDataReveiver.cs

## 24.05.04

- 로그인 시 TCP 연결 구현
  - AuthControl.cs 수정
  - TCPClientManager.cs 수정
- 유저 정보 저장 기능 구현
  - UserInfoManager.cs
- TCP 메시지 수신 기능 구현
  - NetworkDataReceiver.cs
  - TCPMessageHandler.cs
- 방 생성 기능 구현
  - GameScenePlayerLoader.cs 수정

## 24.05.06

- 대기방 생성 기능 수정
- 대기방 참가 기능 구현
  - AuthControl.cs 수정
  - TCPClientManager.cs 수정
  - TCPMessageHandler.cs 수정
  - CharacterMove.cs 수정
  - NetworkDataReceiver.cs 수정
  - MakeRoomPlayerRenderer.cs
  - JoinRoomManager.cs
  - PlayerInfo.cs

## 24.05.08

- 대기방 생성 로직 구현
  - 대기방 생성 시 플레이어 오브젝트 생성
  - 대기방 생성 시 타 플레이어 오브젝트 생성
- 대기방 내 움직임 브로드캐스팅 기능 구현
- AuthControl.cs 수정
- CameraInfoManager.cs
- JoinRoomManager.cs 수정
- MakeRoomPlayerRenderer.cs 수정
- TCPMessageHandler.cs 수정
- CharacterMove.cs 수정 (CharacterMove2.cs)
- 대기방 중력 적용
- JoinRoomBroadcastPosition.cs
- ObjGravity.cs
- Planet.cs
- TCPMessageHandler.cs 수정
- MakeRoomPlayerRenderer.cs 수정
- - CharacterMove.cs 수정

## 24.05.11

- 대기방 로직 구현
  - 대기방 생성
  - 대기방 참가
  - 대기방 삭제
  - 대기방 나가기
  - 대기방 내 브로드캐스팅
    - 인원 브로드캐스팅
    - 움직임 브로드캐스팅
  - WaitingRoomFirstRender.cs
  - WaitingRoomOtherRender.cs
  - JoinRoomManager.cs 수정
  - TCPMessageHandler.cs 수정
  - AuthControl.cs 수정
  - MakeRoomPlayerRenderer.cs 수정
  - ChannelControl.cs 수정
- 평면 움직임 적용
  - CharacterMove.cs 수정
  - CameraControl.cs 수정
  - CameraInfoManager.cs 수정

## 24.05.12

- 대기방 로직 구현
  - 채팅 구현
  - 프로필 변경 기능 구현
    - 프리팹 수정
    - 플레이어 이미지 추가
    - 서버 정보 로드 방법 변경
    - AuthControl.cs 수정
    - UserInfoManager.cs 수정
    - CharacterMove.cs 수정
    - WaitingRoomFirstRender.cs 수정
    - WaitingRoomOtherRender.cs 수정
    - ServerConfingLoader.cs 수정
    - TCPMessageHandler.cs 수정
    - JoinRoomManager.cs 수정
- 원소연구소
  - 원소 및 화합물 리스트 렌더
    - ChannelConrtrol.cs 수정
    - ElementLabRender.cs 수정
    - ElementList.cs
    - CompoundList.cs
