using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ServialLabRender : MonoBehaviour
{
    [Header ("Pages")]
    public GameObject survivaLabPage;
    public GameObject recentResourcePage;
    public GameObject labResearchPage;
    public GameObject labFacPage;
    public GameObject labEffPage;
    public GameObject labSurvPage;
    public GameObject storePage;

    [Header ("Buttons")]
    public Button recent;
    public Button lab;
    public Button labFac;
    public Button labEff;
    public Button labSur;
    public Button store;
    public Button back;


    void Start()
    {
        recent.onClick.AddListener(renderRecentPage);
        lab.onClick.AddListener(renderLabFacPage);
        labFac.onClick.AddListener(renderLabFacPage);
        labEff.onClick.AddListener(renderLabEffPage);
        labSur.onClick.AddListener(renderLabSurPage);
        store.onClick.AddListener(renderStorePage);
        back.onClick.AddListener(closeSurvivalLabPage);
    }

    public void renderRecentPage()
    {
        labResearchPage.SetActive(false);
        storePage.SetActive(false);
        recentResourcePage.SetActive(true);
    }

    public void renderLabFacPage()
    {
        recentResourcePage.SetActive(false);
        labEffPage.SetActive(false);
        labSurvPage.SetActive(false);
        storePage.SetActive(false);
        labResearchPage.SetActive(true);
        labFacPage.SetActive(true);
    }

    public void renderLabEffPage()
    {
        recentResourcePage.SetActive(false);
        labFacPage.SetActive(false);
        labSurvPage.SetActive(false);
        storePage.SetActive(false);
        labResearchPage.SetActive(true);
        labEffPage.SetActive(true);
    }

    public void renderLabSurPage()
    {
        recentResourcePage.SetActive(false);
        labFacPage.SetActive(false);
        labEffPage.SetActive(false);
        storePage.SetActive(false);
        labResearchPage.SetActive(true);
        labSurvPage.SetActive(true);
    }

    public void renderStorePage()
    {
        recentResourcePage.SetActive(false);
        labResearchPage.SetActive(false);
        storePage.SetActive(true);
    }

    public void closeSurvivalLabPage()
    {
        survivaLabPage.SetActive(false);
    }

}
