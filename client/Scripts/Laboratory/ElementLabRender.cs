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
    private List<Dictionary<string, string>> elementList; 
    private List<Dictionary<string, string>> compoundList; 

    // private TCPClientManager tcpClientManager;

    [Header ("Pages")]
    public GameObject elementLabPage;
    public GameObject laboPage;
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


    [Header("Prefabs")]
    public GameObject elementPrefab; // Element 프리팹
    public Transform elementsTransform; // ElementScrollView의 Content Transform
    public GameObject compoundPrefab;
    public Transform compoundTransform;


    public void Awake()
    {
        // tcpClientManager = TCPClientManager.Instance;
        // if (tcpClientManager == null)
        // {
        //     Debug.LogError("TCPClientManager가 초기화되지 않았습니다.");
        //     return;
        // }
        // 원소 및 화합물 리스트
        List<Dictionary<string, object>> elementListObjects = ElementList.Instance.GetElementList();
        elementList = new List<Dictionary<string, string>>();
        foreach(var element in elementListObjects)
        {
            Dictionary<string, string> convertedElement = new Dictionary<string, string>();
            foreach(var pair in element)
            {
                convertedElement.Add(pair.Key, pair.Value.ToString());
            }
            elementList.Add(convertedElement);
        }
        List<Dictionary<string, object>> compoundListObjects = CompoundList.Instance.GetCompoundList();
        compoundList = new List<Dictionary<string, string>>();
        foreach(var compound in compoundListObjects)
        {
            Dictionary<string, string> convertedCompound = new Dictionary<string, string>();
            foreach(var pair in compound)
            {
                convertedCompound.Add(pair.Key, pair.Value.ToString());
            }
            compoundList.Add(convertedCompound);
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

        renderLabFacPage();
        PopulateElementList();
        PopulateCompoundList();
    
    }

    // (메인) 연구실-시설
    public void renderLabFacPage()
    {
        laboPage.SetActive(true);
        labFacPage.SetActive(true);
        labEffPage.SetActive(false);
        eleComPage.SetActive(false);
        extractionPage.SetActive(false);
        storePage.SetActive(false);
    }
    // 연구실-효율
    public void renderLabEffPage()
    {
        laboPage.SetActive(true);
        labFacPage.SetActive(false);
        labEffPage.SetActive(true);
        eleComPage.SetActive(false);
        extractionPage.SetActive(false);
        storePage.SetActive(false);
    }
    // 합성분해실
    public void renderEleComPage()
    {
        laboPage.SetActive(false);
        eleComPage.SetActive(true);
        extractionPage.SetActive(false);
        storePage.SetActive(false);
    }

    // 원소 리스트 렌더
    private void PopulateElementList()
    {
        foreach (var element in elementList)
        {
            GameObject newElementGO = Instantiate(elementPrefab, elementsTransform);
            newElementGO.name = element["key"]; // 원소 이름으로 GameObject 이름 설정

            // 각 원소의 정보를 UI에 설정
            Image elementImage = newElementGO.transform.Find("ElementImage").GetComponent<Image>();
            Text nameText = newElementGO.transform.Find("ElementName").GetComponent<Text>(); 
            Text summaryText = newElementGO.transform.Find("ElementExplain").GetComponent<Text>(); 

            // 이미지 경로를 통해 스프라이트 불러오기
            string imagePath = element["image"].ToString();
            elementImage.sprite = GetElementSprite(imagePath);

            nameText.text = element["name"];
            summaryText.text = element["summary"];
        }
    }

    // 화합물 리스트 렌더
    private void PopulateCompoundList()
    {
        foreach (var compound in compoundList)
        {
            GameObject newElementGO = Instantiate(compoundPrefab, compoundTransform);
            newElementGO.name = compound["key"]; // 화합물 이름으로 GameObject 이름 설정

            // 각 원소의 정보를 UI에 설정
            Image compoundImage = newElementGO.transform.Find("CompoundImage").GetComponent<Image>();
            Text nameText = newElementGO.transform.Find("CompoundName").GetComponent<Text>(); 
            Text summaryText = newElementGO.transform.Find("CompoundExplain").GetComponent<Text>(); 
            // 이미지 경로를 통해 스프라이트 불러오기
            string imagePath = compound["image"].ToString();
            compoundImage.sprite = GetElementSprite(imagePath);

            nameText.text = compound["name"];
            summaryText.text = compound["summary"];
        }
    }
    // 갯수 업데이트 필요

    // 이미지 불러오기
    private Sprite GetElementSprite(string imagePath)
    {
        return Resources.Load<Sprite>(imagePath);
    }

    // 합성하기
    public void synthesize()
    {
        Debug.Log("합성하기");
        // // TCPClientManager가 설정되지 않았으면 오류 출력
        // if (tcpClientManager == null)
        // {
        //     Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
        //     return;
        // }
        //   // TCPClientManager의 GetStream 메서드를 사용하여 NetworkStream 가져오기
        // NetworkStream stream = tcpClientManager.GetStream();
        // if (stream == null)
        // {
        //     Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
        //     return;
        // }
        // SynthesisTCPRequest synthesizeData = new SynthesisTCPRequest("ingame", "synthesis", 0, 0, 1);
        // string json = JsonConvert.SerializeObject(synthesizeData);

        // tcpClientManager.SendTCPRequest(json);
    }
    // 분해하기
    public void disassemble()
    {
        Debug.Log("분해하기");
        // // TCPClientManager가 설정되지 않았으면 오류 출력
        // if (tcpClientManager == null)
        // {
        //     Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
        //     return;
        // }
        //   // TCPClientManager의 GetStream 메서드를 사용하여 NetworkStream 가져오기
        // NetworkStream stream = tcpClientManager.GetStream();
        // if (stream == null)
        // {
        //     Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
        //     return;
        // }
        // DecompositionTCPRequest decompositionData = new DecompositionTCPRequest("ingame", "decomposition", 0, 0, 1);
        // string json = JsonConvert.SerializeObject(decompositionData);

        // tcpClientManager.SendTCPRequest(json);
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
        // // TCPClientManager가 설정되지 않았으면 오류 출력
        // if (tcpClientManager == null)
        // {
        //     Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
        //     return;
        // }
        //   // TCPClientManager의 GetStream 메서드를 사용하여 NetworkStream 가져오기
        // NetworkStream stream = tcpClientManager.GetStream();
        // if (stream == null)
        // {
        //     Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
        //     return;
        // }

        // // JSON 생성
        // ExtractTCPRequest extractionData = new ExtractTCPRequest("ingame", "extraction", 123, 456, 789);
        // string json = JsonConvert.SerializeObject(extractionData);

        // tcpClientManager.SendTCPRequest(json);


    }
    // 저장실
    public void renderStorePage()
    {
        laboPage.SetActive(false);
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
