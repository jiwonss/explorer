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

public class IngamePlayerRenderer : MonoBehaviour
{
    public GameObject player0;
    public GameObject player1;
    public GameObject player2;
    public GameObject player3;
    public GameObject player4;
    public GameObject player5;

    private bool hasInitialized = false;

    private void Awake()
    {
        
    }

    public void Start()
    {
        TCPMessageHandler.SetIngamePlayerRendererInstance(this);
    }

    //public void StartCoroutine(JObject data)
    //{
    //    StartCoroutine(StartPositionInit(data));
    //}


    //public IEnumerator StartPositionInit(JObject data)
    //{
    //    Debug.Log("플레이어 생성 코루틴 진입");
    //    yield return new WaitForSeconds(3.0f);

    //    SetInitPosition(data);
    //}
    public void SetInitPosition(JObject data)
    {

        if (!hasInitialized)
        {
            hasInitialized = true;
            JArray positionsArray = (JArray)data["dataBody"]["positions"];
            foreach (JToken item in positionsArray)
            {
                string positionStr = (string)item["position"];
                int userId = (int)item["userId"];
                string nickname = (string)item["nickname"];
                int avatar = (int)item["avatar"];

                string[] coordinates = positionStr.Split(':');
                if (coordinates.Length == 6)
                {
                    float x = float.Parse(coordinates[0]);
                    float y = float.Parse(coordinates[1]);
                    float z = float.Parse(coordinates[2]);

                    Vector3 position = new Vector3(x, y, z);
                    Quaternion rotation = Quaternion.Euler(0, 0, 0);

                    // 프리팹 설정
                    // 현재 클라이언트의 userId일 경우
                    GameObject selectedPrefab;
                    if (UserInfoManager.Instance.GetUserId() == userId)
                    {
                        switch (avatar)
                        {
                            case 0:
                                selectedPrefab = player0;
                                break;
                            case 1:
                                selectedPrefab = player1;
                                break;
                            case 2:
                                selectedPrefab = player2;
                                break;
                            case 3:
                                selectedPrefab = player3;
                                break;
                            case 4:
                                selectedPrefab = player4;
                                break;
                            case 5:
                                selectedPrefab = player5;
                                break;
                            default:
                                selectedPrefab = null;
                                break;
                        }
                        // 플레이어를 생성하고 위치 및 회전 정보를 설정
                        if (selectedPrefab != null)
                        {
                            GameObject newPlayer = Instantiate(selectedPrefab, position, rotation);
                            // 생성된 플레이어에 대한 정보 설정
                            PlayerInfo playerInfo = newPlayer.GetComponent<PlayerInfo>();
                            if (playerInfo != null)
                            {
                                playerInfo.userId = userId;
                                playerInfo.avatar = avatar;
                                playerInfo.nickname = nickname;
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
                            // 플레이어 오브젝트를 활성화
                            newPlayer.SetActive(true);
                        }
                    }
                    
                }
            }
        }
    }

    public void SetInitPositionRestart(JObject data)
    {
        string positionStr = (string)data["dataBody"]["position"];
        int userId = (int)data["dataBody"]["userId"];
        string nickname = (string)data["dataBody"]["nickname"];
        int avatar = (int)data["dataBody"]["avatar"];

        string[] coordinates = positionStr.Split(':');
            
        float x = float.Parse(coordinates[0]);
        float y = float.Parse(coordinates[1]);
        float z = float.Parse(coordinates[2]);

        Vector3 position = new Vector3(x, y, z);
        Quaternion rotation = Quaternion.Euler(0, 0, 0);

        GameObject selectedPrefab;

        switch (avatar)
        {
            case 0:
                selectedPrefab = player0;
                break;
            case 1:
                selectedPrefab = player1;
                break;
            case 2:
                selectedPrefab = player2;
                break;
            case 3:
                selectedPrefab = player3;
                break;
            case 4:
                selectedPrefab = player4;
                break;
            case 5:
                selectedPrefab = player5;
                break;
            default:
                selectedPrefab = null;
                break;
        }

        if (selectedPrefab != null)
        {
            GameObject newPlayer = Instantiate(selectedPrefab, position, rotation);
            // 생성된 플레이어에 대한 정보 설정
            PlayerInfo playerInfo = newPlayer.GetComponent<PlayerInfo>();
            if (playerInfo != null)
            {
                playerInfo.userId = userId;
                playerInfo.avatar = avatar;
                playerInfo.nickname = nickname;
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
            // 플레이어 오브젝트를 활성화
            newPlayer.SetActive(true);
        }
    }

    public void CreatePlayer()
    {
        Debug.Log("create player enter");
        int userId = UserInfoManager.Instance.GetUserId();
        string nickname = UserInfoManager.Instance.GetNickname();
        int avatar = UserInfoManager.Instance.GetAvatar();


        Vector3 position = new Vector3(0, 0, 0);
        Quaternion rotation = Quaternion.Euler(0, 0, 0);

        GameObject selectedPrefab;

        switch (avatar)
        {
            case 0:
                selectedPrefab = player0;
                break;
            case 1:
                selectedPrefab = player1;
                break;
            case 2:
                selectedPrefab = player2;
                break;
            case 3:
                selectedPrefab = player3;
                break;
            case 4:
                selectedPrefab = player4;
                break;
            case 5:
                selectedPrefab = player5;
                break;
            default:
                selectedPrefab = null;
                break;
        }

        if (selectedPrefab != null)
        {
            GameObject newPlayer = Instantiate(selectedPrefab, position, rotation);
            // 생성된 플레이어에 대한 정보 설정
            PlayerInfo playerInfo = newPlayer.GetComponent<PlayerInfo>();
            if (playerInfo != null)
            {
                playerInfo.userId = userId;
                playerInfo.avatar = avatar;
                playerInfo.nickname = nickname;
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
            // 플레이어 오브젝트를 활성화
            newPlayer.SetActive(true);
        }
    }
}
