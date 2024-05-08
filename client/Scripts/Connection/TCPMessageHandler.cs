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

    // HandleReceivedData 메서드에 AuthControl 인스턴스를 전달할 수 있는 메서드 추가
    public static void SetAuthControlInstance(AuthControl authControl)
    {
        authControlInstance = authControl;
    }

    public static void HandleReceivedData(string response)
    {

        try
        {
            string[] jsonObjects = response.Split(new[] { '\r', '\n' }, StringSplitOptions.RemoveEmptyEntries);
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
                    if(eventName == "move")
                    {
                        GameObject.FindObjectOfType<JoinRoomBroadcastPosition>().SyncPosition(data);
                    }

                }
                // 유니캐스팅
                if (castingType == "UNICASTING")
                {
                    // Debug.Log("유니캐스팅 진입");
                    // 방 생성 메시지 수신
                    if (eventName == "createWaitingRoom")
                    {
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
                                SceneManager.LoadScene("WaitingRoom");
                                GameObject.FindObjectOfType<JoinRoomManager>().ReceiveData(data);
                            }
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

    public static void SetTeamCode(string code)
    {
        teamCode = code;
        Debug.Log("Team code set to: " + teamCode);
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