using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Net.Sockets;
using System.IO;
using Newtonsoft.Json;

// 파밍 아이템 정보 클래스
public class FarmObject
{
    public string type;
    public string eventType;
    public int mapId;
    public int cid;
    public int userId;
    public float posX;
    public float posY;
    public float posZ;

    public FarmObject(string type, string eventType, int mapId, int cid, int userId, float posX, float posY, float posZ)
    {
        this.type = type;
        this.eventType = eventType;
        this.mapId = mapId;
        this.cid = cid;
        this.userId = userId;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }
}



public class ObjectFarming : MonoBehaviour
{
    private TCPClientManager tcpClientManager;
    private bool isFarming = false; // 파밍 진행 여부 체크
    private ProgressBarControl progressBar;

    public void Awake()
    {
        //tcpClientManager = TCPClientManager.Instance;
        //if (tcpClientManager == null)
        //{
        //    Debug.LogError("TCPClientManager가 초기화되지 않았습니다.");
        //    return;
        //}

    }

    public void OjectFarm(RaycastHit hit, Vector3 position)
    {

        if (isFarming)
        {
            return;
        }
        //if (tcpClientManager == null)
        //{
        //    Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
        //    return;
        //}

        //// TCPClientManager의 GetStream 메서드를 사용하여 NetworkStream 가져오기
        //NetworkStream stream = tcpClientManager.GetStream();
        //if (stream == null)
        //{
        //    Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
        //    return;
        //}

        // 파밍 요청 보냄
        //FarmObject canfarming = new FarmObject("ingame", "event", 0, 1, 2, position.x, position.y, position.z);
        //string json = JsonConvert.SerializeObject(canfarming);
        //// JSON 데이터 전송
        //tcpClientManager.SendTCPRequest(json);

        // 서버로부터 파밍 가능 반환 시, 파밍 시간 받아서 프로그래스바 지속
        // hit된 오브젝트에서 ProgressBarControl 컴포넌트 찾기
        progressBar = hit.collider.GetComponent<ProgressBarControl>();
        if (progressBar == null)
        {
            Debug.LogError("ProgressBarControl 컴포넌트를 찾을 수 없습니다.");
            return;
        }
        progressBar.StartProgress(10);
        isFarming = true;


    }

    public void FinishFarming()
    {
        // 끝난 경우 파밍 완료, 파밍 오브젝트 정보 보냄
        Debug.Log("파밍완료~~~~~");
        isFarming = false;
    }

    public void CancleFarming()
    {
        if (isFarming && progressBar != null)
        {
            progressBar.StopProgress();
            isFarming = false;
        }
    }
}
