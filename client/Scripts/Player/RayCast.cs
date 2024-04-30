using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class RayCast : MonoBehaviour
{
    public float maxDistance = 15f;
    private GetMapItem mapItem;

    void Start()
    {
        mapItem = new GetMapItem();
    }

    void Update()
    {
        if (Input.GetMouseButtonDown(0)) // 마우스 왼쪽 버튼 클릭
        {
            FireRayFromScreenCenter();
        }
        else if (Input.GetKeyDown(KeyCode.Z))
        {
            FireRayFromScreenCenter();
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
            if (Input.GetKeyDown(KeyCode.Z) && hit.collider.CompareTag("Item"))
            {
                Debug.Log("z clicked, Item");
                // Item일 경우 Item 관련 요청
                mapItem.Awake();
                mapItem.GetItem(hit.transform.position, hit.transform.rotation);
            }
        }
    }
}
