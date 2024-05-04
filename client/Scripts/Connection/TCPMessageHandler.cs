using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Newtonsoft.Json.Linq;
using System;

public static class TCPMessageHandler
{
    private static string teamCode;

    // AuthControl 인스턴스를 저장할 변수
    private static AuthControl authControlInstance;

    // HandleReceivedData 메서드에 AuthControl 인스턴스를 전달할 수 있는 메서드 추가
    public static void SetAuthControlInstance(AuthControl authControl)
    {
        authControlInstance = authControl;
    }

    public static void HandleReceivedData(string response)
    {
        Debug.Log(response);

        try
        {
            JObject data = JObject.Parse(response);

            string castingType = (string)data["dataHeader"]["castingType"];
            string eventName = (string)data["dataHeader"]["eventName"];

            // 브로드캐스팅
            if (castingType == "BROADCASTING")
            {
                // 플레이어 움직임 메시지 수신
                if (eventName == "moving")
                {
                    Debug.Log("move");
                }
            }
            // 유니캐스팅
            if (castingType == "UNICASTING")
            {   Debug.Log("유니캐스팅 진입");
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
                // 방 입장 메시지 수신
                if(eventName == "joinWaitingRoom")
                {
                    //  어떡하죠..?
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
