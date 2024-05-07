using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Newtonsoft.Json.Linq;

public class JoinRoomManager : MonoBehaviour
{
    public GameObject prefab0; 
    public GameObject prefab1;
    public GameObject prefab2;
    public GameObject prefab3;
    public GameObject prefab4;
    public GameObject prefab5;

    private string nickname;
    private int userId;
    private int avatar;

    void Awake()
    {
        TCPMessageHandler.SetJoinRoomManagerInstance(this);
    }
    
    void Start()
    {
        
    }

    public void CreatePlayer(JObject data)
    {   
        string castingType = (string)data["dataHeader"]["castingType"];

        // int posX = (int)data["dataBody"]["positionInfo"]["posX"];
        // int posY = (int)data["dataBody"]["positionInfo"]["posY"];
        // int posZ = (int)data["dataBody"]["positionInfo"]["posZ"];
        // int rotX = (int)data["dataBody"]["positionInfo"]["rotX"];
        // int rotY = (int)data["dataBody"]["positionInfo"]["rotY"];
        // int rotZ = (int)data["dataBody"]["positionInfo"]["rotZ"];

        // 유니캐스팅으로 받은 경우 -> 본인 캐릭터 오브젝트 생성
        if(castingType == "UNICASTING")
        {
            UserInfoManager userInfoManager = UserInfoManager.Instance;
            userId = userInfoManager.GetUserId();
            nickname = userInfoManager.GetNickname();
            avatar = userInfoManager.GetAvatar();

            Vector3 position = new Vector3(0, 0, 0);
            Quaternion rotation = Quaternion.Euler(0, 0, 0);

            // 프리팹 설정
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
            // 플레이어를 생성하고 위치 및 회전 정보를 설정
            GameObject newPlayer = Instantiate(selectedPrefab, position, rotation);
            // 생성된 플레이어에 대한 정보 설정
            PlayerInfo playerInfo = newPlayer.GetComponent<PlayerInfo>();
            if (playerInfo != null)
            {
                playerInfo.userId = userId;
                playerInfo.avatar = avatar;
                playerInfo.nickname = nickname;
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

        // 멀티캐스팅으로 받은 경우 -> userId 받아서 타인 캐릭터 오브젝트 생성
        else if(castingType == "MULTICASTING")
        {
            JArray dataBody = (JArray)data["dataBody"];
            foreach (JToken item in dataBody)
            {
                // 각 객체에서 속성 값을 가져와야 함
                userId = (int)item["userId"];
                nickname = (string)item["nickname"];
                avatar = (int)item["avatar"];

                // 이후 해당 값을 사용하여 캐릭터 오브젝트 생성 등의 작업을 수행할 수 있음
                Vector3 position = new Vector3(0, 0, 0);
                Quaternion rotation = Quaternion.Euler(0, 0, 0);

                // 프리팹 설정
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
                // 플레이어를 생성하고 위치 및 회전 정보를 설정
                GameObject newPlayer = Instantiate(selectedPrefab, position, rotation);
                // 생성된 플레이어에 대한 정보 설정
                PlayerInfo playerInfo = newPlayer.GetComponent<PlayerInfo>();
                if (playerInfo != null)
                {
                    playerInfo.userId = userId;
                    playerInfo.avatar = avatar;
                    playerInfo.nickname = nickname;
                }
                else
                {
                    Debug.LogError("PlayerInfo component not found on player prefab.");
                }
                // 플레이어 오브젝트를 활성화
                newPlayer.SetActive(true);
                }

                // 내 위치 정보 보내기
                
        }
        
    
    }
}
