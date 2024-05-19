using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;
using UnityEngine.SceneManagement;
using System.Text.RegularExpressions;
using static UnityEngine.UIElements.UxmlAttributeDescription;
using UnityEngine.Networking;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Net.Sockets;

public class ChannelControl : MonoBehaviour
{

    private int avatar;
    private string nickname;

    private bool isNickOk;

    private string planetName;

    private string channel1Id;
    private string channel2Id;
    private string channel3Id;
    private string selectedChanneId;

    public Sprite char0;
    public Sprite char1; 
    public Sprite char2; 
    public Sprite char3; 
    public Sprite char4; 
    public Sprite char5;


    [Header("SignUp")]
    public GameObject SignUpScene;

    [Header("ProfilePage")]
    public GameObject ProfilePage;
    public GameObject ProfileChanger;
    public Image profileAvatarField;
    public Image profileAvatarFieldChange;
    public Button ProfileDesignLeftBtn;
    public Button ProfileDesignRightBtn;
    public TextMeshProUGUI NowNickName;
    public TextMeshProUGUI BeforeNickname;
    public TMP_InputField NickNameChange;
    public Button ProfileChangeBtn;
    public Button ProfileChangeAcceptBtn;
    public Button endingPlanetBtn;

    [Header("RoomChannelSelect")]
    public Button Channel1Image;
    public TextMeshProUGUI Channel1Text;
    public Button Channel2Image;
    public TextMeshProUGUI Channel2Text;
    public Button Channel3Image;
    public TextMeshProUGUI Channel3Text;


    //채널 관련 페이지
    [Header("ExistChannel")] // 채널 있는 경우
    public GameObject ExistModal; // 모달
    public TextMeshProUGUI CreatedAText;
    public TextMeshProUGUI OnlineMember; 
    public Button EnterGameRoomBtn; // 게임(채널) 입장
    public Button ExitGameRooomBtn; // 채널 나가기

    [Header("NoneChannel-MakeNewGame")] // 채널 없는 경우, 새 게임 생성
    public GameObject NoneModal; // 모달
    public Button MakeNewRoomBtn; // 행성 이름 입력 모달 띄우기 버튼
    public GameObject PlanetModal; // 행성 이름 입력 모달
    public TextMeshProUGUI PlanetInput; // 행성 이름 입력 인풋필드
    public Button PlanetEnter; // 행성 이름 입력 후 생성 클릭
    public Button PlanetCancel; // 생성 취소

    [Header("NoneChannel-EnterNewGame")] // 채널 없는 경우, 대기방 입장
    public Button EnterNewRoomBtn; // 팀코드 모달 띄우기 버튼
    public GameObject TeamCodeModal; // 팀코드 모달
    public TextMeshProUGUI TeamCodeInput; // 팀코드 인풋
    public Button TeamCodeEnter; // 팀코드 입력 후 입장
    public Button TeamcodeCancel; // 입장 취소

    //서버 응답
    [System.Serializable]
    public class ServerResponse
    {
        public DataHeader dataHeader;
        public object dataBody;
    }

    [System.Serializable]
    public class DataHeader
    {
        public int successCode;
        public string resultCode;
        public string resultMessage;
    }

    [System.Serializable]
    public class ReissueResponse
    {
        public DataHeader dataHeader;
        public TokenInfo dataBody;
    }

    [System.Serializable]
    public class TokenInfo
    {
        public string accessToken;
        public string refreshToken;
    }

    void Awake()
    {
        DontDestroyOnLoad(this.gameObject);
    }


    public void Start()
    {
        Cursor.visible = true;
        
        ProfileChangeBtn.onClick.AddListener(EditProfile);
        ProfileChangeAcceptBtn.onClick.AddListener(StartEditAccept);
        ProfileDesignLeftBtn.onClick.AddListener(AvatarChangeLeft); // 왼쪽 버튼 클릭
        ProfileDesignRightBtn.onClick.AddListener(AvatarChangeRight); // 오른쪽 버튼 클릭

        Channel1Image.onClick.AddListener(OnChannel1ButtonClick);
        Channel2Image.onClick.AddListener(OnChannel2ButtonClick);
        Channel3Image.onClick.AddListener(OnChannel3ButtonClick);

        EnterGameRoomBtn.onClick.AddListener(RestartGameRequest); // 게임 채널 입장
        ExitGameRooomBtn.onClick.AddListener(DeleteChannel); // 게임 채널 나가기
        MakeNewRoomBtn.onClick.AddListener(PlanetModalOn); // 행성 이름 입력 모달 띄우기 버튼
        PlanetEnter.onClick.AddListener(MakeNewRoom); // 행성명 입력 후 입장.
        PlanetCancel.onClick.AddListener(PlanetModalOff); // 새 대기방 cancel
        EnterNewRoomBtn.onClick.AddListener(TeamCodeModalOn); // 팀코드 모달 띄우기 버튼
        TeamCodeEnter.onClick.AddListener(EnterTeamCode); // 팀코드 입력으로 입장
        TeamcodeCancel.onClick.AddListener(TeamCodeModalOff); // 팀코드 입장 취소



    }
    // 첫 렌더 시 아바타, 닉네임 설정
    public void SetProfile()
    {
        avatar = UserInfoManager.Instance.GetAvatar();
        nickname = UserInfoManager.Instance.GetNickname();
        // 초기 아바타 설정
        // 아바타 값에 따라 스프라이트 설정
        switch (avatar)
        {
            case 0:
                profileAvatarField.sprite = char0;
                break;
            case 1:
                profileAvatarField.sprite = char1;
                break;
            case 2:
                profileAvatarField.sprite = char2;
                break;
            case 3:
                profileAvatarField.sprite = char3;
                break;
            case 4:
                profileAvatarField.sprite = char4;
                break;
            case 5:
                profileAvatarField.sprite = char5;
                break;
            default:
                Debug.LogError("Invalid avatar value: " + avatar);
                break;
        }
        // 초기 닉네임 설정
        NowNickName.text = nickname;
    }

    // 프로필 수정 모달
    public void EditProfile()
    {
        ProfileChanger.SetActive(true);
        ProfilePage.SetActive(false);
        // 초기 아바타 렌더
        switch (avatar)
        {
            case 0:
                profileAvatarFieldChange.sprite = char0;
                break;
            case 1:
                profileAvatarFieldChange.sprite = char1;
                break;
            case 2:
                profileAvatarFieldChange.sprite = char2;
                break;
            case 3:
                profileAvatarFieldChange.sprite = char3;
                break;
            case 4:
                profileAvatarFieldChange.sprite = char4;
                break;
            case 5:
                profileAvatarFieldChange.sprite = char5;
                break;
            default:
                Debug.LogError("Invalid avatar value: " + avatar);
                break;
        }
        // 닉네임 렌더
        isNickOk = false;
        NickNameChange.text = nickname;
    }

    // 버튼 클릭 시 렌더되는 아바타 변경 - 왼쪽
    public void AvatarChangeLeft()
    {
        avatar -= 1;
        if(avatar < 0)
        {
            avatar = 5;
        }
        switch (avatar)
        {
            case 0:
                profileAvatarFieldChange.sprite = char0;
                break;
            case 1:
                profileAvatarFieldChange.sprite = char1;
                break;
            case 2:
                profileAvatarFieldChange.sprite = char2;
                break;
            case 3:
                profileAvatarFieldChange.sprite = char3;
                break;
            case 4:
                profileAvatarFieldChange.sprite = char4;
                break;
            case 5:
                profileAvatarFieldChange.sprite = char5;
                break;
            default:
                Debug.LogError("Invalid avatar value: " + avatar);
                break;
        }
    }
    
    // 버튼 클릭 시 렌더되는 아바타 변경 - 오른쪽
    public void AvatarChangeRight()
    {
        avatar += 1;
        if(avatar > 5)
        {
            avatar = 0;
        }
        switch (avatar)
        {
            case 0:
                profileAvatarFieldChange.sprite = char0;
                break;
            case 1:
                profileAvatarFieldChange.sprite = char1;
                break;
            case 2:
                profileAvatarFieldChange.sprite = char2;
                break;
            case 3:
                profileAvatarFieldChange.sprite = char3;
                break;
            case 4:
                profileAvatarFieldChange.sprite = char4;
                break;
            case 5:
                profileAvatarFieldChange.sprite = char5;
                break;
            default:
                Debug.LogError("Invalid avatar value: " + avatar);
                break;
        }
    }

    public void StartEditAccept()
    {
        StartCoroutine(EditAccept());
    }

    // 프로필 수정 저장
    IEnumerator EditAccept()
    {
        CheckOkNickName(NickNameChange.text);
        // yield return StartCoroutine(CheckDuplicate());
        // 닉네임이 유효하면
        if (isNickOk)
        {
            if (string.IsNullOrEmpty(TokenManager.Instance.GetAccessToken()))
            {
                Debug.LogError("로그인되지 않았습니다. 로그인 후 다시 시도하세요.");
                yield break;
            }
            nickname = NickNameChange.text;
            string url = ServerConfigLoader.URL + "/user/users/profile";
            string jsonData = "{\"nickname\": \"" + nickname + "\", \"avatar\": \"" + avatar + "\"}";
            UnityWebRequest request = new UnityWebRequest(url, "PATCH");

            byte[] bodyRaw = System.Text.Encoding.UTF8.GetBytes(jsonData);
            request.uploadHandler = (UploadHandler)new UploadHandlerRaw(bodyRaw);
            request.downloadHandler = (DownloadHandler)new DownloadHandlerBuffer();
            request.SetRequestHeader("Content-Type", "application/json");
            request.SetRequestHeader("Authorization", "Bearer " + TokenManager.Instance.GetAccessToken());
            yield return request.SendWebRequest();

            if (request.result == UnityWebRequest.Result.Success && request.responseCode == 200)
            {
                ServerResponse response = JsonUtility.FromJson<ServerResponse>(request.downloadHandler.text);




                if (response.dataHeader.resultCode == "EXPIRED_TOKEN")
                {
                    Debug.Log("토큰 만료");
                    //토큰 재발급
                    yield return StartCoroutine(TokenManager.Instance.ReissueToken(TokenManager.Instance.GetAccessToken(), TokenManager.Instance.GetRefreshToken()));

                    //다시 프로필 수정 요청
                    StartCoroutine(EditAccept());
                    yield break;
                }
                // 프로필 수정 성공
                else
                {
                    UserInfoManager.Instance.ChangeProfile(nickname, avatar);

                    ProfileChanger.SetActive(false);
                    ProfilePage.SetActive(true);
                    SetProfile();
                }

            }
            else
            {
                NickNameChange.text = "이미 존재하는 닉네임";
                Debug.Log("이미 존재하는 닉네임");
                isNickOk = false;
            }
        }
        else
        {
            NickNameChange.text = "조건에 맞지 않습니다";
            Debug.Log("조건에 맞지 않습니다");
            isNickOk = false;
        }
        
    }
        
    
    // 닉네임 유효성체크
    void CheckOkNickName(string nickname)
    {
        string pattern = @"^[A-Za-z0-9가-힣]{2,8}$";

        // 정규식과 매치되는지 확인
        if (Regex.IsMatch(nickname, pattern))
        {
            isNickOk = true;
        }
        else
        {
            isNickOk = false;
        }
    }

    // 존재 채널 모달
    public void IfExistRoom()
    {
        //Debug.Log("ExistOK");
        NoneModal.SetActive(false);
        ExistModal.SetActive(true);
        // 존재하는 방일경우 정보 업데이트 필요

    }
    // 없는 채널 모달
    public void IfNoneRoom()
    {
        //Debug.Log("NoneOk");
        NoneModal.SetActive(true);
        ExistModal.SetActive(false);
    }

    // TCP 메시지 수신받아 채널 이름, id 설정
    public void SetChannelList(JObject data)
    {
        if (data["dataBody"].HasValues)
        {
            JArray dataBody = (JArray)data["dataBody"];

            for (int i = 0; i < dataBody.Count; i++)
            {
                JObject channelData = (JObject)dataBody[i];
                string channelName = (string)channelData["channelName"];
                string channelId = (string)channelData["channelId"];

                // i 값에 따라 채널 텍스트를 설정
                switch (i)
                {
                    case 0:
                        Channel1Text.text = channelName;
                        channel1Id = channelId;
                        Channel2Text.text = "새 게임";
                        channel2Id = null;
                        Channel3Text.text = "새 게임";
                        channel3Id = null;
                        break;
                    case 1:
                        Channel2Text.text = channelName;
                        channel2Id = channelId;
                        Channel3Text.text = "새 게임";
                        channel3Id = null;
                        break;
                    case 2:
                        Channel3Text.text = channelName;
                        channel3Id = channelId;
                        break;
                }
                OnChannel1ButtonClick();
            }
        }
        else
        {
            Channel1Text.text = "새 게임";
            channel1Id = null;
            Channel2Text.text = "새 게임";
            channel2Id = null;
            Channel3Text.text = "새 게임";
            channel3Id = null;
            IfNoneRoom();
        }

    }
    // 채널리스트 조회 메시지 송신
    public void GetChannelList()
    {
        int userId = UserInfoManager.Instance.GetUserId();
        ChannelListRequest requestData = new ChannelListRequest("channel", "getChannelList", userId);
        string json = JsonConvert.SerializeObject(requestData);
        TCPClientManager.Instance.SendMainTCPRequest(json);
    }
    public class ChannelListRequest
    {
        public string type;
        public string eventName;
        public int userId;
        public ChannelListRequest(string type, string eventName, int userId)
        {
            this.type = type;
            this.eventName = eventName;
            this.userId = userId;
        }
    }


    // 채널 상세조회 요청 메시지 송신 클래스
    public class ChannelDetailRequest
    {
        public string type;
        public string eventName;
        public int userId;
        public string channelId;
        
        public ChannelDetailRequest(string type, string eventName, int userId, string channelId)
        {
            this.type = type;
            this.eventName = eventName;
            this.userId = userId;
            this.channelId = channelId;
        }
    }
    // 채널 상세 조회 메시지 수신
    public void SetChannelDetail(JObject data)
    {
        IfExistRoom();
        var playerInfoList = data["dataBody"]["playerInfoList"];
        var channelDetailsInfo = data["dataBody"]["channelDetailsInfo"];

        if (data["dataBody"]["playerInfoList"].HasValues)
        {
            // 플레이어 정보를 추출
            foreach (var player in playerInfoList)
            {
                string nickname = player["nickname"].ToString();
                bool online = bool.Parse(player["online"].ToString());
                if (online)
                {
                    OnlineMember.text = OnlineMember.text + "\t" + nickname;
                }
            }
        }
        else
        {
            OnlineMember.text = "현재 접속한 멤버가 없습니다";
        }

        // 채널 정보 추출
        string createdAt = channelDetailsInfo["createdAt"].ToString();
        CreatedAText.text = createdAt;
    }
    // 1번 채널 상세 조회 요청 메시지 송신
    public void OnChannel1ButtonClick()
    {
        if (Channel1Text.text == "새 게임" || channel1Id == null)
        {
            IfNoneRoom();
        }
        else
        {
            int userId = UserInfoManager.Instance.GetUserId();
            selectedChanneId = channel1Id;
            ChannelDetailRequest request = new ChannelDetailRequest("channel", "getChannelDetails",  userId, selectedChanneId);
            string json = JsonConvert.SerializeObject(request);
            TCPClientManager.Instance.SendMainTCPRequest(json);
            ChannelManager.Instance.SetChannelId(selectedChanneId);
            IfExistRoom();
        }
    }

    // 2번 채널 상세 조회 요청 메시지 송신
    public void OnChannel2ButtonClick()
    {
        
        if (Channel2Text.text == "새 게임" || channel2Id == null)
        {
            IfNoneRoom();
        }
        else
        {
            int userId = UserInfoManager.Instance.GetUserId();
            selectedChanneId = channel2Id;
            ChannelDetailRequest request = new ChannelDetailRequest("channel", "getChannelDetails", userId, selectedChanneId);
            string json = JsonConvert.SerializeObject(request);
            TCPClientManager.Instance.SendMainTCPRequest(json);
            ChannelManager.Instance.SetChannelId(selectedChanneId);
            IfExistRoom();
        }
    }
    // 3번 채널 상세 조회 요청 메시지 송신
    public void OnChannel3ButtonClick()
    {
        
        if (Channel3Text.text == "새 게임" || channel3Id == null)
        {
            IfNoneRoom();
        }
        else
        {
            int userId = UserInfoManager.Instance.GetUserId();
            selectedChanneId = channel3Id;
            ChannelDetailRequest request = new ChannelDetailRequest("channel", "getChannelDetails", userId, selectedChanneId);
            string json = JsonConvert.SerializeObject(request);
            TCPClientManager.Instance.SendMainTCPRequest(json);
            ChannelManager.Instance.SetChannelId(selectedChanneId);
            IfExistRoom();
        }
    }

    // 팀코드 입력 모달 on
    public void TeamCodeModalOn()
    {
        TeamCodeModal.SetActive(true);
    }

    // 팀코드 입력 모달 off
    public void TeamCodeModalOff()
    {
        TeamCodeModal.SetActive(false);
    }


    public class JoinRoomRequest
    {
        public string type;
        public string eventName;
        public string teamCode;
        public int userId;
        public string nickname;
        public int avatar;

        public JoinRoomRequest(string type, string eventName, string teamCode, int userId, string nickname, int avatar)
        {
            this.type = type;
            this.eventName = eventName;
            this.teamCode = teamCode;
            this.userId = userId;
            this.nickname = nickname;
            this.avatar = avatar;
        }

    }

    // 팀코드로 입장 요청
    public void EnterTeamCode()
    {
        TeamCodeModal.SetActive(false);
        string teamCode = TeamCodeInput.text;
        teamCode = teamCode.Replace(" ", "");
        teamCode = teamCode.Replace("\u200b", "");
        ChannelManager instance = ChannelManager.Instance;
        ChannelManager.instance.SetTeamCode(teamCode);
        // 유저 정보
        UserInfoManager userInfoManager = UserInfoManager.Instance;
        int userId = userInfoManager.GetUserId();
        string nickname = userInfoManager.GetNickname();
        int avatar = userInfoManager.GetAvatar();
        // 방 입장 요청 보냄
        JoinRoomRequest request = new JoinRoomRequest("waitingRoomSession", "joinWaitingRoom", teamCode, userId, nickname, avatar);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendMainTCPRequest(json);
    }

    // 방 입장 성공 반환 시 채팅 연결, 씬 전환
    public void EnterRoom()
    {
        // 채팅 연결
        TCPClientManager.Instance.ConnectChatServer();
        TCPClientManager.Instance.StartReceivingChat();
        int userId = UserInfoManager.Instance.GetUserId();
        string teamCode = ChannelManager.Instance.GetTeamCode();
        ConnectChatServer request = new ConnectChatServer("chat", "joinChattingRoom", teamCode, userId);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendChatTCPRequest(json);
        // 씬 전환
        SceneManager.LoadScene("WaitingRoom2");
        SignUpScene.SetActive(false);

    }

    public class ConnectChatServer
    {
        public string type;
        public string eventName;
        public string teamCode;
        public int userId;
        public ConnectChatServer(string type, string eventName, string teamCode, int userId)
        {
            this.type = type;
            this.eventName = eventName;
            this.teamCode = teamCode;
            this.userId = userId;
        }
    }

    // 행성 이름 입력 모달 on
    public void PlanetModalOn()
    {
        PlanetModal.SetActive(true);
    }
    // 행성 이름 입력 모달 off
    public void PlanetModalOff()
    {
        PlanetModal.SetActive(false);
    }

    // 방 생성 요청
    public class MakeRoomRequest
    {
        public string type;
        public string eventName;
        public int userId;
        public string nickname;
        public int avatar;

        public MakeRoomRequest(string type, string eventName, int userId, string nickname, int avatar)
        {
            this.type = type;
            this.eventName = eventName;
            this.userId = userId;
            this.nickname = nickname;
            this.avatar = avatar;
        }
    }
    // 대기방 생성 메시지 송신
    public void MakeNewRoom()
    {
        
        if (PlanetInput.text.Length >= 2 && !string.IsNullOrWhiteSpace(PlanetInput.text))
        {
            PlanetModal.SetActive(false);
            planetName = PlanetInput.text; // 행성 이름 저장. 
            planetName = planetName.Replace(" ", "");
            planetName = planetName.Replace("\u200b", "");
            ChannelManager.Instance.SetChannelName(planetName);

            // 저장된 유저 정보
            UserInfoManager userInfoManager = UserInfoManager.Instance;
            int userId = userInfoManager.GetUserId();
            string nickname = userInfoManager.GetNickname();
            int avatar = userInfoManager.GetAvatar();

            // 방 생성 메시지 발신
            MakeRoomRequest request = new MakeRoomRequest("waitingRoomSession", "createWaitingRoom", userId, nickname, avatar);
            string json = JsonConvert.SerializeObject(request);
            TCPClientManager.Instance.SendMainTCPRequest(json);
        }
        else
        {
            Debug.Log("행성 이름은 2글자 이상이어야 하며, 공백일 수 없습니다.");
        }
    }

    // 방 생성 성공시 채팅 연결, 씬 전환
    public void MakeRoom()
    {
        // 채팅 연결
        TCPClientManager.Instance.ConnectChatServer();
        TCPClientManager.Instance.StartReceivingChat();
        int userId = UserInfoManager.Instance.GetUserId();
        string teamCode = ChannelManager.Instance.GetTeamCode();
        ConnectChatServer request = new ConnectChatServer("chat", "joinChattingRoom", teamCode, userId);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendChatTCPRequest(json);
        SignUpScene.SetActive(false);
        SceneManager.LoadScene("WaitingRoom1");

    }

    // 채널 재입장
    public class RestartGame
    {
        public string type;
        public string eventName;
        public string channelId;
        public int userId;
        public string nickname;
        public int avatar;

        public RestartGame(string type, string eventName, string channelId, int userId, string nickname, int avatar)
        {
            this.type = type;
            this.eventName = eventName;
            this.channelId = channelId;
            this.userId = userId;
            this.nickname = nickname;
            this.avatar = avatar;
        }
    }
    // 채널 재입장 요청 송신
    public void RestartGameRequest()
    {
        int userId = UserInfoManager.Instance.GetUserId();
        string channelId = ChannelManager.Instance.GetChannelId();
        RestartGame request = new RestartGame("ingameSession", "restartGame", channelId, userId, nickname, avatar);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendMainTCPRequest(json);
    }
    // 채널 재입장 허용 메시지 수신
    public void AllowedGameRestart(JObject data)
    {
        // 채팅 연결
        TCPClientManager.Instance.ConnectChatServer();
        TCPClientManager.Instance.StartReceivingChat();
        int userId = UserInfoManager.Instance.GetUserId();
        string channelId = ChannelManager.Instance.GetChannelId();
        ConnectChatServer request = new ConnectChatServer("chat", "joinChattingRoom", channelId, userId);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendChatTCPRequest(json);
        int mapId = (int)data["dataBody"]["mapId"];
        ChannelManager.Instance.SetMapId(mapId);
        // 맵정보 처리 필요
        StartCoroutine(LoadSceneAndExecute(mapId));
    }

    private IEnumerator LoadSceneAndExecute(int mapId)
    {
        AsyncOperation asyncLoad = null;

        if (mapId == 1)
        {
            // 주행성 씬전환
            asyncLoad = SceneManager.LoadSceneAsync("IngameTest");
        }
        else if (mapId == 2)
        {
            // 소행성 씬전환
            asyncLoad = SceneManager.LoadSceneAsync("IngameTest");
        }
        else if (mapId == 3)
        {
            // 수성 씬전환
            asyncLoad = SceneManager.LoadSceneAsync("IngameTest");
        }
        else if (mapId == 4)
        {
            // 금성 씬전환
            asyncLoad = SceneManager.LoadSceneAsync("IngameTest");
        }

        // 씬이 로드될 때까지 대기
        if (asyncLoad != null)
        {
            while (!asyncLoad.isDone)
            {
                yield return null;
            }

            // 씬 로드가 완료된 후 작업 실행
            GameObject.FindObjectOfType<IngameRender>().SendPositionTCPRequest();

            SignUpScene.SetActive(false);
        }
    }


    // 채널 삭제
    public void DeleteChannel()
    {
        int userId = UserInfoManager.Instance.GetUserId();
        string channelId = ChannelManager.Instance.GetChannelId();
        DeleteChannelRequest request = new DeleteChannelRequest("channel", "deleteChannel", userId, channelId);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendMainTCPRequest(json);
    }
    public class DeleteChannelRequest
    {
        public string type;
        public string eventName;
        public int userId;
        public string channelId;

        public DeleteChannelRequest(string type, string eventName, int userId, string channelId)
        {
            this.type = type;
            this.eventName = eventName;
            this.userId = userId;
            this.channelId = channelId;
        }
    }



}
