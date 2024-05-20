using Newtonsoft.Json;
using System.Collections;
using TMPro;
using UnityEngine;
using UnityEngine.UI;

public class IngameChat : MonoBehaviour
{
    private bool isChatting;
    private CharacterMove characterMove;
    private AutoScroll autoScroll;
    public TMP_InputField chatInputField;

    // Start is called before the first frame update
    void Start()
    {
        isChatting = false;
        AuthControl.SetIngameChat(this);
        TCPMessageHandler.SetIngameChatInstance(this);
        chatInputField.interactable = false;
        autoScroll = FindObjectOfType<AutoScroll>();
    }

    // Update is called once per frame
    void Update()
    {
        if (Input.GetKeyDown(KeyCode.KeypadEnter) || Input.GetKeyDown(KeyCode.Return))
        {
            // 채팅
            InputChat();
        }
    }

    // 채팅 입력
    public void InputChat()
    {
        characterMove = FindObjectOfType<CharacterMove>();
        if (!isChatting)
        {
            chatInputField.interactable = true;
            chatInputField.Select();
            chatInputField.ActivateInputField();
            isChatting = true;
            characterMove.StartChatting(true);
        }
        else
        {
            if (!string.IsNullOrEmpty(chatInputField.text))
            {
                SubmitChatMessage(chatInputField.text);
            }
            chatInputField.interactable = false;
            isChatting = false;
            chatInputField.text = ""; // 입력 필드 초기화
            chatInputField.DeactivateInputField();
            characterMove.StartChatting(false);
        }
    }



    private void SubmitChatMessage(string message)
    {
        if (!string.IsNullOrEmpty(message))
        {
            int userId = UserInfoManager.Instance.GetUserId();
            string code = ChannelManager.Instance.GetTeamCode();
            if (code == null)
            {
                code = ChannelManager.Instance.GetChannelId();
            }
            chatMessage request = new chatMessage("chat", "sendChat", code, userId, message);
            string json = JsonConvert.SerializeObject(request);
            Debug.Log($"Serialized JSON: {json}");
            TCPClientManager.Instance.SendChatTCPRequest(json);
        }
    }
    // 채팅 수신
    public GameObject chatMessagePrefab;
    public Transform contentPanel;
    public void RenderChat(string nickname, string content)
    {
        if (contentPanel != null)
        {
            GameObject newMessage = Instantiate(chatMessagePrefab, contentPanel);
            TextMeshProUGUI textComponent = newMessage.GetComponent<TextMeshProUGUI>();
            if (textComponent != null)
            {
                textComponent.text = $"{nickname}: {content}";
            }
            StartCoroutine(DelayedLayoutUpdate());
        }
    }

    IEnumerator DelayedLayoutUpdate()
    {
        yield return new WaitForEndOfFrame();
        LayoutRebuilder.ForceRebuildLayoutImmediate(contentPanel.GetComponent<RectTransform>());
        if (autoScroll != null)
        {
            autoScroll.OnChatUpdated();
        }
    }

    public class chatMessage
    {
        public string type;
        public string eventName;
        public string teamCode;
        public int userId;
        public string content;
        public chatMessage(string type, string eventName, string teamCode, int userId, string content)
        {
            this.type = type;
            this.eventName = eventName;
            this.teamCode = teamCode;
            this.userId = userId;
            this.content = content;
        }
    }
}
