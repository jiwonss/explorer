using UnityEngine;
using UnityEngine.SceneManagement;

public class RenderIngameUI : MonoBehaviour
{
    public GameObject IngameUI;

    // Start is called before the first frame update
    void Start()
    {
        string currentSceneName = SceneManager.GetActiveScene().name;

        if (currentSceneName == "IngameTest")
        {
            IngameUI.SetActive(true);
        }
        else
        {
            IngameUI.SetActive(false);
        }
    }

}
