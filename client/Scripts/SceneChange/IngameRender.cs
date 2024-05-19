using System.Collections;
using UnityEngine;
using UnityEngine.SceneManagement;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using TMPro;
using UnityEngine.UI;



public class IngameRender : MonoBehaviour
{
    public GameObject customer0;
    public GameObject customer1;
    public GameObject customer2;
    public GameObject customer3;
    public GameObject customer4;
    public GameObject customer5;


    private void Awake()
    {
        
    }

    private void Start()
    {
        TCPMessageHandler.SetIngameRenderInstance(this);
    }
    private void Update()
    {
        if (Input.GetKeyDown(KeyCode.F5))
        {
            ExitGame();
        }

    }

    public void ExitGame()
    {
        Debug.Log("게임종료");

        // TCP 통신 종료
        TCPClientManager.Instance.DisconnectMainServer();

        // Chat 통신 종료
        TCPClientManager.Instance.DisconnectChatServer();

        // 게임 종료
        #if UNITY_EDITOR
                UnityEditor.EditorApplication.isPlaying = false;
        #else
                Application.Quit();
        #endif
    }
    

    // 타 유저 게임 나가기 시 플레이어 객체 삭제
    public void DeleteOtherPlayer(JObject data)
    {
        Debug.Log("DeleteOtherPlayer enter");
        int targetUserId = (int)data["dataBody"]["userId"];
        // Player 태그를 가진 모든 오브젝트를 찾아서 배열에 저장
        GameObject[] players = GameObject.FindGameObjectsWithTag("Player");

        // 배열을 순회하면서 userId를 확인하고 일치하는 경우 삭제
        foreach (GameObject player in players)
        {
            // PlayerInfo 컴포넌트 가져오기
            PlayerInfo playerInfo = player.GetComponent<PlayerInfo>();

            // PlayerInfo 컴포넌트가 존재하고, userId가 일치하는지 확인
            if (playerInfo != null && playerInfo.userId == targetUserId)
            {
                // 일치하는 경우 해당 오브젝트 삭제
                Destroy(player);
            }
        }
    }

    // 유저 위치, 인벤토리 정보 요청 송신 클래스
    public class InitInfoRequest
    {
        public string type;
        public string eventName;
        public string channelId;
        public int mapId;
        public int userId;

        public InitInfoRequest(string type, string eventName, string channelId, int mapId, int userId)
        {
            this.type = type;
            this.eventName = eventName;
            this.channelId = channelId;
            this.mapId = mapId;
            this.userId = userId;
        }
    }
    // 유저 위치, 인벤토리 정보 요청 송신 메서드
    public void GetInitInfoRequest()
    {
        Debug.Log("정보 요청 송신");
        string channelId = ChannelManager.Instance.GetChannelId();
        int mapId = ChannelManager.Instance.GetMapId();
        int userId = UserInfoManager.Instance.GetUserId();

        InitInfoRequest request = new InitInfoRequest("ingameSession", "userInfo", channelId, mapId, userId);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendMainTCPRequest(json);
    }
    // 유저 위치, 인벤토리 정보 수신 메서드
    public void SetInitUserInfo(JObject data)
    {
        // 인벤정보 세팅 필요

    }
    //public void StartCoroutine(JObject data)
    //{
    //    StartCoroutine(StartPositionInit(data));
    //}
    // 게임 시작 위치 수신, 플레이어 생성 코루틴
    //public IEnumerator StartPositionInit(JObject data)
    //{
    //    Debug.Log("정보 요청 수신, 코루틴 실행");
    //    yield return new WaitForSeconds(3.0f);

    //    SetInitPosition(data);
    //}

    public void SetInitPosition(JObject data)
    {
        JArray positionsArray = (JArray)data["dataBody"]["positions"];
        foreach (JToken item in positionsArray)
        {
            string positionStr = (string)item["position"];
            int userId = (int)item["userId"];
            string nickname = (string)item["nickname"];
            int avatar = (int)item["avatar"];
            string[] coordinates = positionStr.Split(':');
            if (coordinates.Length == 6)
            {
                float x = float.Parse(coordinates[0]);
                float y = float.Parse(coordinates[1]);
                float z = float.Parse(coordinates[2]);

                Vector3 position = new Vector3(x, y, z);
                Quaternion rotation = Quaternion.Euler(0, 0, 0);

                // 프리팹 설정
                // 현재 클라이언트의 userId 제외
                GameObject selectedPrefab;
                if (UserInfoManager.Instance.GetUserId() != userId)
                {
                    switch (avatar)
                    {
                        case 0:
                            selectedPrefab = customer0;
                            break;
                        case 1:
                            selectedPrefab = customer1;
                            break;
                        case 2:
                            selectedPrefab = customer2;
                            break;
                        case 3:
                            selectedPrefab = customer3;
                            break;
                        case 4:
                            selectedPrefab = customer4;
                            break;
                        case 5:
                            selectedPrefab = customer5;
                            break;
                        default:
                            selectedPrefab = null;
                            break;
                    }
                    // 플레이어를 생성하고 위치 및 회전 정보를 설정
                    if (selectedPrefab != null)
                    {
                        GameObject newPlayer = Instantiate(selectedPrefab, position, rotation);
                        // 생성된 플레이어에 대한 정보 설정
                        PlayerInfo playerInfo = newPlayer.GetComponent<PlayerInfo>();
                        if (playerInfo != null)
                        {
                            playerInfo.userId = userId;
                            playerInfo.avatar = avatar;
                            playerInfo.nickname = nickname;
                        }
                        // 플레이어 오브젝트를 활성화
                        newPlayer.SetActive(true);
                    }

                }
                
            }
        }
    }
    // 재입장 유저- 각 유저 정보 요청 송신 클래스
    public class SendPositionRequest
    {
        public string type;
        public string eventName;
        public string channelId;
        public int userId;

        public SendPositionRequest(string type, string eventName, string channelId, int userId)
        {
            this.type = type;
            this.eventName = eventName;
            this.channelId = channelId;
            this.userId = userId;
        }
    }
    // 재입장 유저- 각 유저 정보 요청 송신 메서드
    public void SendPositionTCPRequest()
    {
        string channelId = ChannelManager.Instance.GetChannelId();
        int userId = UserInfoManager.Instance.GetUserId();
        SendPositionRequest request = new SendPositionRequest("ingameSession", "findUserData", channelId, userId);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendMainTCPRequest(json);
    }

    // 재입장 유저- 각 유저 정보 수신 메서드
    public void ReceiveIngameUserInfo(JObject data)
    {
        JArray positionsArray = (JArray)data["dataBody"]["positions"];
        foreach (JToken item in positionsArray)
        {
            // 타 플레이어 오브젝트 생성
            int userId = (int)item["userId"];
            string nickname = (string)item["nickname"];
            int avatar = (int)item["avatar"];

            // 초기 위치 모르므로 0으로 세팅
            Vector3 position = new Vector3(0, 0, 0);
            Quaternion rotation = Quaternion.Euler(0, 0, 0);

            GameObject selectedPrefab;

            switch (avatar)
                    {
                        case 0:
                            selectedPrefab = customer0;
                            break;
                        case 1:
                            selectedPrefab = customer1;
                            break;
                        case 2:
                            selectedPrefab = customer2;
                            break;
                        case 3:
                            selectedPrefab = customer3;
                            break;
                        case 4:
                            selectedPrefab = customer4;
                            break;
                        case 5:
                            selectedPrefab = customer5;
                            break;
                        default:
                            selectedPrefab = null;
                            break;
                    }
                    if (selectedPrefab != null)
                    {
                        GameObject newPlayer = Instantiate(selectedPrefab, position, rotation);
                        // 생성된 플레이어에 대한 정보 설정
                        PlayerInfo playerInfo = newPlayer.GetComponent<PlayerInfo>();
                        if (playerInfo != null)
                        {
                            playerInfo.userId = userId;
                            playerInfo.avatar = avatar;
                            playerInfo.nickname = nickname;
                        }
                        // 플레이어 오브젝트를 활성화
                        newPlayer.SetActive(true);
                    }
        }
    }

    public class SuccessEnterRequest
    {
        public string type;
        public string eventName;
        public string channelId;
        public int userId;
        public bool isNewUser;

        public SuccessEnterRequest(string type, string eventName, string channelId, int userId, bool isNewUser)
        {
            this.type = type;
            this.eventName = eventName;
            this.channelId = channelId;
            this.userId = userId;
            this.isNewUser = isNewUser;
        }
    }
    // 채널 입장 메시지 수신(이미 접속해 있던 유저- 멀티캐스팅)
    public void SomeoneIsComming(JObject data)
    {
        string nickname = (string)data["dataBody"]["nickname"];
        int userId = (int)data["dataBody"]["userId"];
        int avatar = int.Parse((string)data["dataBody"]["avatar"]);
       
        string positionStr = (string)data["dataBody"]["position"];
        string[] coordinates = positionStr.Split(':');

        float x = float.Parse(coordinates[0]);
        float y = float.Parse(coordinates[1]);
        float z = float.Parse(coordinates[2]);

        Vector3 position = new Vector3(x, y, z);
        Quaternion rotation = Quaternion.Euler(0, 0, 0);
        // 프리팹 설정
        GameObject selectedPrefab;
        switch (avatar)
        {
            case 0:
                selectedPrefab = customer0;
                break;
            case 1:
                selectedPrefab = customer1;
                break;
            case 2:
                selectedPrefab = customer2;
                break;
            case 3:
                selectedPrefab = customer3;
                break;
            case 4:
                selectedPrefab = customer4;
                break;
            case 5:
                selectedPrefab = customer5;
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
        string channelId = ChannelManager.Instance.GetChannelId();
        int nowUserId = UserInfoManager.Instance.GetUserId();
        string positionInfo = "";
        CameraInfoManager cameraInfoManager = FindObjectOfType<CameraInfoManager>();
        if (cameraInfoManager != null)
        {
            positionInfo = cameraInfoManager.UpdateCameraInfo();
        }
        else
        {
            Debug.LogError("CameraInfoManager instance not found.");
        }
        int mapId = ChannelManager.Instance.GetMapId();
        SendInitPosition request = new SendInitPosition("ingameSession", "broadcastPosition", channelId, nowUserId, positionInfo, mapId);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendMainTCPRequest(json);
    }

    public class SendInitPosition
    {
        public string type;
        public string eventName;
        public string channelId;
        public int userId;
        public string position;
        public int mapId;

        public SendInitPosition(string type, string eventName, string channelId, int userId, string position, int mapId)
        {
            this.type = type;
            this.eventName = eventName;
            this.channelId = channelId;
            this.userId = userId;
            this.position = position;
            this.mapId = mapId;
        }
    }
    // 연구소 입장
    public void EnterLabPage(JObject data)
    {
        StartCoroutine(LoadSceneAndInitialize("ElementLabScene", data));
    }

    IEnumerator LoadSceneAndInitialize(string sceneName, JObject data)
    {
        // 씬 로드 완료 시 호출될 메서드를 이벤트에 구독
        SceneManager.sceneLoaded += OnSceneLoaded;

        // 씬 로드를 비동기적으로 시작
        var asyncLoad = SceneManager.LoadSceneAsync(sceneName);

        // 씬 로드가 완료될 때까지 대기
        while (!asyncLoad.isDone)
        {
            yield return null;
        }

        // OnSceneLoaded 내부
        void OnSceneLoaded(Scene scene, LoadSceneMode mode)
        {
            if (scene.name == sceneName)
            {
                var elementLabRender = GameObject.FindObjectOfType<ElementLabRender>();
                if (elementLabRender != null)
                {
                    elementLabRender.SetLabLevel(data);
                }
                else
                {
                    Debug.LogError("ElementLabRender not found in the scene.");
                }

                // 이벤트 구독 해제
                SceneManager.sceneLoaded -= OnSceneLoaded;
            }
        }
    }
}
