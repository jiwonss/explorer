using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Newtonsoft.Json.Linq; // Newtonsoft.Json�� JObject�� ����ϱ� ���� �߰�

public class NetworkDataReceiver : MonoBehaviour
{
    public TCPClientManager tcpClientManager;
    public Transform playerTransform;

    void Start()
    {
        tcpClientManager = TCPClientManager.Instance;
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager ����");
        }
    }

    void Update()
    {
        if (tcpClientManager == null || !tcpClientManager.isConnected)
        {
            Debug.LogError("TCPClientManager ������� �ʾҰų� ��ȿ���� ����");
            return;
        }

        // �����κ��� ������ ����
        string receivedData = tcpClientManager.ReceiveTCPResponse();
        if (receivedData != null)
        {
            Debug.Log("Data Received");
            Debug.Log(receivedData);
        }
        

        Vector3 newPosition = ExtractPositionFromData(receivedData);
        Quaternion newRotation = ExtractRotationFromData(receivedData);

        // �÷��̾� ������Ʈ
        playerTransform.position = newPosition;
        playerTransform.rotation = newRotation;
    }

    // ��ġ ���� ����
    private Vector3 ExtractPositionFromData(string jsonData)
    {
        JObject data = JObject.Parse(jsonData); // Newtonsoft.Json�� JObject ���

        float posX = (float)data["posX"];
        float posY = (float)data["posY"];
        float posZ = (float)data["posZ"];

        return new Vector3(posX, posY, posZ);
    }

    // ȸ�� ���� ����
    private Quaternion ExtractRotationFromData(string jsonData)
    {
        JObject data = JObject.Parse(jsonData); // Newtonsoft.Json�� JObject ���

        float rotX = (float)data["rotX"];
        float rotY = (float)data["rotY"];
        float rotZ = (float)data["rotZ"];

        return Quaternion.Euler(rotX, rotY, rotZ);
    }
}
