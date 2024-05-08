using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using UnityEngine.SceneManagement;

public class JoinRoomManager : MonoBehaviour
{
    private Queue<JObject> pendingData = new Queue<JObject>();

    public GameObject prefab0;
    public GameObject prefab1;
    public GameObject prefab2;
    public GameObject prefab3;
    public GameObject prefab4;
    public GameObject prefab5;

    private string nickname;
    private int userId;
    private int avatar;

    void Awake()
    {
        DontDestroyOnLoad(gameObject);
        
    }

    void Start()
    {
        SceneManager.sceneLoaded += OnSceneLoaded;
    }

    void OnSceneLoaded(Scene scene, LoadSceneMode mode)
    {
        while (pendingData.Count > 0)
        {
            CreatePlayer(pendingData.Dequeue());
        }
    }

    public void ReceiveData(JObject data)
    {
        string castingType = (string)data["dataHeader"]["castingType"];

        if (castingType == "MULTICASTING")
        {
            // MULTICASTING 데이터는 즉시 처리
            CreatePlayer(data);
        }
        else if (castingType == "UNICASTING")
        {
            // UNICASTING 데이터는 씬 로드 완료 후 처리
            pendingData.Enqueue(data);
        }
    }

    public class EnterRequest
    {
        public string type;
        public string eventName;
        public string teamCode;
        public int userId;
        public bool isNewUser;
        
        public EnterRequest(string type, string eventName, string teamCode, int userId, bool isNewUser)
        {
            this.type = type;
            this.eventName = eventName;
            this.teamCode = teamCode;
            this.userId = userId;
            this.isNewUser = isNewUser;
        }
    }

    public class SendInitPosition
    {
        public string type;
        public string eventName;
        public string teamCode;
        public int userId;
        public string position;
        public bool isNewUser;

        public SendInitPosition(string type, string eventName, string teamCode, int userId, string position, bool isNewUser)
        {
            this.type = type;
            this.eventName = eventName;
            this.teamCode = teamCode;
            this.userId = userId;
            this.position = position;
            this.isNewUser = isNewUser;
        }
    }


    public void CreatePlayer(JObject data)
    {
        string castingType = (string)data["dataHeader"]["castingType"];

        if (castingType == "UNICASTING")
        {
            // 유니캐스팅 로직: 씬 로드 후 실행됨
            ProcessUnicastingData(data);
        }
        else if (castingType == "MULTICASTING")
        {
            // 멀티캐스팅 로직: 즉시 실행됨
            ProcessMulticastingData(data);
        }
    }

    // 유니캐스팅으로 받은 경우(입장하려는 유저) -> 이미 입장해있는 플레이어 정보 받아 객체 생성
    private void ProcessUnicastingData(JObject data)
    {
        JArray dataBody = (JArray)data["dataBody"];
        foreach (JToken item in dataBody)
        {
            // 각 객체에서 속성 값을 가져와야 함
            userId = (int)item["userId"];
            nickname = (string)item["nickname"];
            avatar = (int)item["avatar"];

            // 이후 해당 값을 사용하여 캐릭터 오브젝트 생성 등의 작업을 수행할 수 있음
            Vector3 position = new Vector3(0, 0, 0);
            Quaternion rotation = Quaternion.Euler(0, 0, 0);

            // 프리팹 설정
            GameObject selectedPrefab;
            switch (avatar)
            {
                case 0:
                    selectedPrefab = prefab0;
                    break;
                case 1:
                    selectedPrefab = prefab1;
                    break;
                case 2:
                    selectedPrefab = prefab2;
                    break;
                case 3:
                    selectedPrefab = prefab3;
                    break;
                case 4:
                    selectedPrefab = prefab4;
                    break;
                case 5:
                    selectedPrefab = prefab5;
                    break;
                default:
                    selectedPrefab = null;
                    break;
            }
            // 플레이어를 생성하고 위치 및 회전 정보를 설정(초기 영점 설정)
            GameObject newPlayer = Instantiate(selectedPrefab, position, rotation);
            // 생성된 플레이어에 대한 정보 설정
            PlayerInfo playerInfo = newPlayer.GetComponent<PlayerInfo>();
            if (playerInfo != null)
            {
                playerInfo.userId = userId;
                playerInfo.avatar = avatar;
                playerInfo.nickname = nickname;
            }
            else
            {
                Debug.LogError("PlayerInfo component not found on player prefab.");
            }
            // 플레이어 오브젝트를 활성화
            newPlayer.SetActive(true);
        }

        // 입장 완료 알림
        string teamCode = TCPMessageHandler.GetTeamCode();
        UserInfoManager userInfoManager = UserInfoManager.Instance;
        int nowUserId = userInfoManager.GetUserId();
        EnterRequest request = new EnterRequest("waitingRoomSession", "broadcastPosition", teamCode, nowUserId, true);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendTCPRequest(json);
    }


    // 멀티캐스팅으로 받은 경우(입장해있는 유저) -> 입장하려는 유저 객체 생성
    private void ProcessMulticastingData(JObject data)
    {
        userId = (int)data["dataBody"]["userId"];
        nickname = (string)data["dataBody"]["nickname"];
        avatar = (int)data["dataBody"]["avatar"];

        Vector3 position = new Vector3(0, 0, 0);
        Quaternion rotation = Quaternion.Euler(0, 0, 0);

        // 프리팹 설정
        GameObject selectedPrefab;
        switch (avatar)
        {
            case 0:
                selectedPrefab = prefab0;
                break;
            case 1:
                selectedPrefab = prefab1;
                break;
            case 2:
                selectedPrefab = prefab2;
                break;
            case 3:
                selectedPrefab = prefab3;
                break;
            case 4:
                selectedPrefab = prefab4;
                break;
            case 5:
                selectedPrefab = prefab5;
                break;
            default:
                selectedPrefab = null;
                break;
        }
        // 플레이어를 생성하고 위치 및 회전 정보를 설정
        GameObject newPlayer = Instantiate(selectedPrefab, position, rotation);
        // 생성된 플레이어에 대한 정보 설정
        PlayerInfo playerInfo = newPlayer.GetComponent<PlayerInfo>();
        if (playerInfo != null)
        {
            playerInfo.userId = userId;
            playerInfo.avatar = avatar;
            playerInfo.nickname = nickname;
        }
        else
        {
            Debug.LogError("PlayerInfo component not found on player prefab.");
        }

        newPlayer.SetActive(true);

        // 본인 위치 정보 전송
        string teamCode = TCPMessageHandler.GetTeamCode();
        UserInfoManager userInfoManager = UserInfoManager.Instance;
        int nowUserId = userInfoManager.GetUserId();
        string positionInfo = "";
        CameraInfoManager cameraInfoManager = FindObjectOfType<CameraInfoManager>();

        if (cameraInfoManager != null)
        {
            positionInfo = cameraInfoManager.UpdateCameraInfo();
            Debug.Log("positionInfo = " + positionInfo);
        }
        else
        {
            Debug.LogError("CameraInfoManager instance not found.");
        }

        SendInitPosition request = new SendInitPosition("waitingRoomSession", "broadcastPosition", teamCode, nowUserId, positionInfo, false);
        string json = JsonConvert.SerializeObject(request);
        Debug.Log("sendData : " + json);
        TCPClientManager.Instance.SendTCPRequest(json);

    }


    
}