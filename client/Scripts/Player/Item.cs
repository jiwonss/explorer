using System.Collections;
using System.Collections.Generic;
using UnityEngine;
// 아이템 타입을 리스트로 만들어서 타입을 지정해준다.
public enum ItemType
{
    //장비
    Equipment,
    //소비
    Consumable,
    //기타
    Etc,
}

[System.Serializable]
public class Item
{
    //아이템의 이름, 사진, 타입을 변수로 지정
    // 아이템의 이름
    public string itemName;
    // 아이템의 사진
    public Sprite itemImage;
    // 아이템의 타입 
    // 타입을 알아볼 수 있는게 따로 없으니까 위에서 어떤 종류들인지 리스트에 리스트에 있는 것중에서 선택
    public ItemType itemtypes;

    public bool Use()
    {
        //사용가능한 아이템인지 판별하여 사용한다.
        return false;
    }
}
