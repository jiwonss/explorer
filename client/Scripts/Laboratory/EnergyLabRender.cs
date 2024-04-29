using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class EnegryLabRender : MonoBehaviour
{
    [Header ("Pages")]
    public GameObject energyLabPage;
    public GameObject labFacPage;
    public GameObject labEnePage;
    public GameObject storePage;

    [Header ("Buttons")]
    public Button labFac;
    public Button labFac2;
    public Button labEne;
    public Button store;
    public Button back;

    void Start()
    {
        labFac.onClick.AddListener(renderLabFacPag);
        labFac2.onClick.AddListener(renderLabFacPag);
        labEne.onClick.AddListener(renderLabEnePage);
        store.onClick.AddListener(renderStorePage);
        back.onClick.AddListener(closeEnergyLabPage);
    }
    
    // (메인) 연구실- 시설
    public void renderLabFacPag()
    {
        labEnePage.SetActive(false);
        storePage.SetActive(false);
        labFacPage.SetActive(true);
    }
    // 연구실 - 에너지
    public void renderLabEnePage()
    {
        labFacPage.SetActive(false);
        storePage.SetActive(false);
        labEnePage.SetActive(true);
    }
    // 저장실
    public void renderStorePage()
    {
        labFacPage.SetActive(false);
        labEnePage.SetActive(false);
        storePage.SetActive(true);
    }
    // 닫기
    public void closeEnergyLabPage()
    {
        energyLabPage.SetActive(false);
    }
}