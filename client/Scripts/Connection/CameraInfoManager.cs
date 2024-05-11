using UnityEngine;
using UnityEngine.SceneManagement;

public class CameraInfoManager : MonoBehaviour
{
    private Camera mainCamera;

    void Awake()
    {
        DontDestroyOnLoad(this.gameObject);
        FindMainCamera();
    }


    private void OnSceneLoaded(Scene scene, LoadSceneMode mode)
    {
        FindMainCamera();  // 새 씬에서 메인 카메라 찾기
    }

    private void FindMainCamera()
    {
        mainCamera = Camera.main;  // 메인 카메라 검색

        if (mainCamera != null)
        {
            Debug.Log("Main Camera found: " + mainCamera.name);
        }
        else
        {
            Debug.LogError("No main camera found in the scene.");
        }
    }

    public void SetAsMainCamera(Camera newMainCamera)
    {
        if (newMainCamera != null)
        {
            if (mainCamera != null)
            {
                mainCamera.enabled = false;  // Disable the old main camera
            }
            mainCamera = newMainCamera;
            mainCamera.enabled = true;
            mainCamera.tag = "MainCamera";

            // Optionally, update position and rotation
            mainCamera.transform.position = newMainCamera.transform.position;
            mainCamera.transform.rotation = newMainCamera.transform.rotation;
        }
    }

    public string UpdateCameraInfo()
    {
        // 메인 카메라가 있는 상태에서 추가적인 로직 필요할 경우
        if (mainCamera != null)
        {
            Vector3 position = mainCamera.transform.parent.position;
            Quaternion rotation = mainCamera.transform.parent.rotation;

            string formattedOutput = string.Format("{0}:{1}:{2}:{3}:{4}:{5}",
                position.x, position.y, position.z,
                rotation.eulerAngles.x, rotation.eulerAngles.y, rotation.eulerAngles.z);

            return formattedOutput;
        }
        else
        {
            Debug.LogError("Main camera reference lost or not found.");
            return "Error: Main camera not found";
        }
    }
}
