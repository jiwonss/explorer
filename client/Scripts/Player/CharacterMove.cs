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
    public CharacterController characterController;

    public float moveSpeed = 3f;
    public float jumpSpeed = 3f;
    public float gravity = -10f;
    public float yVelocity = 0;

    private bool isMoving = false;
    private float sendTimer = 0f;
    private float sendInterval = 0.5f;


    void Start()
    {
        cameraTransform = GetComponentInChildren<Camera>().transform;
        characterController = GetComponent<CharacterController>();

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

    void Update()
    {
        float h = Input.GetAxis("Horizontal");
        float v = Input.GetAxis("Vertical");

        Vector3 moveDirection = new Vector3(h, 0, v);
        moveDirection = cameraTransform.TransformDirection(moveDirection);
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

        isMoving = (h != 0 || v != 0);

        sendTimer += Time.deltaTime;
        if (sendTimer >= sendInterval && isMoving)
        {
            SendPlayerPosition();
            sendTimer = 0f; // 시간초기화
        }
    }

    void Dash()
    {
        if (!Input.GetKeyDown(KeyCode.S))
        {
            if (Input.GetKeyDown(KeyCode.LeftShift))
            {
                moveSpeed += 10f;
            }

            if (Input.GetKeyUp(KeyCode.LeftShift))
            {
                moveSpeed -= 10f;
            }
        }
    }

    void SendPlayerPosition()
    {
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager 없음!");
            return;
        }

        // TCPClientManager 연결정보
        NetworkStream stream = tcpClientManager.GetStream();

        if (stream == null)
        {
            Debug.LogError("TCPClientManager NetworkStream이 존재하지않음");
            return;
        }

        string playerPosition = transform.position.ToString();
        string playerRotation = cameraTransform.rotation.ToString();
        //Debug.Log(playerPosition);
        //Debug.Log(playerRotation);
        int mapId = 0;
        int channelId = 0;
        UserInfoManager userInfoManager = UserInfoManager.Instance;
        int userId = userInfoManager.GetUserId();

        float posX = transform.position.x;
        float posY = transform.position.y;
        float posZ = transform.position.z;
        float rotX = transform.rotation.eulerAngles.x;
        float rotY = transform.rotation.eulerAngles.y;
        float rotZ = transform.rotation.eulerAngles.z;

        playerMovement data = new playerMovement("ingame", "moving", mapId, channelId, userId, posX, posY, posZ, rotX, rotY, rotZ);
        string json = JsonConvert.SerializeObject(data);

        tcpClientManager.SendTCPRequest(json);
    }

    public class playerMovement
    {
        public string type;
        public string category;
        public int mapId;
        public int channelId;
        public int userId;
        public float posX;
        public float posY;
        public float posZ;
        public float rotX;
        public float rotY;
        public float rotZ;
        public playerMovement(string type, string category, int mapId, int channelId, int userId, float posX, float posY, float posZ, float rotX, float rotY, float rotZ)
        {
            this.type = type;
            this.category = category;
            this.mapId = mapId;
            this.channelId = channelId;
            this.userId = userId;
            this.posX = posX;
            this.posX = posY;
            this.posZ = posZ;
            this.rotX = rotX;
            this.rotY = rotY;
            this.rotZ = rotZ;
        }
    }

}
