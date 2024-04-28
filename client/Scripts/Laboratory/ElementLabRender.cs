using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ElementLabRender : MonoBehaviour
{
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
    }
    // 분해하기
    public void disassemble()
    {
        Debug.Log("분해하기");
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
