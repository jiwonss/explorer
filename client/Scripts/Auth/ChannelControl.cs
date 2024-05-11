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
    private TCPClientManager tcpClientManager;

    private string planetName;

    private string channel1Id;
    private string channel2Id;
    private string channel3Id;

    [Header("SignUp")]
    public GameObject SignUpScene;

    [Header("ProfilePage")]
    public GameObject ProfilePage;
    public GameObject ProfileChanger;
    public Image ProfileImage;
    public Button ProfileDesignLeftBtn;
    public Button ProfileDesignRightBtn;
    public TextMeshProUGUI NowNickName;
    public TextMeshProUGUI NickNameChange;
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

    [Header("LogOutModal")] // 로그아웃 모달
    public GameObject LogOutModal;
    public bool LogOutOnOff = false;
    public Button LogOut;
    public Button Exit;

    void Awake()
    {
        DontDestroyOnLoad(this.gameObject);
    }


    public void Start()
    {
        Cursor.visible = true;

        ProfileChangeBtn.onClick.AddListener(EditProfile);
        ProfileChangeAcceptBtn.onClick.AddListener(EditAccept);

        Channel1Image.onClick.AddListener(OnChannel1ButtonClick);
        Channel2Image.onClick.AddListener(OnChannel2ButtonClick);
        Channel3Image.onClick.AddListener(OnChannel3ButtonClick);

        //EnterGameRoomBtn.onClick.AddListener(EnterGame); // 게임 채널 입장
        //ExitGameRooomBtn.onClick.AddListener(ExitGame); // 게임 채널 나가기
        MakeNewRoomBtn.onClick.AddListener(PlanetModalOn); // 행성 이름 입력 모달 띄우기 버튼
        PlanetEnter.onClick.AddListener(MakeNewRoom); // 행성명 입력 후 입장.
        PlanetCancel.onClick.AddListener(PlanetModalOff); // 새 대기방 cancel
        EnterNewRoomBtn.onClick.AddListener(TeamCodeModalOn); // 팀코드 모달 띄우기 버튼
        TeamCodeEnter.onClick.AddListener(EnterTeamCode); // 팀코드 입력으로 입장
        TeamcodeCancel.onClick.AddListener(TeamCodeModalOff); // 팀코드 입장 취소

        LogOut.onClick.AddListener(ToggleObject);
        Exit.onClick.AddListener(ToggleObject);
    }
    // 프로필 수정
    public void EditProfile()
    {
        ProfileChanger.SetActive(true);
        ProfilePage.SetActive(false);
    }

    // 프로필 수정 저장
    public void EditAccept()
    {
        ProfileChanger.SetActive(false);
        ProfilePage.SetActive(true);
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
                        break;
                    case 1:
                        Channel2Text.text = channelName;
                        channel2Id = channelId;
                        break;
                    case 2:
                        Channel3Text.text = channelName;
                        channel3Id = channelId;
                        break;
                }
            }
        }
        else
        {
            IfNoneRoom();
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

    // 1번 채널 상세 조회 요청 메시지 송신
    public void OnChannel1ButtonClick()
    {
        if (Channel1Text.text == "새 게임" || channel1Id == null)
        {
            IfNoneRoom();
        }
        else
        {
            // TCP Check
            tcpClientManager = TCPClientManager.Instance;
            if (tcpClientManager == null)
            {
                Debug.LogError("TCPClientManager가 초기화되지 않았습니다.");
                return;
            }
            if (tcpClientManager == null)
            {
                Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
                return;
            }
            NetworkStream stream = tcpClientManager.GetStream();
            if (stream == null)
            {
                Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
                return;
            }
            
            UserInfoManager userInfoManager = UserInfoManager.Instance;
            int userId = userInfoManager.GetUserId();

            ChannelDetailRequest request = new ChannelDetailRequest("channel", "getChannelDetails",  userId, channel1Id);

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
            // TCP Check
            tcpClientManager = TCPClientManager.Instance;
            if (tcpClientManager == null)
            {
                Debug.LogError("TCPClientManager가 초기화되지 않았습니다.");
                return;
            }
            if (tcpClientManager == null)
            {
                Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
                return;
            }
            NetworkStream stream = tcpClientManager.GetStream();
            if (stream == null)
            {
                Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
                return;
            }
            // 채널 상세 조회 메시지 송신
            UserInfoManager userInfoManager = UserInfoManager.Instance;
            int userId = userInfoManager.GetUserId();

            ChannelDetailRequest request = new ChannelDetailRequest("channel", "getChannelDetails", userId, channel2Id);

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
            // TCP Check
            tcpClientManager = TCPClientManager.Instance;
            if (tcpClientManager == null)
            {
                Debug.LogError("TCPClientManager가 초기화되지 않았습니다.");
                return;
            }
            if (tcpClientManager == null)
            {
                Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
                return;
            }
            NetworkStream stream = tcpClientManager.GetStream();
            if (stream == null)
            {
                Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
                return;
            }
            // 채널 상세 조회 메시지 송신
            UserInfoManager userInfoManager = UserInfoManager.Instance;
            int userId = userInfoManager.GetUserId();

            ChannelDetailRequest request = new ChannelDetailRequest("channel", "getChannelDetails", userId, channel3Id);

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

        // TCP Check
        tcpClientManager = TCPClientManager.Instance;
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager가 초기화되지 않았습니다.");
            return;
        }
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
            return;
        }
        NetworkStream stream = tcpClientManager.GetStream();
        if (stream == null)
        {
            Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
            return;
        }
        // 유저 정보
        UserInfoManager userInfoManager = UserInfoManager.Instance;
        int userId = userInfoManager.GetUserId();
        string nickname = userInfoManager.GetNickname();
        int avatar = userInfoManager.GetAvatar();
        // 방 입장 요청 보냄
        JoinRoomRequest request = new JoinRoomRequest("waitingRoomSession", "joinWaitingRoom", teamCode, userId, nickname, avatar);
        string json = JsonConvert.SerializeObject(request);
        tcpClientManager.SendTCPRequest(json);
    }

    // 방 입장 성공 반환 시 씬 전환
    public void EnterRoom()
    {
        SceneManager.LoadScene("WaitingRoom2");
        SignUpScene.SetActive(false);

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

            // TCP Check
            tcpClientManager = TCPClientManager.Instance;
            if (tcpClientManager == null)
            {
                Debug.LogError("TCPClientManager가 초기화되지 않았습니다.");
                return;
            }
            NetworkStream stream = tcpClientManager.GetStream();
            if (stream == null)
            {
                Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
                return;
            }

            // 저장된 유저 정보
            UserInfoManager userInfoManager = UserInfoManager.Instance;
            int userId = userInfoManager.GetUserId();
            string nickname = userInfoManager.GetNickname();
            int avatar = userInfoManager.GetAvatar();

            // 방 생성 메시지 발신
            MakeRoomRequest request = new MakeRoomRequest("waitingRoomSession", "createWaitingRoom", userId, nickname, avatar);
            string json = JsonConvert.SerializeObject(request);
            tcpClientManager.SendTCPRequest(json);
        }
        else
        {
            Debug.Log("행성 이름은 2글자 이상이어야 하며, 공백일 수 없습니다.");
        }
    }

    // 방 생성 성공시 씬 전환
    public void MakeRoom()
    {
        SignUpScene.SetActive(false);
        SceneManager.LoadScene("WaitingRoom1");

    }

   

    // 로그아웃 토글
    private void Update()
    {
        if (Input.GetKeyDown(KeyCode.Escape))
        {
            ToggleObject();
        }
        // LogOutModal이 null이 아닐 때만 접근
        if (LogOutModal != null)
        {
            LogOutModal.SetActive(LogOutOnOff);
        }
    }

    void ToggleObject()
    {
        // isActive 값을 반대로 변경하고 그에 따라 게임 오브젝트를 활성화 또는 비활성화
        LogOutOnOff = !LogOutOnOff;
    }



}
