using System;
using System.Net.Sockets;
using UnityEngine;

public class TCPClientManager : MonoBehaviour
{
    private static TCPClientManager instance;

    private TcpClient client;
    private NetworkStream stream;
    private string serverIp;
    private int serverPort;

    private void Awake()
    {
        // TCPClientManager 싱글톤 인스턴스 설정
        if (instance == null)
        {
            instance = this;
            // 다른 씬으로 이동해도 유지
            DontDestroyOnLoad(gameObject);
        }
        else
        {
            // 이미 인스턴스가 있는 경우 중복 방지를 위해 파괴
            Destroy(gameObject); 
        }
    }

    public static TCPClientManager Instance
    {
        get
        {
            // 인스턴스가 없는 경우 새로 생성
            if (instance == null)
            {
                GameObject obj = new GameObject("TCPClientManager");
                instance = obj.AddComponent<TCPClientManager>();
            }
            return instance;
        }
    }

    public void Init(string ip, int port)
    {
        serverIp = ip;
        serverPort = port;
    }

    public bool Connect()
    {
        try
        {
            client = new TcpClient(serverIp, serverPort);
            stream = client.GetStream();
            Debug.Log("연결 성공!");
            return true;
        }
        catch (Exception e)
        {
            Debug.LogError("연결 실패: " + e.Message);
            return false;
        }
    }

    public void Disconnect()
    {
        if (client != null)
        {
            client.Close();
            Debug.Log("연결 종료");
        }
    }

    public NetworkStream GetStream()
    {
        return stream;
    }

    public void SendTCPRequest(string request)
    {
        try
        {
            byte[] requestData = System.Text.Encoding.UTF8.GetBytes(request);
            stream.Write(requestData, 0, requestData.Length);
        }
        catch (Exception e)
        {
            Debug.LogError("TCP 요청 보내기 중 에러 발생: " + e.Message);
        }
    }
}
