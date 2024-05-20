using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Newtonsoft.Json.Linq;
using UnityEngine.SceneManagement;
using System;


public static class TCPMessageHandler
{
    private static string teamCode;
    private static AuthControl authControlInstance;
    private static ChannelControl channelControlInstance;
    public static WaitingRoomFirstRender waitingRoomFirstRender;
    public static WaitingRoomOtherRender waitingRoomOtherRender;
    private static IngameRender ingameRenderInstance;
    private static IngamePlayerRenderer ingamePlayerRendererInstance;
    private static IngameChat ingameChatInstance;



    //오브젝트 생성 파괴 
    //private static CreateObjectBroadCast createObjectBroadCast;
    private static DestroyObjectBroadcast destroyObjectBroadcast;

    // HandleReceivedData 메서드에 AuthControl 인스턴스를 전달할 수 있는 메서드 추가
    public static void SetAuthControlInstance(AuthControl authControl)
    {
        authControlInstance = authControl;
    }

    public static void SetDestroyObjectBroadcastInstance(DestroyObjectBroadcast instance)
    {
        destroyObjectBroadcast = instance;
    }

    public static void SetChannelControlInstance(ChannelControl channelControl)
    {
        channelControlInstance = channelControl;
    }
    public static void SetWaitingRoomFirstRender(WaitingRoomFirstRender waitingRoomFirstRender)
    {
        TCPMessageHandler.waitingRoomFirstRender = waitingRoomFirstRender; 
        if (waitingRoomFirstRender == null)
        {
            Debug.LogError("waitingRoomFirstRender was called with a null reference");
        }
        else
        {
            // Debug.Log("waitingRoomFirstRender was set successfully");
        }
    }

    public static void SetWaitingRoomOtherRender(WaitingRoomOtherRender waitingRoomOtherRender)
    {
        TCPMessageHandler.waitingRoomOtherRender = waitingRoomOtherRender; 
        if (waitingRoomOtherRender == null)
        {
            Debug.LogError("SetWaitingRoomOtherRender was called with a null reference");
        }
        else
        {
            // Debug.Log("SetWaitingRoomOtherRender was set successfully");
        }
    }
    public static void SetIngameRenderInstance(IngameRender ingameRender)
    {
        ingameRenderInstance = ingameRender;
    }
    public static void SetIngamePlayerRendererInstance(IngamePlayerRenderer ingamePlayerRenderer)
    {
        ingamePlayerRendererInstance = ingamePlayerRenderer;
    }
    public static void SetIngameChatInstance(IngameChat ingameChat)
    {
        ingameChatInstance = ingameChat;
    }

    public static void HandleReceivedData(string response)
    {

        try
        {
            Debug.Log(response);
            string[] jsonObjects = response.Split(new[] { '\n' }, StringSplitOptions.RemoveEmptyEntries);
            foreach (string obj in jsonObjects)
            {
                JObject data = JObject.Parse(obj);
                string castingType = (string)data["dataHeader"]["castingType"];
                string eventName = (string)data["dataHeader"]["eventName"];

                // 브로드캐스팅
                if (castingType == "BROADCASTING")
                {
                    // 대기방 초기 위치 브로드캐스팅
                    if (eventName == "broadcastPosition")
                    {
                        GameObject.FindObjectOfType<JoinRoomBroadcastPosition>().SyncPosition(data);
                    }
                    // 움직임 브로드캐스팅
                    if (eventName == "move")
                    {
                        GameObject.FindObjectOfType<JoinRoomBroadcastPosition>().SyncPosition(data);
                    }
                    // 대기방 삭제 브로드캐스팅
                    if (eventName == "deleteWaitingRoom")
                    {
                        authControlInstance.DeleteRoom();
                    }
                    // 대기방 나가기 브로드캐스팅
                    if (eventName == "leaveWaitingRoom")
                    {
                        UserInfoManager userInfoManager = UserInfoManager.Instance;
                        int nowUserId = userInfoManager.GetUserId();
                        int userId = (int)data["dataBody"]["userId"];
                        if (nowUserId != userId)
                        {
                            // 해당 유저 삭제
                            if (waitingRoomOtherRender != null)
                            {
                                waitingRoomOtherRender.ExitUserReceive(userId);
                            }
                            else if (waitingRoomFirstRender != null)
                            {
                                waitingRoomFirstRender.ExitUserReceive(userId);
                            }

                        }

                    }

                    // 대기방 인원 업데이트 브로드캐스팅
                    if (eventName == "getWaitingRoomHeadcount")
                    {
                        Debug.Log("인원 업데이트 브로드캐스팅");
                        string value = (string)data["dataBody"]["headcount"];
                        // 대기방 인원 업데이트
                        if (waitingRoomOtherRender != null)
                        {
                            waitingRoomOtherRender.UpdateCountValue(value);
                        }
                        else if (waitingRoomFirstRender != null)
                        {
                            waitingRoomFirstRender.UpdateCountValue(value);
                        }
                        else
                        {
                            Debug.Log("waitingRoomRender 인스턴스를 찾을 수 없습니다.");
                        }
                    }
                    // 채팅 브로드캐스팅
                    if (eventName == "sendChat")
                    {
                        string nickname = (string)data["dataBody"]["nickname"];
                        string content = (string)data["dataBody"]["content"];

                        if (ingameChatInstance != null)
                        {
                            ingameChatInstance.RenderChat(nickname, content);
                        }
                        else
                        {
                            Debug.Log("ingameChatInstance == null");
                        }
                        
                    }
                    // 게임 시작 브로드캐스팅
                    if (eventName == "startGame")
                    {
                        string channelId = (string)data["dataBody"]["channelId"];
                        ChannelManager.Instance.SetChannelId(channelId);
                        ChannelManager.Instance.DeleteTeamCode();
                        if (waitingRoomOtherRender != null)
                        {
                            // 메인맵 씬전환
                            waitingRoomOtherRender.StartGameResponse();
                        }
                        else if (waitingRoomFirstRender != null)
                        {
                            // 메인맵 씬전환
                            waitingRoomFirstRender.StartGameResponse();
                        }
                    }
                    // 메인 맵 정보 수신 브로드캐스팅
                    if (eventName == "mainMapInfo")
                    {
                        if ((string)data["dataHeader"]["msg"] == "success")
                        {
                            // 맵 아이디 설정
                            ChannelManager.Instance.SetMapId(0);
                            // 캐릭터 생성 및 시작 위치 설정
                            // 유저 위치, 인벤토리 유니캐스팅 요청
                            ingameRenderInstance.GetInitInfoRequest();
                        }
                    }
                    // 재시작- 원래 있던 유저 위치 정보 브로드캐스팅
                    if (eventName == "broadcastPosition")
                    {
                        GameObject.FindObjectOfType<JoinRoomBroadcastPosition>().SyncPosition(data);
                    }
                    // 인게임 타 유저 나가기 브로드캐스팅
                    if (eventName == "leaveGame")
                    {
                        GameObject.FindObjectOfType<IngameRender>().DeleteOtherPlayer(data);
                    }
                    // 채팅 연결 성공
                    if (eventName == "joinChattingRoom")
                    {
                        if ((string)data["dataHeader"]["msg"] == "success")
                        {
                            Debug.Log("채팅 연결 성공");
                            string nickname = (string)data["dataBody"]["nickname"];
                            string content = (string)data["dataBody"]["content"];
                            Debug.Log("content : " + nickname + " : " + content);
                            authControlInstance.EnterChattingRoom(nickname, content);

                            Debug.Log("채팅 연결 성공");
                        }
                    }
                    // 파밍 브로드캐스팅
                    //if (eventName == "farm" && destroyObjectBroadcast != null)
                    if (eventName == "farm")
                    {

                        Debug.Log("Received farm event: " + data.ToString());
                        GameObject.FindObjectOfType<DestroyObjectBroadcast>().DestroyObject(data);
                        
                    }

                }
                // 유니캐스팅
                if (castingType == "UNICASTING")
                {
                    // Debug.Log("유니캐스팅 진입");
                    // 채널 목록 조회 메시지 수신
                    if (eventName == "getChannelList")
                    {
                        // 채널 목록 조회
                        channelControlInstance.SetChannelList(data);
                        channelControlInstance.SetProfile();
                    }
                    // 채널 상세 조회 메시지 수신
                    if (eventName == "getChannelDetails")
                    {
                        channelControlInstance.SetChannelDetail(data);
                    }

                    // 방 생성 메시지 수신
                    if (eventName == "createWaitingRoom")
                    {
                        teamCode = (string)data["dataBody"]["teamCode"];
                        ChannelManager.Instance.SetTeamCode(teamCode);
                        channelControlInstance.MakeRoom();

                    }
                    // 방 입장 허용 메시지 수신
                    if (eventName == "joinWaitingRoom")
                    {
                        if ((string)data["dataHeader"]["msg"] == "success")
                        {
                            JToken dataBody = data["dataBody"];
                            if (dataBody == null || dataBody.Type == JTokenType.Null)
                            {
                                if ((string)data["dataHeader"]["resultCode"] == "EXCEEDING_CAPACITY")
                                {
                                    Debug.Log("대기방 인원 초과");
                                }
                                else if ((string)data["dataHeader"]["resultCode"] == "EXIST_USER")
                                {
                                    Debug.Log("이미 대기방에 존재하는 유저");
                                }
                                else
                                {
                                    Debug.Log("dataBody is null");
                                }
                            }
                            else
                            {
                                channelControlInstance.EnterRoom();
                                GameObject.FindObjectOfType<JoinRoomManager>().ReceiveData(data);
                            }
                        }
                    }
                    // 연구소 입장 메시지 답신 수신
                    if (eventName == "enterLab")
                    {
                        if ((string)data["dataHeader"]["msg"] == "success")
                        {
                            // 연구소 입장 허용
                            ingameRenderInstance.EnterLabPage(data);
                        }
                        else
                        {
                            // 입장 실패
                            Debug.Log("이미 다른 유저가 이용중입니다");
                        }
                    }
                    // 연구소 퇴장 메시지 답신 수신
                    if (eventName == "leaveLab")
                    {
                        if ((string)data["dataHeader"]["msg"] == "success")
                        {
                            // 나가기 성공, 씬 전환
                            GameObject.FindObjectOfType<ElementLabRender>().LeaveLabPage();
                        }
                    }
                    // 연구소 업그레이드 수신
                    if (eventName == "upgrade")
                    {
                        GameObject.FindObjectOfType<ElementLabRender>().UpgradeLabResponse(data);
                    }
                    // 추출 이벤트 수신
                    if (eventName == "extracting")
                    {
                        GameObject.FindObjectOfType<ElementLabRender>().ExtractingResponse(data);
                    }
                    // 합성 이벤트 수신
                    if (eventName == "synthesizing")
                    {
                        GameObject.FindObjectOfType<ElementLabRender>().SynthesizingResponse(data);
                    }
                    // 게임시작- 유저 위치, 인벤토리 이벤트 수신
                    if (eventName == "userInventory")
                    {
                        ingameRenderInstance.SetInitUserInfo(data);
                    }
                    // 게임 시작 시 시작 위치 유니캐스팅
                    if (eventName == "getUserPosition")
                    {
                        // 타 플레이어 위치 설정
                        ingameRenderInstance.SetInitPosition(data);
                        // 본인 플레이어 위치 설정
                        ingamePlayerRendererInstance.SetInitPosition(data);
                    }
                    // 채널 재입장 이벤트 수신 - 맵 정보 (재입장 유저)
                    if (eventName == "restartGame")
                    {
                        channelControlInstance.AllowedGameRestart(data);
                    }
                    // 재입장- 이미 입장해있는 유저들 정보 수신
                    if (eventName == "ingameUserInfo")
                    {
                        ingameRenderInstance.ReceiveIngameUserInfo(data);
                    }
                    // 재입장- 본인 리스폰 위치 정보 수신
                    if (eventName == "newUserInfo")
                    {
                        ingamePlayerRendererInstance.SetInitPositionRestart(data);
                    }
                    // 채널 삭제 성공 메시지 수신
                    if (eventName == "deleteChannel")
                    {
                        if ((string)data["dataHeader"]["msg"] == "success")
                        {
                            Debug.Log("채널 삭제 성공");
                            // 채널 상세조회 메시지 송신
                            channelControlInstance.GetChannelList();
                        }
                    }

                }
                if (castingType == "MULTICASTING")
                {
                    // 방 입장 메시지 수신, 이미 입장해있는 유저에 새로 입장한 유저 정보 전송
                    if (eventName == "joinWaitingRoom")
                    {
                        if ((string)data["dataHeader"]["msg"] == "success")
                        {
                            GameObject.FindObjectOfType<JoinRoomManager>().ReceiveData(data);
                        }
                    }
                    // 채널 입장 메시지 수신, 이미 입장해있는 유저에 새로 입장한 유저 정보 전송
                    if (eventName == "newUserInfo")
                    {
                        ingameRenderInstance.SomeoneIsComming(data);
                    }
                }
            }
        }

        catch (Exception e)
        {
            Debug.LogError("Error parsing JSON: " + e.Message);
        }
    }


}