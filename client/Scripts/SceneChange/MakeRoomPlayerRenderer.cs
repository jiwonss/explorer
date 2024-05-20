using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MakeRoomPlayerRenderer : MonoBehaviour
{
    public GameObject prefab0;
    public GameObject prefab1;
    public GameObject prefab2;
    public GameObject prefab3;
    public GameObject prefab4;
    public GameObject prefab5;

    private bool hasInitialized = false;

    private void Awake()
    {
        if (!hasInitialized)
        {
            hasInitialized = true;

            // 클라이언트 정보를 가져와서 실행
            // 현재 클라이언트의 정보 가져오기
            UserInfoManager userInfoManager = UserInfoManager.Instance;
            int userId = userInfoManager.GetUserId();
            string nickname = userInfoManager.GetNickname();
            int avatar = userInfoManager.GetAvatar();

            // 선택된 아바타에 따라 플레이어 프리팹 설정
            GameObject selectedPrefab;
            switch (avatar)
            {
                case 0:
                    selectedPrefab = prefab0;
                    break;
                case 1:
                    selectedPrefab = prefab1;
                    break;
                case 2:
                    selectedPrefab = prefab2;
                    break;
                case 3:
                    selectedPrefab = prefab3;
                    break;
                case 4:
                    selectedPrefab = prefab4;
                    break;
                case 5:
                    selectedPrefab = prefab5;
                    break;
                default:
                    selectedPrefab = null;
                    break;
            }

            if (selectedPrefab != null)
            {
                // 플레이어 오브젝트 생성
                GameObject newPlayer = Instantiate(selectedPrefab, new Vector3(0f, 0f, 0f), Quaternion.identity);

                // 플레이어 정보 설정
                PlayerInfo playerInfo = newPlayer.GetComponent<PlayerInfo>();
                if (playerInfo != null)
                {
                    playerInfo.SetUserInfo(userId, nickname, avatar);
                }
                else
                {
                    Debug.LogError("PlayerInfo component not found on player prefab.");
                }

                // 플레이어 프리팹 안에 있는 카메라를 찾음
                Camera playerCamera = newPlayer.GetComponentInChildren<Camera>();
                if (playerCamera != null)
                {
                    // 클라이언트의 메인 카메라를 찾아서 새로운 플레이어의 카메라로 설정
                    Camera mainCamera = Camera.main;
                    if (mainCamera != null)
                    {
                        // 메인 카메라의 위치와 회전을 새로운 플레이어의 카메라로 설정
                        mainCamera.transform.position = playerCamera.transform.position;
                        mainCamera.transform.rotation = playerCamera.transform.rotation;

                        // 카메라 정보 관리자를 사용하여 메인 카메라 업데이트
                        CameraInfoManager cameraManager = FindObjectOfType<CameraInfoManager>();
                        if (cameraManager != null)
                        {
                            cameraManager.SetAsMainCamera(playerCamera);
                        }
                        else
                        {
                            Debug.LogError("CameraInfoManager instance not found.");
                        }
                    }
                    else
                    {
                        Debug.LogError("Main camera not found.");
                    }
                }
                else
                {
                    Debug.LogError("Player camera not found.");
                }

            }
            else
            {
                Debug.LogError("Selected prefab is null.");
            }
        }
    }
}
