using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;
using UnityEngine.SceneManagement;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;




public class ElementLabRender : MonoBehaviour
{
    private List<Dictionary<string, string>> elementList;
    private List<Dictionary<string, string>> compoundList;
    private List<int> elementsCounts;
    private List<int> compoundsCounts;

    private int selectedExItemId;
    private int selectedCompoundId;
    public int labLevel;

    [Header("Pages")]
    public GameObject elementLabPage;
    public GameObject laboPage;
    public GameObject labFacPage;
    //public GameObject labEffPage;
    public GameObject eleComPage;
    public GameObject extractionPage;


    [Header("Buttons")]
    public Button labFacPageButton;
    //public Button labEffPageButton;
    public Button eleComPageButton;
    public Button synthesisButton; // 합성하기
    //public Button decomposition; // 분해하기
    public Button extractionPageButton; // 추출하기 페이지
    public Button extractionButton; // 추출하기
    //public Button storePageButton;
    public Button backButton;

    [Header("SelectField")]
    public Button VenusButton;
    public Button MercuryButton;
    public Button PlanetButton;
    public TextMeshProUGUI SelectedRockText;
    public TextMeshProUGUI SelectedCompoundName;
    public TextMeshProUGUI resultText;


    [Header("Prefabs")]
    public GameObject elementPrefab; // Element 프리팹
    public Transform elementsTransform; // ElementScrollView의 Content Transform
    public GameObject compoundPrefab;
    public Transform compoundsTransform;

    [Header("labLevelButtons")]
    public Button level1Image;
    public Button level2Image;
    public Button level3Image;
    public GameObject button1;
    public GameObject button2;
    public GameObject button3;
    public Button lv1UpgradeButton;
    public Button lv2UpgradeButton;
    public Button lv3UpgradeButton;
    public TextMeshProUGUI levelExplainField;


    public void Awake()
    {
        // 원소 및 화합물 리스트
        List<Dictionary<string, object>> elementListObjects = ElementList.Instance.GetElementList();
        elementList = new List<Dictionary<string, string>>();
        foreach (var element in elementListObjects)
        {
            Dictionary<string, string> convertedElement = new Dictionary<string, string>();
            foreach (var pair in element)
            {
                convertedElement.Add(pair.Key, pair.Value.ToString());
            }
            elementList.Add(convertedElement);
        }
        List<Dictionary<string, object>> compoundListObjects = CompoundList.Instance.GetCompoundList();
        compoundList = new List<Dictionary<string, string>>();
        foreach (var compound in compoundListObjects)
        {
            Dictionary<string, string> convertedCompound = new Dictionary<string, string>();
            foreach (var pair in compound)
            {
                convertedCompound.Add(pair.Key, pair.Value.ToString());
            }
            compoundList.Add(convertedCompound);
        }

        renderLabFacPage();

    }

    void Start()
    {
        Cursor.visible = true;
        selectedExItemId = 4;
        selectedCompoundId = 999;
        labFacPageButton.onClick.AddListener(renderLabFacPage);
        level1Image.onClick.AddListener(Select1level);
        level2Image.onClick.AddListener(Select2level);
        level3Image.onClick.AddListener(Select3level);
        //labEffPageButton.onClick.AddListener(renderLabEffPage);
        eleComPageButton.onClick.AddListener(renderEleComPage);
        synthesisButton.onClick.AddListener(synthesize);
        //decomposition.onClick.AddListener(disassemble);
        extractionPageButton.onClick.AddListener(renderExtractionPage);
        extractionButton.onClick.AddListener(extract);
        //storePageButton.onClick.AddListener(renderStorePage);
        backButton.onClick.AddListener(closeElementLabPage);

        // 업그레이드
        lv1UpgradeButton.onClick.AddListener(UpgradeLab);
        lv2UpgradeButton.onClick.AddListener(UpgradeLab);
        lv3UpgradeButton.onClick.AddListener (UpgradeLab);


        // 추출
        VenusButton.onClick.AddListener(ExtractButton1);
        MercuryButton.onClick.AddListener(ExtractButton2);
        PlanetButton.onClick.AddListener(ExtractButton3);


    }

    // 연구소 입장 허용, 초기 상태 메시지 수신
    public void SetLabLevel(JObject data)
    {
        labLevel = (int)data["dataBody"]["labLevel"];
        SetLabLevelButton();

        elementsCounts = data["dataBody"]["element"].ToObject<List<int>>();
        compoundsCounts = data["dataBody"]["compound"].ToObject<List<int>>();

        PopulateElementList();
        PopulateCompoundList();
    }

    // 연구실 레벨 별 버튼 토글
    public void SetLabLevelButton()
    {
        Debug.Log("버튼 세팅");
        if(labLevel == 0)
        {
            button1.SetActive(true);
            button2.SetActive(false);
            button3.SetActive(false);
            Select1level();
        }
        else if (labLevel == 1)
        {
            button1.SetActive(false);
            button2.SetActive(true);
            button3.SetActive(false);
            Select2level();
        }
        else if (labLevel == 2)
        {
            button1.SetActive(false);
            button2.SetActive(false);
            button3.SetActive(true);
            Select3level();
        }
        else
        {
            Debug.Log("연구 완료");
            levelExplainField.text = "연구 완료";
            button1.SetActive(false);
            button2.SetActive(false);
            button3.SetActive(false);
        }
    }

    // 연구 이미지 버튼 클릭 시 설명
    public void Select1level()
    {
        levelExplainField.text = "연구소 업그레이드 lv.1 \n\n효과: 추출 효율이 조금 증가한다.\n필요재료: 목재: 1, NH3: 1, \n티타늄금속판: 1, 철괴: 1 ";
    }
    public void Select2level()
    {
        levelExplainField.text = "연구소 업그레이드 lv.2 \n\n효과: 추출 효율이 많이 증가한다.\n필요재료: 목재: 2, NH3: 3, \n티타늄금속판: 1, 은괴: 1";
    }
    public void Select3level()
    {
        levelExplainField.text = "연구소 업그레이드 lv.3 \n\n효과: 추출 효율이 대폭 증가한다.\n필요재료: 목재: 3, NH3: 5, \n티타늄금속판: 1, 금괴: 1";
    }

    // 연구소 업그레이드 요청
    public void UpgradeLab()
    {
        string channelId = ChannelManager.Instance.GetChannelId();
        int userId = UserInfoManager.Instance.GetUserId();
        int labId = 0;
        UpgradeRequest request = new UpgradeRequest("ingame", "laboratory", "upgrade", channelId, userId, labId);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendMainTCPRequest(json);
        Debug.Log("json : " + json);

    }

    public class UpgradeRequest
    {
        public string type;
        public string category;
        public string eventName;
        public string channelId;
        public int userId;
        public int labId;

        public UpgradeRequest(string type, string category, string eventName, string channelId, int userId, int labId)
        {
            this.type = type;
            this.category = category;
            this.eventName = eventName;
            this.channelId = channelId;
            this.userId = userId;
            this.labId = labId;
        }
    }
    // 연구소 업그레이드 요청 답신
    public void UpgradeLabResponse(JObject data)
    {
        // 업그레이드 성공
        if ((string)data["dataHeader"]["msg"] == "success")
        {
            // 인벤토리 업데이트 필요

            labLevel = (int)data["dataBody"]["labLevel"];
            SetLabLevelButton();
        }

        // 업그레이드 실패
        else if ((string)data["dataHeader"]["msg"] == "fail")
        {
            // 업그레이드 불가
            if ((string)data["dataBody"]["msg"] == "cannotUpgrade")
            {
                levelExplainField.text = "업그레이드가 불가능합니다.";
            }
            // 재료부족
            else if ((string)data["dataBody"]["msg"] == "noItem")
            {
                levelExplainField.text = "재료가 부족합니다.";
            }
            
        }
    }

    // (메인) 연구실-시설
    public void renderLabFacPage()
    {
        laboPage.SetActive(true);
        labFacPage.SetActive(true);
        //labEffPage.SetActive(false);
        eleComPage.SetActive(false);
        extractionPage.SetActive(false);
        //storePage.SetActive(false);
    }

    // 합성분해실
    public void renderEleComPage()
    {
        laboPage.SetActive(false);
        eleComPage.SetActive(true);
        extractionPage.SetActive(false);
        //storePage.SetActive(false);
    }
    // 원소 리스트 렌더링
    private void PopulateElementList()
    {
        int index = 0;
        foreach (var element in elementList)
        {
            GameObject newElementGO = Instantiate(elementPrefab, elementsTransform);
            newElementGO.name = element["key"];

            // 각 원소의 정보를 UI에 설정
            Image elementImage = newElementGO.transform.Find("ElementImage").GetComponent<Image>();
            Text nameText = newElementGO.transform.Find("ElementName").GetComponent<Text>();
            Text summaryText = newElementGO.transform.Find("ElementExplain").GetComponent<Text>();
            Text countText = newElementGO.transform.Find("ElementCount").GetComponent<Text>();

            // 이미지 경로를 통해 스프라이트 불러오기
            string imagePath = element["image"].ToString();
            elementImage.sprite = GetElementSprite(imagePath);

            nameText.text = element["name"];
            summaryText.text = element["summary"];
            countText.text = elementsCounts[index].ToString();  // 갯수 설정

            index++;
        }
    }

    // 화합물 리스트 렌더링
    private void PopulateCompoundList()
    {
        int index = 0;
        foreach (var compound in compoundList)
        {
            GameObject newElementGO = Instantiate(compoundPrefab, compoundsTransform);
            newElementGO.name = compound["key"];

            // 각 화합물의 정보를 UI에 설정
            Image compoundImage = newElementGO.transform.Find("CompoundImage").GetComponent<Image>();
            Text nameText = newElementGO.transform.Find("CompoundName").GetComponent<Text>();
            Text summaryText = newElementGO.transform.Find("CompoundExplain").GetComponent<Text>();
            Text countText = newElementGO.transform.Find("CompoundCount").GetComponent<Text>(); 

            // 이미지 경로를 통해 스프라이트 불러오기
            string imagePath = compound["image"].ToString();
            compoundImage.sprite = GetElementSprite(imagePath);

            nameText.text = compound["name"];
            summaryText.text = compound["summary"];
            countText.text = compoundsCounts[index].ToString();

            index++;
        }

        // Transform compoundTransform = compoundsTransform.Find("4"); // NH3
        // GameObject compoundGO = compoundTransform.gameObject;
        // Text countText2 = compoundGO.transform.Find("CompoundCount").GetComponent<Text>();
    }

    // 이미지 불러오기
    private Sprite GetElementSprite(string imagePath)
    {
        return Resources.Load<Sprite>(imagePath);
    }

    // 합성하기

    public void UpdateSelectedCompound(string name, int key)
    {
        SelectedCompoundName.text = name;
        selectedCompoundId = key;
    }
    public void synthesize()
    {
        Debug.Log("합성하기");

        if (selectedCompoundId == 999)
        {
            SelectedCompoundName.text = "합성할 화합물을 선택해주세요";
        }
        else
        {
            string channelId = ChannelManager.Instance.GetChannelId();
            int userId = UserInfoManager.Instance.GetUserId();

            
            SynthesizeRequest request = new SynthesizeRequest("ingame", "laboratory", "synthesizing", channelId, userId, "compound", selectedCompoundId);
            string json = JsonConvert.SerializeObject(request);
            TCPClientManager.Instance.SendMainTCPRequest(json);
            selectedCompoundId = 999;
            SelectedCompoundName.text = "";

            Transform elementTransform = elementsTransform.Find("6"); // Fe로 고정
            GameObject elementGO = elementTransform.gameObject;
            Text countText = elementGO.transform.Find("ElementCount").GetComponent<Text>();
            countText.text = "0"; // 이전에 값 5로 설정

            Transform compoundTransform = compoundsTransform.Find("0"); // 철괴로 고정
            GameObject compoundGO = compoundTransform.gameObject;
            Text countText2 = compoundGO.transform.Find("CompoundCount").GetComponent<Text>();
            countText2.text = "1"; // 이전에 값 0으로 설정


            SelectedCompoundName.text = "합성 성공!";


        }

    }
    
    public void SynthesizingResponse(JObject data)
    {
        // 합성 실패
        if((string)data["dataBody"]["msg"] == "noitem")
        {
            SelectedCompoundName.text = "재료 부족";
        }
        // 합성 성공
        else
        {
            elementsCounts = data["dataBody"]["element"].ToObject<List<int>>();
            compoundsCounts = data["dataBody"]["compound"].ToObject<List<int>>();
            SelectedCompoundName.text = "합성 성공!";
            for (int i = 0; i < elementList.Count; i++)
            {
                Transform elementTransform = elementsTransform.Find(i.ToString());
                if (elementTransform == null)
                {
                    Debug.LogError("Element not found");
                    return;
                }
                else
                {
                    GameObject elementGO = elementTransform.gameObject;
                    Text countText = elementGO.transform.Find("ElementCount").GetComponent<Text>();
                    countText.text = elementsCounts[i].ToString();
                }

            }
            for (int i = 0; i < compoundList.Count; i++)
            {
                Transform compoundTransform = compoundsTransform.Find(i.ToString());
                if (compoundTransform == null)
                {
                    Debug.LogError("compound not found");
                    return;
                }
                else
                {
                    GameObject compoundGO = compoundTransform.gameObject;
                    Text countText = compoundGO.transform.Find("CompoundCount").GetComponent<Text>();
                    countText.text = compoundsCounts[i].ToString();
                }

            }
        }
    }

    public class SynthesizeRequest
    {
        public string type;
        public string category;
        public string eventName;
        public string channelId;
        public int userId;
        public string itemCategory;
        public int itemId;

        public SynthesizeRequest(string type, string category, string eventName, string channelId, int userId, string itemCategory, int itemId)
        {
            this.type = type;
            this.category = category;
            this.eventName = eventName;
            this.channelId = channelId;
            this.userId = userId;
            this.itemCategory = itemCategory;
            this.itemId = itemId;
        }
    }
    // 원소추출실
    public void ExtractButton1()
    {
        selectedExItemId = 0;
        SelectedRockText.text = "수성광석";
    }
    public void ExtractButton2()
    {
        selectedExItemId = 1;
        SelectedRockText.text = "금성광석";
    }
    public void ExtractButton3()
    {
        selectedExItemId = 2;
        SelectedRockText.text = "소행성광석";
    }

    public void renderExtractionPage()
    {
        selectedExItemId = 4;
        SelectedRockText.text = "";
        labFacPage.SetActive(false);
        //labEffPage.SetActive(false);
        eleComPage.SetActive(false);
        extractionPage.SetActive(true);
        //storePage.SetActive(false);
    }
    // 추출하기

    public void extract()
    {
        Debug.Log("추출하기");

        if (selectedExItemId == 4)
        {
            SelectedRockText.text = "추출할 아이템을 선택해주세요";

        }
        else
        {
            //// 추출 메시지 송신
            int userId = UserInfoManager.Instance.GetUserId();
            string channelId = ChannelManager.Instance.GetChannelId();
            //// string teamcode = ChannelManager.Instance.GetTeamCode();
            ExtractRequest request = new ExtractRequest("ingame", "laboratory", "extracting", channelId, userId, "extractionMaterial", selectedExItemId);
            string json = JsonConvert.SerializeObject(request);
            TCPClientManager.Instance.SendMainTCPRequest(json);
            selectedExItemId = 4;
            SelectedRockText.text = "";

            resultText.text = "Fe: 5, H: 3, C: 3\n N: 7, O: 2 S: 1";

            Transform elementTransform0 = elementsTransform.Find("0"); // 수소 3개
            GameObject elementGO0 = elementTransform0.gameObject;
            Text countText0 = elementGO0.transform.Find("ElementCount").GetComponent<Text>();
            countText0.text = "3";

            Transform elementTransform1 = elementsTransform.Find("1"); // 탄소 3개
            GameObject elementGO1 = elementTransform1.gameObject;
            Text countText1 = elementGO1.transform.Find("ElementCount").GetComponent<Text>();
            countText1.text = "3";

            Transform elementTransform2 = elementsTransform.Find("2"); // 질소 7개
            GameObject elementGO2 = elementTransform2.gameObject;
            Text countText2 = elementGO2.transform.Find("ElementCount").GetComponent<Text>();
            countText2.text = "7";

            Transform elementTransform3 = elementsTransform.Find("3"); // 산소 2개
            GameObject elementGO3 = elementTransform3.gameObject;
            Text countText3 = elementGO3.transform.Find("ElementCount").GetComponent<Text>();
            countText3.text = "2";

            Transform elementTransform5 = elementsTransform.Find("5"); // 황 1개
            GameObject elementGO5 = elementTransform5.gameObject;
            Text countText5 = elementGO5.transform.Find("ElementCount").GetComponent<Text>();
            countText5.text = "1";

            Transform elementTransform6 = elementsTransform.Find("6"); // 철 5개
            GameObject elementGO6 = elementTransform6.gameObject;
            Text countText6 = elementGO6.transform.Find("ElementCount").GetComponent<Text>();
            countText6.text = "5";

            // 인벤에서 소행성 광석 사라져야 함
        }


    }
    // 연구소 상태 업데이트 브로드캐스팅
    class ItemCnt
    {
        public int Id { get; set; }
        public int Value { get; set; }
    }


    static List<ItemCnt> ParseItemCntList(JToken itemCntArray)
    {
        List<ItemCnt> itemCntList = new List<ItemCnt>();

        foreach (var item in itemCntArray)
        {
            string[] parts = item.ToString().Split(':');
            int id = int.Parse(parts[0]);
            int value = int.Parse(parts[1]);
            itemCntList.Add(new ItemCnt { Id = id, Value = value });
        }

        return itemCntList;
    }

    // 추출 메시지 수신 - 유니캐스팅
    public void ExtractingResponse(JObject data)
    {
        string msg = (string)data["dataHeader"]["msg"];
        if (msg == "fail")
        {
            SelectedRockText.text = "보유한 아이템이 없습니다";
        }
        else if (msg == "success")
        {

            string result = (string)data["dataBody"]["extractResult"];
            resultText.text = result;

            elementsCounts = data["dataBody"]["labData:element"].ToObject<List<int>>();
            for (int i = 0; i < elementList.Count; i++)
            {
                Transform elementTransform = elementsTransform.Find(i.ToString());
                if (elementTransform == null)
                {
                    Debug.LogError("Element not found");
                    return;
                }
                else
                {
                    GameObject elementGO = elementTransform.gameObject;
                    Text countText = elementGO.transform.Find("ElementCount").GetComponent<Text>();
                    countText.text = elementsCounts[i].ToString();
                }

            }

            // 인벤토리에 남은 아이템이 있는 경우

            // 인벤토리, 추출 결과 업데이트 필요

            // 인벤에 남은 아이템이 없는 경우
            // 추출 결과 업데이트 필요
            // 인벤토리 비우기

        }
    }

    public class ExtractRequest
    {
        public string type;
        public string category;
        public string eventName;
        public string channelId;
        public int userId;
        public string itemCategory;
        public int itemId;

        public ExtractRequest(string type, string category, string eventName, string channelId, int userId, string itemCategory, int itemId)
        {
            this.type = type;
            this.category = category;
            this.eventName = eventName;
            this.channelId = channelId;
            this.userId = userId;
            this.itemCategory = itemCategory;
            this.itemId = itemId;

        }
    }
    // back
    public void closeElementLabPage()
    {
        // 나가기 요청
        string channelId = ChannelManager.Instance.GetChannelId();
        int userId = UserInfoManager.Instance.GetUserId();
        int labId = 0;
        LeaveLabRequest request = new LeaveLabRequest("ingame", "laboratory", "leaveLab", channelId, userId, labId);
        string json = JsonConvert.SerializeObject(request);
        TCPClientManager.Instance.SendMainTCPRequest(json);
    }
    // 나가기 허용 수신
    public void LeaveLabPage()
    {

        // 비동기 씬 로딩 시작
        StartCoroutine(LoadSceneAndCreatePlayer("IngameTest"));
    }

    IEnumerator LoadSceneAndCreatePlayer(string sceneName)
    {
        // 씬 로드를 비동기적으로 시작
        AsyncOperation asyncLoad = SceneManager.LoadSceneAsync(sceneName);

        // 로드가 완료될 때까지 대기
        while (!asyncLoad.isDone)
        {
            yield return null;
        }

        // 씬 로드가 완료되었으므로, 플레이어 생성
        IngamePlayerRenderer renderer = GameObject.FindObjectOfType<IngamePlayerRenderer>();
        if (renderer != null)
        {
            renderer.CreatePlayer();
        }
        else
        {
            Debug.LogError("IngamePlayerRenderer not found in the scene.");
        }
    }
    public class LeaveLabRequest
    {
        public string type;
        public string category;
        public string eventName;
        public string channelId;
        public int userId;
        public int labId;
        public LeaveLabRequest(string type, string category, string eventName, string channelId, int userId, int labId)
        {
            this.type = type;
            this.category = category;
            this.eventName = eventName;
            this.channelId = channelId;
            this.userId = userId;
            this.labId = labId;
        }
    }

}
