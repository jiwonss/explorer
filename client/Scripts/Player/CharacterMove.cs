using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Net.Sockets;
using System.IO; 

public class CharacterMove : MonoBehaviour
{
    private TCPClientManager tcpClientManager;

    void OnCollisionEnter(Collision collision)
    {
        // ������ �浹 ����
        if (collision.contacts[0].normal.y > 0.5)  // �浹 ǥ���� ������ ������ ��츸
        {
            isGrounded = true;
        }
    }

    private bool isGrounded;

    void OnCollisionExit(Collision collision)
    {
        // ������ ����� ����
        isGrounded = false;
    }
    //private TCPClientManager tcpClientManager;

    //private TcpClient client;
    //private NetworkStream stream;

    //private IPAddress serverIp;
    //private int serverPort;

    //public Transform cameraTransform;
    public Rigidbody rb;

    public float moveSpeed = 1f;
    public float jumpSpeed = 1f;

    private bool isMoving = false;
    void NonDash()
    {
        moveSpeed -= 2f;
    }
    private float sendInterval = 0.5f; // 0.5

    void SendPlayerPosition()
    {
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager 없음.");
            return;
        }

        NetworkStream stream = tcpClientManager.GetStream();

        if (stream == null)
        {
            Debug.LogError("TCPClientManager stream 없음.");
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


    void Start()
    {
        // 캐릭터 이동 초기화
        cameraTransform = GetComponentInChildren<Camera>().transform;
        characterController = GetComponent<CharacterController>();

        // TCPClientManager 인스턴스 가져오기
        tcpClientManager = TCPClientManager.Instance;
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager ");
        }
        else
        {
            // TCPClientManager 인스턴스
            SetTCPClientManager(tcpClientManager);
        }
    }

    void Dash()
    {
        moveSpeed += 2f;
    }

    public void SetTCPClientManager(TCPClientManager clientManager)
    {
        tcpClientManager = clientManager;
    }

    void Update()
    {
        float h = Input.GetAxis("Horizontal");
        float v = Input.GetAxis("Vertical");

        Vector3 moveDirection = new Vector3(h, 0.0f, v);

        if (Input.GetKeyDown(KeyCode.Space)&& isGrounded)
        {
            rb.AddForce(Vector3.up * jumpSpeed , ForceMode.Impulse);

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


        isMoving = (h != 0 || v != 0);

        //// 0.5�ʿ� �� ���� �÷��̾� ��ġ�� ����
        //sendTimer += Time.deltaTime;
        //if (sendTimer >= sendInterval && isMoving)
        //{
        //SendPlayerPosition();
        //sendTimer = 0f; // Ÿ�̸� �ʱ�ȭ
        //}

        transform.Translate(moveDirection * moveSpeed * Time.deltaTime);
        sendTimer += Time.deltaTime;
        if (sendTimer >= sendInterval && isMoving)
        {
            SendPlayerPosition();
            sendTimer = 0f; // Ÿ�̸� �ʱ�ȭ
        }
    }

    //void SendPlayerPosition()
    //{
    //    string playerPosition = transform.position.ToString();
    //    string playerRotation = cameraTransform.rotation.ToString();
    //    //Debug.Log(playerPosition);
    //    //Debug.Log(playerRotation);

    //    // �÷��̾��� ���� ��ġ �� ȸ�� ������ JSON���� ��ȯ
    //    string json = "{\"type\":\"ingame\",\"event\":\"moving\",\"mapId\":0,\"channelId\":1,\"userId\":1," +
    //        "\"posX\":" + transform.position.x + ",\"posY\":" + transform.position.y + ",\"posZ\":" + transform.position.z +
    //        ",\"rotX\":" + transform.rotation.eulerAngles.x + ",\"rotY\":" + transform.rotation.eulerAngles.y +
    //        ",\"rotZ\":" + transform.rotation.eulerAngles.z + "}";

    //    // JSON�� ������ ����
    //    byte[] data = Encoding.UTF8.GetBytes(json);
    //    stream.Write(data, 0, data.Length);
    //}

    //// ��������
    //private void OnDestroy()
    //{
    //    tcpClientManager.Disconnect();
    //}

}
