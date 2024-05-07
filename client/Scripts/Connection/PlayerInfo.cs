using UnityEngine;

public class PlayerInfo : MonoBehaviour
{
    public int userId;
    public string nickname;
    public int avatar;

    public void SetUserInfo(int userId, string nickname, int avatar)
    {
        this.userId = userId;
        this.nickname = nickname;
        this.avatar = avatar;

        // Debug.Log("PlayerInfo: SetUserInfo called - userId: " + userId + ", nickname: " + nickname + ", avatar: " + avatar);
    }

}
