using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;
using UnityEngine.SceneManagement;

public class SpaceShipPageRender : MonoBehaviour
{
    public static SpaceShipPageRender Instance;
    GameObject[] players;

    public Text clickedPlanet;

    [Header("Page")]
    public GameObject choosePlanet;

    [Header("Buttons")]
    public Button mercury;
    public Button venus;
    public Button asteroid;
    public Button startExplorer;
    public Button back;

    [Header("EventSystems")]
    public EventSystem eventSystem; // 원래 게임 씬에 존재하는 EventSystem
    public EventSystem modalEventSystem; // Modal에 사용될 EventSystem

    void Awake()
    {
        Instance = this;
        choosePlanet.SetActive(false); // 초기에 비활성화
        players = GameObject.FindGameObjectsWithTag("Player");
    }

    void Start()
    {
        mercury.onClick.AddListener(() => changeClickedText("수성"));
        venus.onClick.AddListener(() => changeClickedText("금성"));
        asteroid.onClick.AddListener(() => changeClickedText("소행성"));
        startExplorer.onClick.AddListener(startExplorerEvent);
        back.onClick.AddListener(closeExplorerPage);
    }

    public void RocketPageRnder()
    {
        Debug.Log("Page Render!");
        choosePlanet.SetActive(true);
        modalEventSystem.enabled = true; // ModalEventSystem 활성화
        eventSystem.enabled = false; // 원래 게임 씬에 존재하는 EventSystem 비활성화

        // Player 태그가 붙어있는 모든 오브젝트를 찾아서 RayCast 스크립트를 비활성화
        foreach (GameObject player in players)
        {
            RayCast rayCastScript = player.GetComponent<RayCast>();
            if (rayCastScript != null)
            {
                rayCastScript.enabled = false;
            }
        }
    }

    public void changeClickedText(string buttonText)
    {
        Debug.Log(buttonText + "button clicked");
        clickedPlanet.text = "[" + buttonText + "]";
    }

    public void startExplorerEvent()
    {
        Debug.Log("탐사 출발!!");
        // 새로운 맵으로 연결하는 로직 구현
        // SceneManager.LoadScene("TempScene2"); 
    }

    public void closeExplorerPage() 
    {
        choosePlanet.SetActive(false);
        modalEventSystem.enabled = false; // ModalEventSystem 비활성화
        eventSystem.enabled = true; // 원래 게임 씬에 존재하는 EventSystem 활성화

        // 비활성화된 RayCast 스크립트를 활성화
        foreach (GameObject player in players)
        {
            RayCast rayCastScript = player.GetComponent<RayCast>();
            if (rayCastScript != null)
            {
                rayCastScript.enabled = true;
            }
        }
    }
}
