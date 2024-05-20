using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Newtonsoft.Json.Linq;

public class JoinRoomBroadcastPosition : MonoBehaviour
{
    public void Start()
    {
    }
    public void SyncPosition(JObject data)
    {
        int userId = UserInfoManager.Instance.GetUserId();
        int targetUserId = (int)data["dataBody"]["userId"];
        // 태그가 "Player"인 오브젝트를 찾음
        GameObject[] players = GameObject.FindGameObjectsWithTag("Player");
        foreach (GameObject playerObject in players)
        {
            if(userId != targetUserId)
            {
                PlayerInfo playerInfo = playerObject.GetComponent<PlayerInfo>();
                if (playerInfo != null && playerInfo.userId == targetUserId)
                {
                    string positionString = (string)data["dataBody"]["position"];
                    string[] parts = positionString.Split(':');

                    // Vector3 위치 값으로 변환
                    Vector3 newPosition = new Vector3(
                        float.Parse(parts[0]),
                        float.Parse(parts[1]),
                        float.Parse(parts[2])
                    );

                    // Quaternion 회전 값으로 변환
                    Quaternion newRotation = Quaternion.Euler(
                    float.Parse(parts[3]),
                    float.Parse(parts[4]),
                    float.Parse(parts[5])
                    );

                    // 플레이어 오브젝트의 위치와 회전을 업데이트
                    playerObject.transform.position = newPosition;
                    playerObject.transform.rotation = newRotation;

                    // 찾았으면 더 이상의 검색은 중지
                    return;
                }
            } 
            
        }

    }
}
