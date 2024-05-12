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

    // HandleReceivedData 메서드에 AuthControl 인스턴스를 전달할 수 있는 메서드 추가
    public static void SetAuthControlInstance(AuthControl authControl)
    {
        authControlInstance = authControl;
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
        Debug.Log("waitingRoomFirstRender was set successfully");
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
        Debug.Log("SetWaitingRoomOtherRender was set successfully");
    }
}

    public static void HandleReceivedData(string response)
    {

        try
        {
            Debug.Log("response :" + response);
            string[] jsonObjects = response.Split(new[] { '\n' }, StringSplitOptions.RemoveEmptyEntries);
            foreach (string obj in jsonObjects)
            {
                JObject data = JObject.Parse(obj);
                Debug.Log("data :" + data);
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
                    if(eventName == "move")
                    {
                        GameObject.FindObjectOfType<JoinRoomBroadcastPosition>().SyncPosition(data);
                    }
                    // 대기방 삭제 브로드캐스팅
                    if(eventName == "deleteWaitingRoom")
                    {
                        authControlInstance.DeleteRoom();
                    }
                    // 대기방 나가기 브로드캐스팅
                    if(eventName == "leaveWaitingRoom")
                    {
                        UserInfoManager userInfoManager = UserInfoManager.Instance;
                        int nowUserId = userInfoManager.GetUserId();
                        int userId = (int)data["dataBody"]["userId"];
                        if(nowUserId != userId)
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
                if( eventName == "chat")
                {
                    string nickname = (string)data["dataBody"]["nickname"];
                    string content = (string)data["dataBody"]["content"];
                    if (waitingRoomOtherRender != null)
                    {
                        waitingRoomOtherRender.RenderChat(nickname, content);
                    }
                    else if (waitingRoomFirstRender != null)
                    {
                        waitingRoomFirstRender.RenderChat(nickname, content);
                    }
                }

                }
                // 유니캐스팅
                if (castingType == "UNICASTING")
                {
                    // Debug.Log("유니캐스팅 진입");
                    // 채널 목록 조회 메시지 수신
                    if(eventName == "getChannelList")
                    {
                        // 채널 목록 조회
                        channelControlInstance.SetChannelList(data);
                        channelControlInstance.SetProfile();
                    }

                    // 방 생성 메시지 수신
                    if (eventName == "createWaitingRoom")
                    {
                        teamCode = (string)data["dataBody"]["teamCode"];
                        if (channelControlInstance != null)
                        {
                            ChannelManager instance = ChannelManager.Instance;
                            ChannelManager.instance.SetTeamCode(teamCode);
                            channelControlInstance.MakeRoom();
                        }
                        else
                        {
                            Debug.LogError("AuthControl instance is not set.");
                        }
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
                    // 채팅 연결 실패
                    if ( eventName == "channelIn")
                    {
                        if ((string)data["dataHeader"]["msg"] == "fail")
                        {
                            Debug.LogError("채팅 연결 실패");
                        }
                    }
                }
                if (castingType == "MULTICASTING")
                {
                    // 방 입장 메시지 수신, 이미 입장해있는 유저에 새로 입장한 유저 정보 전송
                    if (eventName == "joinWaitingRoom")
                    {
                        Debug.Log("Event Name: " + eventName);
                        if ((string)data["dataHeader"]["msg"] == "success")
                        {
                            GameObject.FindObjectOfType<JoinRoomManager>().ReceiveData(data);
                        }
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