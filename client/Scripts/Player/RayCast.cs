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
public class RayCast : MonoBehaviour
{
    public float maxDistance = 15f;
    public float holdDuration = 0.5f; // 클릭을 유지하는 시간

    private GetMapItem mapItem;
    private ObjectFarming farming;
    private float clickStartTime = 0f; // 클릭이 시작된 시간
    private bool hasEventOccurred = false;

    void Start()
    {
        mapItem = gameObject.AddComponent<GetMapItem>(); 
        farming = gameObject.AddComponent<ObjectFarming>();
    }

    void Update()
    {
        if (Input.GetMouseButtonDown(0) || Input.GetKeyDown(KeyCode.Z)) // 마우스 왼쪽 버튼 클릭
        {
            clickStartTime = Time.time;
            hasEventOccurred = false;
            FireRayFromScreenCenter();
        }

        if (Input.GetMouseButton(0)) // 마우스 왼쪽 버튼이 눌러져 있는 동안
        {
            // 클릭을 시작한 후 지정한 시간 이상 지속되는지 확인
            if (Time.time - clickStartTime >= holdDuration)
            {
                hasEventOccurred = true;
                FireRayFromScreenCenter();
            }
        }

        if (Input.GetMouseButtonUp(0)) // 마우스 왼쪽 버튼을 놓았을 때
        {
            hasEventOccurred = false;
            farming.CancleFarming();
        }
    }

    void FireRayFromScreenCenter()
    {
        // 메인 카메라를 기준으로 화면 중심 방향 계산
        Camera mainCamera = Camera.main;
        Vector3 screenCenter = new Vector3(Screen.width / 2, Screen.height / 2, 0);
        Ray ray = mainCamera.ScreenPointToRay(screenCenter);

        // 레이캐스트를 화면 중심 방향으로 보냄
        Debug.DrawRay(ray.origin, ray.direction * maxDistance, Color.red);
        RaycastHit hit;
        if (Physics.Raycast(ray, out hit, maxDistance))
        {
            if(hasEventOccurred)
            {
                if (hit.collider.CompareTag("Farming"))
                {
                    farming.Awake();
                    farming.OjectFarm(hit, hit.transform.position);
                }
                // 좌클릭 지속 x 시 진행중인 메서드 취소
                if (Input.GetMouseButtonUp(0))
                {
                    hasEventOccurred = false;
   
                    farming.CancleFarming();
                }

            }
            else
            {
                //if (hit.collider.CompareTag("Item") && Input.GetKeyDown(KeyCode.Z))
                //{
                //    // Item일 경우 Item 관련 요청
                //    mapItem.Awake();
                //    mapItem.GetItem(hit.transform.position, hit.transform.rotation);
                //    Debug.Log("Item");
                //}
                if (hit.collider.CompareTag("Rocket"))
                {
                    // 로켓 탑승(모달 렌더)
                    SpaceShipPageStart rocketScript = hit.collider.GetComponent<SpaceShipPageStart>();
                    if (rocketScript != null)
                    {
                        // TCP로 탑승 통신 필요.
                        rocketScript.Onclick();
                    }
                    else
                    {
                        Debug.Log("rocketScript == null");
                    }
                }
                if (hit.collider.CompareTag("ElementLab"))
                {
                    Debug.Log("Lab Clicked");
                    // 연구소 입장 메시지 송신
                    // 연구소 초기 상태 업데이트 메시지 송신
                    string channelId = ChannelManager.Instance.GetChannelId();
                    int userId = UserInfoManager.Instance.GetUserId();
                    int labId = 0;
                    ElementLabInit request = new ElementLabInit("ingame", "laboratory", "enterLab", channelId, userId, labId);
                    string json = JsonConvert.SerializeObject(request);
                    TCPClientManager.Instance.SendMainTCPRequest(json);
                }
            }
            
        }
    }
}

public class ElementLabInit
{
    public string type;
    public string category;
    public string eventName;
    public string channelId;
    public int userId;
    public int labId;

    public ElementLabInit(string type, string category, string eventName, string channelId, int userId, int labId)
    {
        this.type = type;
        this.category = category;
        this.eventName = eventName;
        this.channelId = channelId;
        this.userId = userId;
        this.labId = labId;
    }
}
