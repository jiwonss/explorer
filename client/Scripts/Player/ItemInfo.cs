using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ItemInfo : MonoBehaviour
{
    // 다른 클래스에서 아이템의 인포에 다가올수 있도록 설정
    public static ItemInfo istance;

    private void Awake()
    {
        istance = this;
    }

    public List<Item> itemDB = new List<Item>();
}
