using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Net.Sockets;
using System.IO; 

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

        string json = "{\"type\":\"ingame\",\"event\":\"moving\",\"mapId\":0,\"channelId\":1,\"userId\":1," +
            "\"posX\":" + transform.position.x + ",\"posY\":" + transform.position.y + ",\"posZ\":" + transform.position.z +
            ",\"rotX\":" + transform.rotation.eulerAngles.x + ",\"rotY\":" + transform.rotation.eulerAngles.y +
            ",\"rotZ\":" + transform.rotation.eulerAngles.z + "}";


        tcpClientManager.SendTCPRequest(json);
    }

}
