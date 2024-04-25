using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Timer : MonoBehaviour
{
    public Text timer;
    public float maxTime = 60f;
    private float currentTime;

    void Start()
    {
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
    }
}
