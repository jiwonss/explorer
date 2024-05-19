using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class AutoScroll : MonoBehaviour
{
    public ScrollRect scrollRect;

    void Start()
    {
        StartCoroutine(ScrollToBottom());
    }

    public void OnEnable()
    {
        StartCoroutine(ScrollToBottom());
    }

    IEnumerator ScrollToBottom()
    {
        yield return new WaitForEndOfFrame();  // 프레임의 끝을 기다립니다.
        scrollRect.verticalNormalizedPosition = 0f;
    }

    public void OnChatUpdated()
    {
        StartCoroutine(ScrollToBottom());
    }
}
