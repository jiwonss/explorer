using UnityEngine;
using UnityEngine.UI;

public class ProgressBarControl : MonoBehaviour
{
    public Slider slider;

    public float minSliderValue = 0f;
    public float maxSliderValue;
    public float sliderValue;

    public Canvas progressCanvas;

    private bool isUpdating = false; // Update 함수 실행 여부를 확인하기 위한 플래그

    void Start()
    {
        progressCanvas.enabled = false;
    }

    public void StartProgress(float farmingTime)
    {
        progressCanvas.enabled = true;
        maxSliderValue = farmingTime;
        sliderValue = minSliderValue;
        if (slider == null)
        {
            Debug.LogError("Slider reference is not set");
            return;
        }

        slider.minValue = minSliderValue;
        slider.maxValue = maxSliderValue;
        slider.value = sliderValue;

        // Update 함수 실행 플래그 설정
        isUpdating = true;
    }

    void Update()
    {
        // Update 함수 실행 중일 때만 실행
        if (isUpdating)
        {
            sliderValue += Time.deltaTime;

            // 파밍 완료
            if (sliderValue >= maxSliderValue)
            {
                sliderValue = maxSliderValue;
                Debug.Log("it's full");
                isUpdating = false; // Update 함수 중지
                enabled = false; // 스크립트 비활성화

                ObjectFarming objectFarming = FindObjectOfType<ObjectFarming>();
                if (objectFarming != null)
                {
                    objectFarming.FinishFarming();
                }
            }

            slider.value = sliderValue;
        }
    }

    public void StopProgress()
    {
        progressCanvas.enabled = false;
    }
}
