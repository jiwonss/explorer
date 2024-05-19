using System.Collections;
using UnityEngine;
using UnityEngine.UI;
using TMPro;
using UnityEngine.SceneManagement;
using Newtonsoft.Json;


public class WaitingRoomOtherRender : MonoBehaviour
{
    public string nowTeamCode;
    private bool isChatting;
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
            ExitWaitingRoom();
        }
    }


    void Start()
    {
        isChatting = false;
        Cursor.visible = false;
        nowTeamCode = ChannelManager.instance.GetTeamCode();
        teamCode.text = nowTeamCode;

        exitBtn.onClick.AddListener(ExitWaitingRoom);
        TCPMessageHandler.SetWaitingRoomOtherRender(this);

    }

    // 방 나가기(방장이 아닌 경우)
    public void ExitWaitingRoom()
    {
        // 방 나가기 메시지 송신
        UserInfoManager userInfoManager = UserInfoManager.Instance;
        int userId = userInfoManager.GetUserId();

        // 방 나가기 메시지 송신
        ExitWaitingRoomRequest request = new ExitWaitingRoomRequest("waitingRoomSession", "leaveWaitingRoom", nowTeamCode, userId, false);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendMainTCPRequest(json);

        // 인원 업데이트 메시지 송신
        UpdateCount request2 = new UpdateCount("waitingRoomSession", "getWaitingRoomHeadcount", nowTeamCode);
        string json2 = JsonConvert.SerializeObject(request2);
        TCPClientManager.Instance.SendMainTCPRequest(json2);
        AuthControl authControlInstance = GameObject.FindObjectOfType<AuthControl>();
        // AuthControl 인스턴스가 유효한지 확인 후 ExitRoomSelf 호출
        if (authControlInstance != null)
        {
            SceneManager.LoadScene("SignUp");
            authControlInstance.ExitRoomSelf();
            ChannelManager.Instance.SetTeamCode(null);
            TCPClientManager.Instance.DisconnectChatServer();
            Cursor.visible = true;
        }
        else
        {
            Debug.LogError("AuthControl 인스턴스를 찾을 수 없습니다.");
        }
    }
    public void UpdateCountValue(string count)
    {
        playerCount.text = count;
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
    

    public class ExitWaitingRoomRequest
    {
        public string type;
        public string eventName;
        public string teamCode;
        public int userId;
        public bool isLeader;

        public ExitWaitingRoomRequest(string type, string eventName, string teamCode, int userId, bool isLeader)
        {
            this.type = type;
            this.eventName = eventName;
            this.teamCode = teamCode;
            this.userId = userId;
            this.isLeader = isLeader;
        }
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




    // 게임시작 메시지 수신
    public void StartGameResponse()
    {
        SceneManager.LoadScene("IngameTest");
    }
}
