using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Net.Sockets;
using System.IO; 
using Newtonsoft.Json;

// 아이템 정보를 담을 클래스
public class MapItemData
{
    public string type;
    public string eventType;
    public int mapId;
    public int channelId;
    public int userId;
    public float posX;
    public float posY;
    public float posZ;
    public float rotX;
    public float rotY;
    public float rotZ;

    // 생성자
    public MapItemData(string type, string eventType, int mapId, int channelId, int userId, float posX, float posY, float posZ, float rotX, float rotY, float rotZ)
    {
        this.type = type;
        this.eventType = eventType;
        this.mapId = mapId;
        this.channelId = channelId;
        this.userId = userId;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
    }
}

public class GetMapItem : MonoBehaviour
{
    private TCPClientManager tcpClientManager;

    public void Awake()
    {
        tcpClientManager = TCPClientManager.Instance;
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager가 초기화되지 않았습니다.");
            return;
        }
        else
        {
            Debug.Log("초기화는 되어있습니다..");
        }

        
        
    }

    // GetItem 메서드
    public void GetItem(Vector3 position, Quaternion rotation)
    {
        // TCPClientManager가 설정되지 않았으면 오류 출력
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
            return;
        }

        // TCPClientManager의 GetStream 메서드를 사용하여 NetworkStream 가져오기
        NetworkStream stream = tcpClientManager.GetStream();
        if (stream == null)
        {
            Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
            return;
        }

        // JSON 생성
        MapItemData itemData = new MapItemData("ingame", "getItemFromMap", 0, 1, 1, position.x, position.y, position.z, rotation.eulerAngles.x, rotation.eulerAngles.y, rotation.eulerAngles.z);
        string json = JsonConvert.SerializeObject(itemData);

        // TCPClientManager의 SendTCPRequest 메서드를 사용하여 JSON 데이터 전송
        tcpClientManager.SendTCPRequest(json);
    }
}
