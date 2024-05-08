using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Net.Sockets;
using System.IO; 
using Newtonsoft.Json; 

public class CharacterMove : MonoBehaviour
{
    public TCPClientManager tcpClientManager;

    public Transform cameraTransform;
    public Rigidbody rb;

    public float moveSpeed = 1f;
    public float jumpSpeed = 1f;

    private bool isGrounded;

    private float sendTimer = 0f;
    private float sendInterval = 0.08f;


    void Start()
    {
        rb = GetComponent<Rigidbody>();
        cameraTransform = GetComponentInChildren<Camera>().transform;

        tcpClientManager = TCPClientManager.Instance;
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager 없음");
        }
        else
        {
            SetTCPClientManager(tcpClientManager);
        }
    }
    public void SetTCPClientManager(TCPClientManager clientManager)
    {
        tcpClientManager = clientManager;
    }
    void OnCollisionEnter(Collision collision)
    {
        // 땅과의 충돌 감지
        if (collision.contacts[0].normal.y > 0.5)  // 충돌 표면의 방향이 위쪽인 경우만
        {
            isGrounded = true;
        }
    }

    void OnCollisionExit(Collision collision)
    {
        // 땅에서 벗어남을 감지
        isGrounded = false;
    }

    void Update()
    {
        float h = Input.GetAxis("Horizontal");
        float v = Input.GetAxis("Vertical");

        Vector3 moveDirection = new Vector3(h, 0.0f, v);

        if (Input.GetKeyDown(KeyCode.Space) && isGrounded)
        {
            rb.AddForce(Vector3.up * jumpSpeed, ForceMode.Impulse);

            //SendPlayerPosition();
        }

        if (!Input.GetKeyDown(KeyCode.S))
        {
            if (Input.GetKeyDown(KeyCode.LeftShift))
            {
                Dash();
            }

            if (Input.GetKeyUp(KeyCode.LeftShift))
            {
                NonDash();
            }
        }

        transform.Translate(moveDirection * moveSpeed * Time.deltaTime);

        // 타이머 업데이트
        //sendTimer += Time.deltaTime;
        //if (sendTimer >= sendInterval)
        //{
        //    // 위치를 전송하는 간격이 되면 플레이어 위치를 서버로 전송
        //    SendPlayerPosition();
        //    sendTimer = 0f; // 타이머 초기화
        //}
    }


    void Dash()
    {
        moveSpeed += 2f;
    }
    void NonDash()
    {
        moveSpeed -= 2f;
    }

    void SendPlayerPosition()
    {
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager 없음!");
            return;
        }

        // TCPClientManager 연결정보
        //NetworkStream stream = tcpClientManager.GetStream();

        //if (stream == null)
        //{
        //    Debug.LogError("TCPClientManager NetworkStream이 존재하지않음");
        //    return;
        //}

        //string playerPosition = transform.position.ToString();
        //string playerRotation = cameraTransform.rotation.ToString();
        ////Debug.Log(playerPosition);
        ////Debug.Log(playerRotation);
        //int mapId = 0;
        ////int channelId = 0;
        //string teamCode = TCPMessageHandler.GetTeamCode();
        //UserInfoManager userInfoManager = UserInfoManager.Instance;
        //int userId = userInfoManager.GetUserId();

        //float posX = transform.position.x;
        //float posY = transform.position.y;
        //float posZ = transform.position.z;
        //float rotX = transform.rotation.eulerAngles.x;
        //float rotY = transform.rotation.eulerAngles.y;
        //float rotZ = transform.rotation.eulerAngles.z;

        //string position = posX + ":" + posY + ":" + posZ + ":" + rotX + ":" + rotY + ":" + rotZ; 


        //playerMovement data = new playerMovement("ingame", "move", "moving", mapId, teamCode, userId, position);
        //string json = JsonConvert.SerializeObject(data);

        //tcpClientManager.SendTCPRequest(json);
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
