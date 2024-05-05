using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GameScenePlayerLoader : MonoBehaviour
{
    public Dictionary<int, GameObject> characterPrefabs = new Dictionary<int, GameObject>(); // ��ųʸ� �ʱ�ȭ

    public GameObject prefab0; 
    public GameObject prefab1;
    public GameObject prefab2;
    public GameObject prefab3;
    public GameObject prefab4;
    public GameObject prefab5;

    void Awake()
    {
        characterPrefabs.Add(0, prefab0);
        characterPrefabs.Add(1, prefab1);
        characterPrefabs.Add(2, prefab2);
        characterPrefabs.Add(3, prefab3);
        characterPrefabs.Add(4, prefab4);
        characterPrefabs.Add(5, prefab5);
    }


    void Start()
    {
        string teamCode = TCPMessageHandler.GetTeamCode();
        SpawnCharacter(5, new Vector3(0, 15, 0));

    }

    void Update()
    {

    }

    public void SpawnCharacter(int characterID, Vector3 spawnPosition)
    {
        int receivedCharacterID = characterID;

        GameObject selectedCharacterPrefab = characterPrefabs[receivedCharacterID];
        if (selectedCharacterPrefab != null)
        {
            GameObject spawnedCharacter = Instantiate(selectedCharacterPrefab, transform.position, Quaternion.identity);
        }
        else
        {
            Debug.LogError("Prefab is not assigned for character ID: " + receivedCharacterID);
        }
    }
}
