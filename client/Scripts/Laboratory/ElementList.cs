using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ElementList : MonoBehaviour
{
    private List<Dictionary<string, object>> dictArray;

    public static ElementList Instance { get; private set; }

    private void Awake()
    {
        if (Instance == null)
        {
            Instance = this;
            DontDestroyOnLoad(gameObject);
        }
        else
        {
            Destroy(gameObject);
        }

        InitializeElementList();
    }

    private void InitializeElementList()
    {
        dictArray = new List<Dictionary<string, object>>
        {
            new Dictionary<string, object>
            {
                { "key", 0 },
                { "image", "Images/Elements/H"},
                { "name", "H\n(수소)" },
                { "summary", "원자번호 1번. 비금속 물질. 우주 구성의 75%를 차지한다." }
            },
            new Dictionary<string, object>
            {
                { "key", 1 },
                { "name", "C\n(탄소)" },
                { "image", "Images/Elements/C"},
                { "summary", "원자번호 6번. 비금속 물질. 우주에서 4번째로 많다." }
            },
            new Dictionary<string, object>
            {
                { "key", 2 },
                { "name", "N\n(질소)" },
                { "image", "Images/Elements/N"},
                { "summary", "원자번호 7번. 비금속 물질. 지구 대기의 78%를 차지한다." }
            },
            new Dictionary<string, object>
            {
                { "key", 3 },
                { "name", "O\n(산소)" },
                { "image", "Images/Elements/O"},
                { "summary", "원자번호 8번. 비금속 물질. 지구 대기의 21%를 차지한다." }
            },
            new Dictionary<string, object>
            {
                { "key", 4 },
                { "name", "Al\n(알루미늄)" },
                { "image", "Images/Elements/Al"},
                { "summary", "원자번호 13번. 금속 물질. 은백색의 부드러운 질감이다." }
            },
            new Dictionary<string, object>
            {
                { "key", 5 },
                { "name", "S\n(황)" },
                { "image", "Images/Elements/S"},
                { "summary", "원자번호 16번. 비금속 물질. 무맛무취이며 생명에 필수 요소이다." }
            },
            new Dictionary<string, object>
            {
                { "key", 6 },
                { "name", "Fe\n(철)" },
                { "image", "Images/Elements/Fe"},
                { "summary", "원자번호 26번. 금속 물질. 우주에 가장 많이 퍼져 있는 중금속이다." }
            },
            new Dictionary<string, object>
            {
                { "key", 7 },
                { "name", "Cu\n(구리)" },
                { "image", "Images/Elements/Cu"},
                { "summary", "원자번호 29번. 금속 물질. 부드럽고 열, 전기 전도성이 매우 높다." }
            },
            new Dictionary<string, object>
            {
                { "key", 8 },
                { "name", "Ag\n(은)" },
                { "image", "Images/Elements/Ag"},
                { "summary", "원자번호 47번. 금속 물질. 무르고 열, 전기 전도성이 높다." }
            },
            new Dictionary<string, object>
            {
                { "key", 9 },
                { "name", "Au\n(금)" },
                { "image", "Images/Elements/Au"},
                { "summary", "원자번호 79번. 금속 물질. 무겁고 어떤 경우에도 녹이 슬지 않는다." }
            }
        };
    }

    public List<Dictionary<string, object>> GetElementList()
    {
        return dictArray;
    }
}
