using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ChannelManager : MonoBehaviour
{
    public static ChannelManager instance;
    
    public static ChannelManager Instance
    {
        get
        {
            if(instance == null)
            {
                instance = FindObjectOfType<ChannelManager>();
                if(instance == null )
                {
                    GameObject singleton = new GameObject("ChannelManager");
                    instance = singleton.AddComponent<ChannelManager>();
                    DontDestroyOnLoad(singleton);
                }
            }
            return instance;
        }
    }

    void Awake()
    {
        if (instance == null)
        {
            instance = this;
            DontDestroyOnLoad(this.gameObject);
        }
        else if (instance != this)
        {
            Destroy(gameObject);
        }
    }

    private string teamCode;
    private string channelId;
    private int mapId;
    
    public void SetTeamCode(string teamCode)
    {
        this.teamCode = teamCode;
    }
    public string GetTeamCode()
    {
        return teamCode;
    }
    public void DeleteTeamCode()
    {
        teamCode = null;
    }
    public void SetChannelId(string channelId)
    {
        this.channelId = channelId;
    }
    public string GetChannelId()
    {
        return channelId;
    }
    public void DeleteChannelId()
    {
        channelId = null;
    }
    public void SetMapId(int mapId)
    {
        this.mapId = mapId;
    }
    public int GetMapId()
    { 
        return mapId; 
    }
    public void DeleteMapId()
    {
        mapId = 0;
    }
}
