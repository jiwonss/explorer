using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Newtonsoft.Json.Linq; // Newtonsoft.Json의 JObject를 사용하기 위해 추가

public class NetworkDataReceiver : MonoBehaviour
{
    public TCPClientManager tcpClientManager;
    public Transform playerTransform;

    void Start()
    {
        tcpClientManager = TCPClientManager.Instance;
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager 없음");
        }
    }

    void Update()
    {
        if (tcpClientManager == null || !tcpClientManager.isConnected)
        {
            Debug.LogError("TCPClientManager 연결되지 않았거나 유효하지 않음");
            return;
        }

        // 서버로부터 데이터 수신
        string receivedData = tcpClientManager.ReceiveTCPResponse();
        if (receivedData != null)
        {
            Debug.Log("Data Received");
            Debug.Log(receivedData);
        }
        

        Vector3 newPosition = ExtractPositionFromData(receivedData);
        Quaternion newRotation = ExtractRotationFromData(receivedData);

        // 플레이어 업데이트
        playerTransform.position = newPosition;
        playerTransform.rotation = newRotation;
    }

    // 위치 정보 추출
    private Vector3 ExtractPositionFromData(string jsonData)
    {
        JObject data = JObject.Parse(jsonData); // Newtonsoft.Json의 JObject 사용

        float posX = (float)data["posX"];
        float posY = (float)data["posY"];
        float posZ = (float)data["posZ"];

        return new Vector3(posX, posY, posZ);
    }

    // 회전 정보 추출
    private Quaternion ExtractRotationFromData(string jsonData)
    {
        JObject data = JObject.Parse(jsonData); // Newtonsoft.Json의 JObject 사용

        float rotX = (float)data["rotX"];
        float rotY = (float)data["rotY"];
        float rotZ = (float)data["rotZ"];

        return Quaternion.Euler(rotX, rotY, rotZ);
    }
}
