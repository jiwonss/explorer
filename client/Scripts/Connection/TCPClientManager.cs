using System;
using System.Collections;
using System.Net.Sockets;
using UnityEngine;

public class TCPClientManager : MonoBehaviour
{
    private static TCPClientManager instance;

    private TcpClient mainClient;
    private TcpClient chatClient;
    private NetworkStream mainStream;
    private NetworkStream chatStream;
    private string serverIp;
    private int serverPort;
    private int chatServerPort;

    public bool IsMainConnected { get; private set; } = false;
    public bool IsChatConnected { get; private set; } = false;

    private bool receivingMain = false;
    private bool receivingChat = false;

    private string mainServerData;  // Received data from the main server

    public static TCPClientManager Instance
    {
        get
        {
            if (instance == null)
            {
                GameObject obj = new GameObject("TCPClientManager");
                instance = obj.AddComponent<TCPClientManager>();
                DontDestroyOnLoad(obj);
            }
            return instance;
        }
    }

    private void Awake()
    {
        if (instance == null)
        {
            instance = this;
            DontDestroyOnLoad(gameObject);
        }
        else if (instance != this)
        {
            Destroy(gameObject);
        }
    }

    private void Start()
    {
        AuthControl authControl = FindObjectOfType<AuthControl>();
        if (authControl != null)
        {
            TCPMessageHandler.SetAuthControlInstance(authControl);
        }
        else
        {
            Debug.LogError("AuthControl instance not found.");
        }

        ChannelControl channelControl = FindObjectOfType<ChannelControl>();
        if (channelControl == null)
        {
            GameObject channelControlObject = new GameObject("ChannelControl");
            channelControl = channelControlObject.AddComponent<ChannelControl>();
        }
        TCPMessageHandler.SetChannelControlInstance(channelControl);

        Init(ServerConfigLoader.serverIp, ServerConfigLoader.serverPort, ServerConfigLoader.chatServerPort);


        // DestroyObjectBroadcast 인스턴스 설정
        DestroyObjectBroadcast destroyObjectBroadcast = FindObjectOfType<DestroyObjectBroadcast>();
        if (destroyObjectBroadcast == null)
        {
            GameObject destroyObjectBroadcastObject = new GameObject("DestroyObjectBroadcast");
            destroyObjectBroadcast = destroyObjectBroadcastObject.AddComponent<DestroyObjectBroadcast>();
        }
        TCPMessageHandler.SetDestroyObjectBroadcastInstance(destroyObjectBroadcast);

        Init(ServerConfigLoader.serverIp, ServerConfigLoader.serverPort, ServerConfigLoader.chatServerPort);


    }

    public void Init(string ip, int mainPort, int chatPort)
    {
        serverIp = ip;
        serverPort = mainPort;
        chatServerPort = chatPort;
    }

    public bool ConnectMainServer()
    {
        try
        {
            mainClient = new TcpClient(serverIp, serverPort);
            mainStream = mainClient.GetStream();
            IsMainConnected = true;
            return true;
        }
        catch (Exception e)
        {
            Debug.LogError("Main server 연결 실패: " + e.Message);
            return false;
        }
    }

    public bool ConnectChatServer()
    {
        try
        {
            chatClient = new TcpClient(serverIp, chatServerPort);
            chatStream = chatClient.GetStream();
            IsChatConnected = true;
            return true;
        }
        catch (Exception e)
        {
            Debug.LogError("Chat server 연결 실패: " + e.Message);
            return false;
        }
    }

    public void DisconnectMainServer()
    {
        if (mainClient != null)
        {
            mainClient.Close();
            Debug.Log("Main server 연결 종료");
        }
        IsMainConnected = false;
    }

    public void DisconnectChatServer()
    {
        if (chatClient != null)
        {
            chatClient.Close();
            Debug.Log("Chat server 연결 종료");
        }
        IsChatConnected = false;
    }

    public void StartReceivingMain()
    {
        if (IsMainConnected && !receivingMain)
        {
            receivingMain = true;
            StartCoroutine(WaitForMainTCPRequest());
        }
    }

    public void StartReceivingChat()
    {
        if (IsChatConnected && !receivingChat)
        {
            receivingChat = true;
            StartCoroutine(WaitForChatTCPRequest());
        }
    }

    private IEnumerator WaitForMainTCPRequest()
    {
        while (IsMainConnected)
        {
            yield return StartCoroutine(ReceiveMainDataCoroutine());
            yield return null;
        }
    }

    private IEnumerator WaitForChatTCPRequest()
    {
        while (IsChatConnected)
        {
            yield return StartCoroutine(ReceiveChatDataCoroutine());
            yield return null;
        }
    }

    private IEnumerator ReceiveMainDataCoroutine()
    {
        try
        {
            if (IsMainConnected && mainClient.Available > 0)
            {
                byte[] buffer = new byte[mainClient.Available];
                int bytesRead = mainStream.Read(buffer, 0, buffer.Length);
                mainServerData = System.Text.Encoding.UTF8.GetString(buffer, 0, bytesRead);
                TCPMessageHandler.HandleReceivedData(mainServerData);
            }
        }
        catch (Exception e)
        {
            Debug.LogError("Main server data receive error: " + e);
        }
        yield return null;
    }

    public string ReceiveMainData()
    {
        string data = mainServerData;
        mainServerData = null; // Clear the data after retrieving it
        return data;
    }

    private IEnumerator ReceiveChatDataCoroutine()
    {
        try
        {
            if (IsChatConnected && chatClient.Available > 0)
            {
                byte[] buffer = new byte[chatClient.Available];
                int bytesRead = chatStream.Read(buffer, 0, buffer.Length);
                string responseData = System.Text.Encoding.UTF8.GetString(buffer, 0, bytesRead);
                TCPMessageHandler.HandleReceivedData(responseData);
            }
        }
        catch (Exception e)
        {
            Debug.LogError("Chat server data receive error: " + e);
        }
        yield return null;
    }

    public void SendMainTCPRequest(string request)
    {
        try
        {
            byte[] requestData = System.Text.Encoding.UTF8.GetBytes(request);
            mainStream.Write(requestData, 0, requestData.Length);
        }
        catch (Exception e)
        {
            Debug.LogError("Main server 송신 실패: " + e.Message);
        }
    }

    public void SendChatTCPRequest(string request)
    {
        try
        {
            byte[] requestData = System.Text.Encoding.UTF8.GetBytes(request);
            chatStream.Write(requestData, 0, requestData.Length);
        }
        catch (Exception e)
        {
            Debug.LogError("Chat server 송신 실패: " + e.Message);
        }
    }
}
