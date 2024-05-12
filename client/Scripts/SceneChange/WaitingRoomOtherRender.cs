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


public class WaitingRoomOtherRender : MonoBehaviour
{
    private TCPClientManager tcpClientManager;
    public string nowTeamCode;
    private CharacterMove characterMove; 


    [Header("TextField")]
    public TextMeshProUGUI playerCount;
    public TextMeshProUGUI teamCode;
    public TMP_InputField chatInputField;
    public TMP_Text chatDisplay; 

    [Header("Button")]
    public Button exitBtn;

    private void Update()
    {
        // ESC 키 입력
        if (Input.GetKeyDown(KeyCode.Escape))
        {
            ExitWaitingRoom();
        }
        if (Input.GetKeyDown(KeyCode.Return) || Input.GetKeyDown(KeyCode.KeypadEnter))
        {
            ActivateChatInputField();
        }
    }

    private void ActivateChatInputField()
    {
        // 채팅 입력 필드가 비활성화 상태라면 활성화
        if (!chatInputField.gameObject.activeSelf)
        {
            chatInputField.gameObject.SetActive(true);
        }

        // 채팅 입력 필드에 포커스
        chatInputField.Select();
        chatInputField.ActivateInputField();
    }

    void Start()
    {

        // CharacterMove 스크립트의 인스턴스가 있는지 확인
        if (characterMove != null)
        {
            // StartChatting 메서드를 호출
            characterMove.StartChatting();
        }
        else
        {
            Debug.LogError("CharacterMove 스크립트의 인스턴스를 찾을 수 없습니다.");
        }
        Cursor.visible = false;
        nowTeamCode = ChannelManager.instance.GetTeamCode();
        teamCode.text = nowTeamCode;
        exitBtn.onClick.AddListener(ExitWaitingRoom);
        TCPMessageHandler.SetWaitingRoomOtherRender(this);
        chatInputField.onEndEdit.AddListener(HandleChatInputSubmit);
        // 채팅 메시지 표시 컴포넌트 초기화
        if (chatDisplay == null)
        {
            Debug.LogError("ChatDisplay is not assigned in the inspector!");
        }
        characterMove = FindObjectOfType<CharacterMove>();
        if (characterMove != null)
        {
            characterMove.StartChatting();
        }
        else
        {
            Debug.LogError("CharacterMove 스크립트의 인스턴스를 찾을 수 없습니다.");
        }

    }

    // 방 나가기(방장이 아닌 경우)
    public void ExitWaitingRoom()
    {
        // 방 나가기 메시지 송신
        if (isVaildTCP())
        {
            Debug.Log("방 나가기");
            UserInfoManager userInfoManager = UserInfoManager.Instance;
            int userId = userInfoManager.GetUserId();

            // 방 나가기 메시지 송신
            ExitWaitingRoomRequest request = new ExitWaitingRoomRequest("waitingRoomSession", "leaveWaitingRoom", nowTeamCode, userId, false);
            string json = JsonConvert.SerializeObject(request);
            tcpClientManager.SendTCPRequest(json);

            // 인원 업데이트 메시지 송신
            UpdateCount request2 = new UpdateCount("waitingRoomSession", "getWaitingRoomHeadcount", nowTeamCode);
            string json2 = JsonConvert.SerializeObject(request2);
            tcpClientManager.SendTCPRequest(json2);


            AuthControl authControlInstance = GameObject.FindObjectOfType<AuthControl>();

            // AuthControl 인스턴스가 유효한지 확인 후 ExitRoomSelf 호출
            if (authControlInstance != null)
            {
                SceneManager.LoadScene("SignUp");
                authControlInstance.ExitRoomSelf();

            }
            else
            {
                
                Debug.LogError("AuthControl 인스턴스를 찾을 수 없습니다.");
            }

        }

    }
    public void UpdateCountValue(string count)
    {
        Debug.Log("count = " + count);
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
        TCPClientManager.Instance.SendTCPRequest(json);
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

        // 채팅 전송
    private void HandleChatInputSubmit(string input)
    {
        if (!string.IsNullOrEmpty(input) && (Input.GetKeyDown(KeyCode.Return) || Input.GetKeyDown(KeyCode.KeypadEnter)))
        {
            SubmitChatMessage(input);
            
            characterMove.StartChatting();
        }
    }
    private void SubmitChatMessage(string message)
    {
        if (!string.IsNullOrEmpty(message))
        {
            int userId = UserInfoManager.Instance.GetUserId();
            chatMessage request = new chatMessage("chat", nowTeamCode, userId, message);
            string json = JsonConvert.SerializeObject(request);
            TCPClientManager.Instance.SendTCPRequest(json);

            chatInputField.text = ""; // 입력 필드 초기화
            chatInputField.ActivateInputField(); // 입력 필드 재활성화
            characterMove.StartChatting();
        }
    }
    // 채팅 수신
    public void RenderChat(string nickname, string content)
    {
        if (chatDisplay != null)
        {
            chatDisplay.text += $"{nickname}: {content}\n"; // 새로운 메시지를 기존 텍스트에 추가
        }
    }

    public class chatMessage
    {
        public string type;
        public string teamCode;
        public int userId;
        public string content;
        public chatMessage(string type, string teamCode, int userId, string content)
        {
            this.type = type;
            this.teamCode = teamCode;
            this.userId = userId;
            this.content = content;
        }
    }

    public bool isVaildTCP()
    {
        // TCP Check
        tcpClientManager = TCPClientManager.Instance;
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager가 초기화되지 않았습니다.");
            return false;
        }
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
            return false;
        }
        NetworkStream stream = tcpClientManager.GetStream();
        if (stream == null)
        {
            Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
            return false;
        }

        return true;
    }
}
