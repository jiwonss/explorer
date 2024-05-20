using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class UserInfoManager : MonoBehaviour
{
    private static UserInfoManager instance;

    public static UserInfoManager Instance
    {
        get
        {
            if (instance == null)
            {
                instance = FindObjectOfType<UserInfoManager>();
                if (instance == null)
                {
                    GameObject singleton = new GameObject("UserInfoManager");
                    instance = singleton.AddComponent<UserInfoManager>();
                    DontDestroyOnLoad(singleton);
                }
            }
            return instance;
        }
    }

    private int userId;
    private string nickname;
    private int avatar;

    public void SetUserInfo(int userId, string nickname, int avatar)
    {
        this.userId = userId;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public int GetUserId()
    {
        return userId;
    }

    public string GetNickname()
    {
        return nickname;
    }

    public int GetAvatar()
    {

        return avatar;
    }

    public void ChangeProfile(string nickname, int avatar)
    {
        this.nickname = nickname;
        this.avatar = avatar;
    }
}
