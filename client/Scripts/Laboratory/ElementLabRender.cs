using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.Net.Sockets;
using System.IO; 
using Newtonsoft.Json;

public class ExtractTCPRequest
{
    public string type;
    public string eventType;
    public int cid;
    public int itemId;
    public int itemCnt;

    public ExtractTCPRequest(string type, string eventType, int cid, int itemId, int itemCnt)
    {
        this.type = type;
        this.eventType = eventType;
        this.cid = cid;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
    }
}

public class SynthesisTCPRequest
{
    public string type;
    public string eventType;
    public int cid;
    public int itemId;
    public int itemCnt;

    public SynthesisTCPRequest(string type, string eventType, int cid, int itemId, int itemCnt)
    {
        this.type = type;
        this.eventType = eventType;
        this.cid = cid;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
    }
}

public class DecompositionTCPRequest
{
    public string type;
    public string eventType;
    public int cid;
    public int itemId;
    public int itemCnt;

    public DecompositionTCPRequest(string type, string eventType, int cid, int itemId, int itemCnt)
    {
        this.type = type;
        this.eventType = eventType;
        this.cid = cid;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
    }
}

public class ElementLabRender : MonoBehaviour
{

    private TCPClientManager tcpClientManager;

    [Header ("Pages")]
    public GameObject elementLabPage;
    public GameObject labFacPage;
    public GameObject labEffPage;
    public GameObject eleComPage;
    public GameObject extractionPage;
    public GameObject storePage;


    [Header ("Buttons")]
    public Button LabPageButton;
    public Button labFacPageButton;
    public Button labEffPageButton;
    public Button eleComPageButton;
    public Button synthesisButton; // 합성하기
    public Button decomposition; // 분해하기
    public Button extractionPageButton; // 추출하기 페이지
    public Button extractionButton; // 추출하기
    public Button storePageButton;
    public Button backButton;

    

    public void Awake()
    {
        tcpClientManager = TCPClientManager.Instance;
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager가 초기화되지 않았습니다.");
            return;
        }
    }

    void Start()
    {
        LabPageButton.onClick.AddListener(renderLabFacPage);
        labFacPageButton.onClick.AddListener(renderLabFacPage);
        labEffPageButton.onClick.AddListener(renderLabEffPage);
        eleComPageButton.onClick.AddListener(renderEleComPage);
        synthesisButton.onClick.AddListener(synthesize);
        decomposition.onClick.AddListener(disassemble);
        extractionPageButton.onClick.AddListener(renderExtractionPage);
        extractionButton.onClick.AddListener(extract);
        storePageButton.onClick.AddListener(renderStorePage);
        backButton.onClick.AddListener(closeElementLabPage);
    }

    // (메인) 연구실-시설
    public void renderLabFacPage()
    {
        labFacPage.SetActive(true);
        labEffPage.SetActive(false);
        eleComPage.SetActive(false);
        extractionPage.SetActive(false);
        storePage.SetActive(false);
    }
    // 연구실-효율
    public void renderLabEffPage()
    {
        labFacPage.SetActive(false);
        labEffPage.SetActive(true);
        eleComPage.SetActive(false);
        extractionPage.SetActive(false);
        storePage.SetActive(false);
    }
    // 합성분해실
    public void renderEleComPage()
    {
        labFacPage.SetActive(false);
        labEffPage.SetActive(false);
        eleComPage.SetActive(true);
        extractionPage.SetActive(false);
        storePage.SetActive(false);
    }
    // 합성하기
    public void synthesize()
    {
        Debug.Log("합성하기");
        // TCPClientManager가 설정되지 않았으면 오류 출력
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
            return;
        }
          // TCPClientManager의 GetStream 메서드를 사용하여 NetworkStream 가져오기
        NetworkStream stream = tcpClientManager.GetStream();
        if (stream == null)
        {
            Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
            return;
        }
        SynthesisTCPRequest synthesizeData = new SynthesisTCPRequest("ingame", "synthesis", 0, 0, 1);
        string json = JsonConvert.SerializeObject(synthesizeData);

        tcpClientManager.SendTCPRequest(json);
    }
    // 분해하기
    public void disassemble()
    {
        Debug.Log("분해하기");
        // TCPClientManager가 설정되지 않았으면 오류 출력
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
            return;
        }
          // TCPClientManager의 GetStream 메서드를 사용하여 NetworkStream 가져오기
        NetworkStream stream = tcpClientManager.GetStream();
        if (stream == null)
        {
            Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
            return;
        }
        DecompositionTCPRequest decompositionData = new DecompositionTCPRequest("ingame", "decomposition", 0, 0, 1);
        string json = JsonConvert.SerializeObject(decompositionData);

        tcpClientManager.SendTCPRequest(json);
    }
    // 원소추출실
    public void renderExtractionPage()
    {
        labFacPage.SetActive(false);
        labEffPage.SetActive(false);
        eleComPage.SetActive(false);
        extractionPage.SetActive(true);
        storePage.SetActive(false);
    }
    // 추출하기
    public void extract()
    {
        Debug.Log("추출하기");
        // TCPClientManager가 설정되지 않았으면 오류 출력
        if (tcpClientManager == null)
        {
            Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
            return;
        }
          // TCPClientManager의 GetStream 메서드를 사용하여 NetworkStream 가져오기
        NetworkStream stream = tcpClientManager.GetStream();
        if (stream == null)
        {
            Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
            return;
        }

        // JSON 생성
        ExtractTCPRequest extractionData = new ExtractTCPRequest("ingame", "extraction", 123, 456, 789);
        string json = JsonConvert.SerializeObject(extractionData);

        tcpClientManager.SendTCPRequest(json);


    }
    // 저장실
    public void renderStorePage()
    {
        labFacPage.SetActive(false);
        labEffPage.SetActive(false);
        eleComPage.SetActive(false);
        extractionPage.SetActive(false);
        storePage.SetActive(true);
    }
    // back
    public void closeElementLabPage()
    {
        elementLabPage.SetActive(false);
    }

}
