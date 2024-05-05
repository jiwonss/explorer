using UnityEngine;
using System.IO;

public class ServerConfigLoader : MonoBehaviour
{
    public static string serverIp;
    public static string serverPort;

    private void Awake()
    {
        LoadServerConfig();
    }

    private void LoadServerConfig()
    {
        string filePath = Path.Combine(Application.streamingAssetsPath, "server_config.json");

        if (File.Exists(filePath))
        {
            string dataAsJson = File.ReadAllText(filePath);
            ServerConfig serverConfig = JsonUtility.FromJson<ServerConfig>(dataAsJson);

            serverIp = serverConfig.server_ip;
            serverPort = serverConfig.server_port.ToString();

        }
        else
        {
            Debug.LogError("Server configuration file not found!");
        }
    }
    [System.Serializable]
    private class ServerConfig
    {
        public string server_ip;
        public int server_port;
    }

}
    
        
    

