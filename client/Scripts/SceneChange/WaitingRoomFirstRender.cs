using System.Collections;
using UnityEngine;
using UnityEngine.UI;
using TMPro;
using UnityEngine.SceneManagement;
using Newtonsoft.Json;

public class WaitingRoomFirstRender : MonoBehaviour
{
    public string nowTeamCode;
    private CharacterMove characterMove; 

    [Header("TextField")]
    public TextMeshProUGUI playerCount;
    public TextMeshProUGUI teamCode;

    [Header("Button")]
    public Button exitBtn;

    private void Update()
    {
        // ESC 키 입력
        if (Input.GetKeyDown(KeyCode.Escape))
        {
            DeleteWaitingRoom();
        }
        // F5 키 입력 감지
        if (Input.GetKeyDown(KeyCode.F5))
        {
            StartGame();
        }
    }

    void Start()
    {
        Cursor.visible = false;
        nowTeamCode = ChannelManager.instance.GetTeamCode();
        teamCode.text = nowTeamCode;

        exitBtn.onClick.AddListener(DeleteWaitingRoom);
        TCPMessageHandler.SetWaitingRoomFirstRender(this);

        // chatInputField.onEndEdit.AddListener(InputChat());
    }

    // 방 삭제(방장이 나가는 경우)
    public void DeleteWaitingRoom()
    {
        Debug.Log("방 삭제");
        UserInfoManager userInfoManager = UserInfoManager.Instance;
        int userId = userInfoManager.GetUserId();

        DeleteWaitingRoomRequest request = new DeleteWaitingRoomRequest("waitingRoomSession", "deleteWaitingRoom", nowTeamCode, userId, true);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendMainTCPRequest(json);

        AuthControl authControlInstance = GameObject.FindObjectOfType<AuthControl>();
        if (authControlInstance != null)
        {
            SceneManager.LoadScene("SignUp", LoadSceneMode.Single);
            authControlInstance.DeleteRoom();
            ChannelManager.Instance.SetTeamCode(null);
            // TCPClientManager.Instance.DisconnectChatServer();
        }
        else
        {
            Debug.LogWarning("AuthControl 인스턴스를 찾을 수 없습니다.");
        }
    
    }

    public void UpdateCountValue(string count)
    {
        playerCount.text = count;
    }
    public class UpdateCount
    {
        public string type;
        public string eventName;
        public string teamCode;
        public UpdateCount(string type, string eventName, string teamCode)
        {
            this.type = type;
            this.eventName = eventName;
            this.teamCode = teamCode;
        }
    }


    // 게임 시작(방장이 게임 시작)
    public void StartGame()
    {
        // 게임 시작 메시지 송신
        Debug.Log("게임시작");
        string channelName = ChannelManager.Instance.GetChannelName();
        GameStartRequest request = new GameStartRequest("ingameSession", "startGame", nowTeamCode, channelName);
        string json = JsonConvert.SerializeObject(request);
        SceneManager.LoadScene("IngameTest");
        TCPClientManager.Instance.SendMainTCPRequest(json);

    }
    public class GameStartRequest
    {
        public string type;
        public string eventName;
        public string teamCode;
        public string channelName;
        

        // public GameStartRequest(string type, string eventName, string teamCode, string channelName)
        public GameStartRequest(string type, string eventName, string teamCode, string channelName)
        {
            this.type = type;
            this.eventName = eventName;
            this.teamCode = teamCode;
            this.channelName = channelName;
            
        }
    }

    // 게임시작 메시지 수신
    public void StartGameResponse()
    {
        // SceneManager.LoadScene("IngameTest");
        // 메인 맵으로 이동
        SceneManager.LoadScene("IngameTest2");
    }

    // 플레이어 나가기 메시지 수신
    public void ExitUserReceive(int targetUserId)
    {
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
        // 코루틴 시작
        StartCoroutine(WaitAndSendUpdateCount());
    }

    // 3초 기다린 후 인원 업데이트 메시지 송신
    IEnumerator WaitAndSendUpdateCount()
    {
        yield return new WaitForSeconds(2);

        UpdateCount request = new UpdateCount("waitingRoomSession", "getWaitingRoomHeadcount", nowTeamCode);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendMainTCPRequest(json);
    }

    public class DeleteWaitingRoomRequest
    {
        public string type;
        public string eventName;
        public string teamCode;
        public int userId;
        public bool isLeader;

        public DeleteWaitingRoomRequest(string type, string eventName, string teamCode, int userId, bool isLeader)
        {
            this.type = type;
            this.eventName = eventName;
            this.teamCode = teamCode;
            this.userId = userId;
            this.isLeader = isLeader;
        }
    }

}
