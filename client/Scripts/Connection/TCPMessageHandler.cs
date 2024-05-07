using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Newtonsoft.Json.Linq;
using System;

public static class TCPMessageHandler
{
    private static string teamCode;
    private static JoinRoomManager joinRoomManagerInstance;
    private static AuthControl authControlInstance;

    // HandleReceivedData 메서드에 AuthControl 인스턴스를 전달할 수 있는 메서드 추가
    public static void SetAuthControlInstance(AuthControl authControl)
    {
        authControlInstance = authControl;
    }
    public static void SetJoinRoomManagerInstance(JoinRoomManager joinRoomManager)
    {
        joinRoomManagerInstance = joinRoomManager;
    }

    public static void HandleReceivedData(string response)
    {

        try
        {
            JObject data = JObject.Parse(response);
            string castingType = (string)data["dataHeader"]["castingType"];
            string eventName = (string)data["dataHeader"]["eventName"];

            // 브로드캐스팅
            if (castingType == "BROADCASTING")
            {
                // 움직임 브로드캐스팅
                MovementSync movementSync = GameObject.FindObjectOfType<MovementSync>();
                if (movementSync != null)
                {
                    movementSync.SyncMovement(data);
                }
                else
                {
                    Debug.LogError("MovementSync instance not found.");
                }
                
            }
            // 유니캐스팅
            if (castingType == "UNICASTING")
            {   
                // Debug.Log("유니캐스팅 진입");
                // 방 생성 메시지 수신
                if (eventName == "createWaitingRoom")
                {
                    Debug.Log("teamCode : " + (string)data["dataBody"]["teamCode"]);
                    teamCode = (string)data["dataBody"]["teamCode"];
                    if (authControlInstance != null)
                    {
                        authControlInstance.MakeRoom();
                    }
                    else
                    {
                        Debug.LogError("AuthControl instance is not set.");
                    }
                }
                // 방 입장 허용 메시지 수신
                if(eventName == "joinWaitingRoom")
                {
                    if ((string)data["dataHeader"]["msg"] == "success")
                    {
                        JToken dataBody = data["dataBody"];
                        if (dataBody == null || dataBody.Type == JTokenType.Null)
                        {
                            if((string)data["dataHeader"]["resultCode"] == "EXCEEDING_CAPACITY")
                            {
                                Debug.Log("대기방 인원 초과");
                            }
                            else if((string)data["dataHeader"]["resultCode"] == "EXIST_USER")
                            {
                                Debug.Log("이미 대기방에 존재하는 유저");
                            }
                            else{
                                Debug.Log("dataBody is null");
                            }
                        }
                        else
                        {
                            authControlInstance.EnterRoom();
                            if (joinRoomManagerInstance != null)
                            {
                                joinRoomManagerInstance.CreatePlayer(data); // joinRoomManagerInstance를 사용하여 CreatePlayer 호출
                            }
                            else
                            {
                                Debug.LogError("JoinRoomManager instance is not set.");
                            }
                        }
                    }
                }
            }
            if ( castingType == "MULTICASTING")
            {
                // 방 입장 메시지 수신, 이미 입장해있는 유저에 새로 입장한 유저 정보 전송
                if( eventName == "joinWatingRoom")
                {
                    if((string)data["dataHeader"]["msg"] == "success")
                    {
                        if (joinRoomManagerInstance != null)
                        {   
                            // 입장한 유저 정보 수신, 객체 생성
                            joinRoomManagerInstance.CreatePlayer(data);
                            // 현재 내 위치 정보 전송

                        }
                        else
                        {
                            Debug.LogError("JoinRoomManager instance is not set.");
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

    public static string GetTeamCode()
    {
        return teamCode;
    }
    public static void DeleteTeamCode()
    {
        teamCode = null;
    }
}
