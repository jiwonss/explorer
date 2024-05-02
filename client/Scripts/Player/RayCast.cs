using System.Collections;
using System.Collections.Generic;
using UnityEngine;

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
        mapItem = new GetMapItem();
        farming = new ObjectFarming();
    }

    void Update()
    {
        if (Input.GetMouseButtonDown(0)) // 마우스 왼쪽 버튼 클릭
        {
            clickStartTime = Time.time;
            hasEventOccurred = false;
        }

        FireRayFromScreenCenter();

        if (Input.GetMouseButton(0)) // 마우스 왼쪽 버튼이 눌러져 있는 동안
        {
            // 클릭을 시작한 후 지정한 시간 이상 지속되는지 확인
            if (Input.GetMouseButton(0)) // 마우스 왼쪽 버튼이 눌러져 있는 동안
    {
        // 클릭을 시작한 후 지정한 시간 이상 지속되는지 확인
        if (Time.time - clickStartTime >= holdDuration)
        {
            // hold true
            hasEventOccurred = true;

        }
    }
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
                if (hit.collider.CompareTag("Item"))
                {
                    // Item일 경우 Item 관련 요청
                    mapItem.Awake();
                    mapItem.GetItem(hit.transform.position, hit.transform.rotation);
                }
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
            }
            
        }
    }
}
