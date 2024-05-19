using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class Countdown : MonoBehaviour
{
    public TMP_Text timer;
    public float maxTime;
    private float currentTime;

    [Header("Object")]
    public GameObject rocket;

    public void SetExploreTime(int exploreTime)
    {
        maxTime = exploreTime;
        currentTime = maxTime;
        UpdateTimerText();
    }

    void Update()
    {
        if (currentTime > 0f)
        {
            currentTime -= Time.deltaTime;

            if (currentTime < 0f)
            {
                currentTime = 0f;
            }

            UpdateTimerText();
        }
    }

    void UpdateTimerText()
    {
        int minutes = Mathf.FloorToInt(currentTime / 60f);
        int seconds = Mathf.FloorToInt(currentTime % 60f);

        timer.text = string.Format("{0:00}:{1:00}", minutes, seconds);

        if (minutes <= 0 && seconds <= 0)
        {
            Debug.Log("Ž�� �;��");
            enabled = false;
            // Ž�� ���� TCP ��� �ʿ�
            rocket.SetActive(true);


        }
    }
}
