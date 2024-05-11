using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TokenManager : MonoBehaviour
{
    private static TokenManager instance;

    public static TokenManager Instance
    {
        get
        {
            if (instance == null)
            {
                instance = FindObjectOfType<TokenManager>();
                if (instance == null)
                {
                    //GameObject singleton = new GameObject(typeof(TokenManager).Name);
                    //instance = singleton.AddComponent<TokenManager>();
                    //DontDestroyOnLoad(singleton);
                    GameObject singleton = new GameObject("TokenManager");
                    instance = singleton.AddComponent<TokenManager>();
                    DontDestroyOnLoad(singleton);
                }
            }
            return instance;
        }
    }

    private string accessToken;
    private string refreshToken;

    public void SetTokens(string accessToken, string refreshToken)
    {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        // Debug.Log("Access Token Updated: " + accessToken);
        // Debug.Log("Refresh Token Updated: " + refreshToken);
    }

    //public string GetAccessToken()
    //{
    //    return accessToken;
    //}

    //public string GetRefreshToken()
    //{
    //    return refreshToken;
    //}
    public string GetAccessToken() => accessToken;
    public string GetRefreshToken() => refreshToken;
}
