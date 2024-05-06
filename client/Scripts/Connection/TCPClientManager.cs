using System;
using System.Collections;
using System.Net.Sockets;
using UnityEngine;

public class TCPClientManager : MonoBehaviour
{
    private static TCPClientManager instance;

    private TcpClient client;
    private NetworkStream stream;
    private string serverIp;
    private int serverPort;

    private JoinRoomManager joinRoomManager;

    public bool isConnected { get; private set; } = false;

    public bool IsConnected
    {
        get { return isConnected; }
    }

    private bool receiving = false;

    private void Awake()
    {
        if (instance == null)
        {
            instance = this;
            DontDestroyOnLoad(gameObject);
        }
        else
        {
            Destroy(gameObject); 
        }
    }

    private void Start()
    {
        // TCPMessageHandler 클래스에 AuthControl 인스턴스 설정
        AuthControl authControl = FindObjectOfType<AuthControl>();
        if (authControl != null)
        {
            TCPMessageHandler.SetAuthControlInstance(authControl);
        }
        else
        {
            Debug.LogError("AuthControl instance not found.");
        }
        // JoinRoomManager 인스턴스를 찾거나 생성하여 TCPMessageHandler에 설정
        joinRoomManager = FindObjectOfType<JoinRoomManager>();
        if (joinRoomManager != null)
        {
            TCPMessageHandler.SetJoinRoomManagerInstance(joinRoomManager);
        }
        else
        {
            Debug.LogError("JoinRoomManager instance not found.");
        }

    

    }

    public static TCPClientManager Instance
    {
        get
        {
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

    // TCP 서버에 연결
    public bool Connect()
    {
        try
        {
            client = new TcpClient(serverIp, serverPort);
            stream = client.GetStream();
            isConnected = true;
            // Debug.Log("TCP 연결됨");
            return true;
        }
        catch (Exception e)
        {
            Debug.LogError("TCP 연결 실패: " + e.Message);
            return false;
        }
    }

    public NetworkStream GetStream()
    {
        return stream;
    }

    public void Disconnect()
    {
        if (client != null)
        {
            client.Close();
            Debug.Log("TCP 연결 종료");
        }
        isConnected = false;
    }

    // TCP 서버 메시지 수신 작업 시작
    public void StartReceiving()
    {
        // Debug.Log("StartReceiving");
        if (!receiving)
        {
            receiving = true;
            StartCoroutine(WaitForTCPRequest());
        }
    }

    private IEnumerator WaitForTCPRequest()
    {   
        // Debug.Log("WaitForTCPRequest");
        while (true)
        {
            yield return StartCoroutine(ReceiveData());
            yield return null;
        }
    }

    // TCP 서버로부터 메시지 수신
    private IEnumerator ReceiveData()
    {
        string responseData = null; // 값을 저장할 변수 선언

        try
        {
            if (isConnected && client.Available > 0)
            {
                byte[] buffer = new byte[client.Available];
                int bytesRead = stream.Read(buffer, 0, buffer.Length);
                responseData = System.Text.Encoding.UTF8.GetString(buffer, 0, bytesRead);
                Debug.Log("Received data: " + responseData);
                TCPMessageHandler.HandleReceivedData(responseData);
            }
        }
        catch (Exception e)
        {
            Debug.LogError("Error receiving data: " + e);
        }

        yield return responseData; // 변수에 저장된 값을 반환
    }

    // TCP 서버로부터 메시지를 동기적으로 수진
    public string ReceiveTCPResponse()
    {
        try
        {
            if (isConnected && client.Available > 0)
            {
                byte[] buffer = new byte[client.Available];
                int bytesRead = stream.Read(buffer, 0, buffer.Length);
                string responseData = System.Text.Encoding.UTF8.GetString(buffer, 0, bytesRead);
                // Debug.Log("Received message from server: " + responseData);
                return responseData;
            }
            else
            {
                return null; // 응답이 없는 경우 null 반환
            }
        }
        catch (Exception e)
        {
            Debug.LogError("TCP 응답 수신 실패: " + e.Message);
            return null; // 오류가 발생한 경우 null 반환
        }
    }

    // TCP 서버로 메시지 전송
    public void SendTCPRequest(string request)
    {
        try
        {
            byte[] requestData = System.Text.Encoding.UTF8.GetBytes(request);
            stream.Write(requestData, 0, requestData.Length);
        }
        catch (Exception e)
        {
            Debug.LogError("TCP 송신 실패: " + e.Message);
        }
    }
}
