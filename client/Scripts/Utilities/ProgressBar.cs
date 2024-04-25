using UnityEngine;
using UnityEngine.UI;

public class ProgressBarControl : MonoBehaviour
{
    public Slider slider;

    public float minSliderValue = 0f;
    public float maxSliderValue = 20f;
    public float sliderValue;

    void Start()
    {
        sliderValue = minSliderValue;
        if (slider == null)
        {
            Debug.LogError("Slider reference is not set in ProgressBarControl script!");
            return;
        }

        slider.minValue = minSliderValue;
        slider.maxValue = maxSliderValue;
        slider.value = sliderValue;

    }

        void Update()
    {
        sliderValue += Time.deltaTime;

        if (sliderValue > maxSliderValue)
        {
            sliderValue = maxSliderValue;
        }

        slider.value = sliderValue;

        if(sliderValue >= maxSliderValue)
        {
            Debug.Log("it's full");
            enabled = false;
        }
    }
}
