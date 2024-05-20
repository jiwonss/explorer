using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Net.Sockets;
using System.IO;
using Newtonsoft.Json;

public class CharacterMove : MonoBehaviour
{

    //public TCPClientManager tcpClientManager;

    //public TCPClientManager tcpClientManager;

    //이전 코드 남겨두기
    //public Transform cameraTransform;
    //public CharacterController characterController;
    //public float moveSpeed = 5f;
    //public float jumpSpeed = 3f;
    //public float gravity = -10f;
    //public float yVelocity = 0;
    public CharacterMove characterMove;
    public Transform cameraTransform;
    public CharacterController characterController;

    public float moveSpeed = 5f;
    public float jumpSpeed = 5f;
    public float yVelocity = 0;
    public float gravity = -10f;

    private float sendTimer = 0f;
    public float sendInterval = 0.8f;

    private bool isChatting;



    void Start()
    {
        //rb = GetComponent<Rigidbody>();
        cameraTransform = GetComponentInChildren<Camera>().transform;
        cameraTransform = GetComponentInChildren<Camera>().transform;
        characterController = GetComponent<CharacterController>();
        isChatting = false;
    }

    void Update()
    {

        if (isChatting)
            return;

        float h = Input.GetAxis("Horizontal");
        float v = Input.GetAxis("Vertical");

        Vector3 moveDirection = new Vector3(h, 0, v);
        moveDirection = transform.TransformDirection(moveDirection);
        moveDirection *= moveSpeed;

        if (characterController.isGrounded)
        {
            yVelocity = 0;
            if (Input.GetKeyDown(KeyCode.Space))
            {
                yVelocity = jumpSpeed;
                SendPlayerPosition();
            }
        }

        yVelocity += (gravity * Time.deltaTime);
        moveDirection.y = yVelocity;
        characterController.Move(moveDirection * Time.deltaTime);

        Dash();

        // 움직임이나 회전이 발생할 때마다 전송
        if (Mathf.Abs(h) > 0 || Mathf.Abs(v) > 0 || Input.GetKeyDown(KeyCode.Space))
        {

            SendPlayerPosition();
        }

        sendTimer += Time.deltaTime;
        if (sendTimer >= sendInterval)
        {
            sendTimer = 0f;
        }
    }



    void Dash()
    {

        if (Input.GetKeyDown(KeyCode.LeftShift))
        {
            moveSpeed += 2f;
        }

        if (Input.GetKeyUp(KeyCode.LeftShift))
        {
            moveSpeed -= 2f;
        }
    }


    void SendPlayerPosition()
    {

        // 플레이어의 위치와 회전 가져오기
        Vector3 playerPosition = transform.position;
        Quaternion playerRotation = transform.rotation;

        // 위치와 회전 정보를 문자열로 변환
        string position = playerPosition.x + ":" + playerPosition.y + ":" + playerPosition.z + ":"
                        + playerRotation.eulerAngles.x + ":" + playerRotation.eulerAngles.y + ":" + playerRotation.eulerAngles.z;

        // 기타 정보 설정
        int mapId = 0;
        string sendCode = ChannelManager.Instance.GetTeamCode();
        int userId = UserInfoManager.Instance.GetUserId();

        if (sendCode == null)
        {
            sendCode = ChannelManager.Instance.GetChannelId();
        }

        // 플레이어 이동 데이터 생성
        playerMovement data = new playerMovement("ingame", "moving", "move", mapId, sendCode, userId, position);
        string json = JsonConvert.SerializeObject(data);

        // 서버로 데이터 전송
        TCPClientManager.Instance.SendMainTCPRequest(json);
    }

    public void StartChatting(bool isChat)
    {
        isChatting = isChat;

    }

    public class playerMovement
    {
        public string type;
        public string category;
        public string eventName;
        public int mapId;
        public string channelId;
        public int userId;
        public string position;

        public playerMovement(string type, string category, string eventName, int mapId, string channelId, int userId, string position)
        {
            this.type = type;
            this.category = category;
            this.eventName = eventName;
            this.mapId = mapId;
            this.channelId = channelId;
            this.userId = userId;
            this.position = position;
        }
    }
}