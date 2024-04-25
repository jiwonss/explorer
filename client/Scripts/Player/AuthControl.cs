using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;
using UnityEngine.SceneManagement;
using System.Text.RegularExpressions;

public class AuthControl : MonoBehaviour
{
    [Header ("LoginPage")]
    public GameObject loginPage;// ÃÊ±â ·Î±×ÀÎ È­¸é ¿ÀºêÁ§Æ®
    public GameObject signPage;// È¸¿ø°¡ÀÔÀ» ´­·¶À»¶§ ³ªÅ¸³ª´Â ¿ÀºêÁ§Æ®
    public GameObject ChannelChoose; // ·Î±×ÀÎ ÈÄ¿¡ º¸ÀÌ´Â Ã¤³ã ¸ñ·Ï
    public GameObject title;
    public Button SignUpBtn;
    public Button SignUpYesBTN;
    public Button SignUpNoBTN;
    public Button idCheckBTN;
    public Button nickNameCheckBTN;
    public Button LoginBTN;


    [Header ("LoginField")]
    public TMP_InputField loginID;
    public TMP_InputField loginPW;

    [Header ("SignupField")]
    public TMP_InputField signUpId;
    public TMP_InputField signUpnickname;
    public TMP_InputField signUpPw;
    public TMP_InputField signUpPwChenk;
    public TextMeshProUGUI signUpInfoText;

    [Header("ProfilePage")]
    public GameObject ProfilePage;
    public GameObject ProfileChanger;
    public Image ProfileImage;
    public Button ProfileDesignLeftBtn;
    public Button ProfileDesignRightBtn;
    public TextMeshProUGUI NowNickName;
    public TextMeshProUGUI NickNameChange;
    public Button ProfileChangeBtn;
    public Button ProfileChangeAcceptBtn;
    public Button endingPlanetBtn;


    [Header("RoomChannelSelect")]
    public GameObject ExistRoomOBJ;
    public GameObject NoneRoomOBJ;
    public GameObject NewMakeChannel;
    public GameObject JoinNewChannel;
    public Button ExistChannelBtn;
    public Button ExistChannelJoinBtn;
    public Button ExistChannelDeleteBtn;
    public Button NoneChannelBtn;
    public Button NewChannelJoinBtn;
    public Button JoinOkBtn;
    public Button JoinCancelBtn;
    public Button MakeOkBtn;
    public Button MakeCancelBtn;
    public Button MakeNewChannelBtn;



    // Start is called before the first frame update
    private void Start()
    {
        SignUpBtn.onClick.AddListener(GoSignUp);
        SignUpYesBTN.onClick.AddListener(() => SignUp());
        SignUpNoBTN.onClick.AddListener(SignUpCancel);
        LoginBTN.onClick.AddListener(GoChanel);
        ProfileChangeBtn.onClick.AddListener(EditProfile);
        ProfileChangeAcceptBtn.onClick.AddListener(EditAccept);
        ExistChannelBtn.onClick.AddListener(IfExistRoom);
        NoneChannelBtn.onClick.AddListener(IfNoneRoom);
        //ExistChannelJoinBtn.onClick.AddListener(IfNoneRoom);
        //ExistChannelDeleteBtn.onClick.AddListener(IfNoneRoom);
        NewChannelJoinBtn.onClick.AddListener(JoinAnotherRoom);
        JoinOkBtn.onClick.AddListener(JoinOk);
        idCheckBTN.onClick.AddListener(validId);
        nickNameCheckBTN.onClick.AddListener(validNickName);
        JoinCancelBtn.onClick.AddListener(JoinCancel);
        MakeNewChannelBtn.onClick.AddListener(MakeAnotherRoom);
        MakeOkBtn.onClick.AddListener(MakeOk);
        MakeCancelBtn.onClick.AddListener(MakeCancel);
        title.SetActive(true);
        loginPage.SetActive(true);
        loginPW.contentType = TMP_InputField.ContentType.Password;
        signUpPw.contentType = TMP_InputField.ContentType.Password;
        signUpPwChenk.contentType = TMP_InputField.ContentType.Password;
        signUpPwChenk.interactable = false;
        signUpPw.interactable = false;
        nickNameCheckBTN.interactable = false;
        signUpnickname.interactable = false;
    }

    public void CancelLogin()
    {
        // È¸¿ø°¡ÀÔ Ãë¼Ò ½Ã ÀÔ·Â ÇÊµå ¹× ÇÇµå¹é ÅØ½ºÆ® ÃÊ±âÈ­
        loginID.text = "";
        loginPW.text = "";
    }
    public void CancelSignUp()
    {
        // È¸¿ø°¡ÀÔ Ãë¼Ò ½Ã ÀÔ·Â ÇÊµå ¹× ÇÇµå¹é ÅØ½ºÆ® ÃÊ±âÈ­
        signUpId.text = "";
        signUpnickname.text = "";
        signUpPw.text = "";
        signUpPwChenk.text = "";
        signUpInfoText.text = "";
    }
    public void EditProfile()
    {
        ProfileChanger.SetActive(true);
        ProfilePage.SetActive(false);
    }

    public void EditAccept()
    {
        ProfileChanger.SetActive(false);
        ProfilePage.SetActive(true);
    }

    public void GoSignUp()
    {
        CancelLogin();
        loginPage.SetActive(false);
        signPage.SetActive(true);


    }
    bool CheckOkPw(string password)
    {
        // ¿µ¹®ÀÚ, ¼ýÀÚ, Æ¯¼ö¹®ÀÚ Áß¿¡¼­ ÃÖ¼Ò ÇÏ³ª¾¿À» Æ÷ÇÔÇÏ¸ç, ±æÀÌ°¡ 8¿¡¼­ 15 »çÀÌÀÎ ¹®ÀÚ¿­À» ³ªÅ¸³»´Â Á¤±Ô½Ä
        string pattern = @"^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,15}$";

        // Á¤±Ô½Ä°ú ¸ÅÄ¡µÇ´ÂÁö È®ÀÎ
        if (Regex.IsMatch(password, pattern))
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public void SignUp()
    {

        string password = signUpPw.text;
        string passwordConfirm = signUpPwChenk.text;

        // À¯È¿¼º °Ë»ç
        if (CheckOkPw(password))
        {
            if (password == passwordConfirm)
            {
                Debug.Log("È¸¿ø°¡ÀÔ ¼º°ø");
                CancelSignUp();
                signPage.SetActive(false);
                loginPage.SetActive(true);
            }
            else
            {
                signUpInfoText.text = "ºñ¹Ð¹øÈ£¿Í ºñ¹Ð¹øÈ£ È®ÀÎÀÌ ÀÏÄ¡ÇÏÁö ¾Ê½À´Ï´Ù.";
            }
        }
        else
        {
            // À¯È¿ÇÏÁö ¾ÊÀº ºñ¹Ð¹øÈ£ÀÔ´Ï´Ù.
            Debug.Log("ºñ¹Ð¹øÈ£´Â ¾ËÆÄºª, ¼ýÀÚ,@$!%*?&¸¦ °¢°¢ 1°³ ÀÌ»ó Æ÷ÇÔÇØ¾ßÇÕ´Ï´Ù. ");
            signUpInfoText.text = "ºñ¹Ð¹øÈ£´Â ¾ËÆÄºª, ¼ýÀÚ, Æ¯¼ö¹®ÀÚ(@$!%*?&)¸¦ °¢°¢ 1°³ ÀÌ»ó Æ÷ÇÔÇØ¾ßÇÕ´Ï´Ù.";
        }
    }

    public void SignUpCancel()
    {
        CancelSignUp();
        Debug.Log("ÀÌ°Å Äµ½½»çÀÎ¾÷ ÂïÇû¾î");
        signPage.SetActive(false);
        loginPage.SetActive(true);
    }

    public void GoChanel()
    {
        loginPage.SetActive(false);
        title.SetActive(false);
        ChannelChoose.SetActive(true);
    }


    public void IfExistRoom()
    {
        Debug.Log("ExistOK");
        NoneRoomOBJ.SetActive(false);
        ExistRoomOBJ.SetActive(true);
    }
    public void IfNoneRoom()
    {
        Debug.Log("NoneOk");
        ExistRoomOBJ.SetActive(false);
        NoneRoomOBJ.SetActive(true);
    }

    public void JoinAnotherRoom()
    {
        JoinNewChannel.SetActive(true);
    }

    public void JoinOk()
    {
        JoinNewChannel.SetActive(false);
        SceneManager.LoadScene("GameRoom");
    }

    public void JoinCancel()
    {
        JoinNewChannel.SetActive(false);
    }

    public void MakeAnotherRoom()
    {
        NewMakeChannel.SetActive(true);
    }

    public void MakeOk()
    {
        NewMakeChannel.SetActive(false);
        SceneManager.LoadScene("GameRoom");
    }

    public void MakeCancel()
    {
        NewMakeChannel.SetActive(false);
    }


    bool CheckOkId(string id)
    {
        string pattern = @"^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,15}$";

        // Á¤±Ô½Ä°ú ¸ÅÄ¡µÇ´ÂÁö È®ÀÎ
        if (Regex.IsMatch(id, pattern))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public void validId()
    {
        string id = signUpId.text;
        // À¯È¿¼º °Ë»ç
        if (CheckOkId(id))
        {
            //if (id == passwordConfirm)
            //{
                Debug.Log("¾ÆÀÌµð Á¶°Ç¸¸Á·");
                signUpInfoText.text = "";
                nickNameCheckBTN.interactable = true;
                signUpnickname.interactable = true;
                signUpId.interactable = false;
                idCheckBTN.interactable = false;
            //}
            //else
            //{
            //    signUpInfoText.text = "¾ÆÀÌµð°¡ Áßº¹µË´Ï´Ù.";
            //}
        }
        else
        {
            // À¯È¿ÇÏÁö ¾ÊÀº idÀÔ´Ï´Ù.
            Debug.Log("id´Â ¿µ¾î,¼ýÀÚ°¡ °¢°¢ 1°³ ÀÌ»ó Æ÷ÇÔµÈ 6 ±ÛÀÚ ÀÌ»ó 15 ±ÛÀÚ ÀÌÇÏÀÔ´Ï´Ù. ");
            signUpInfoText.text = "id´Â ¿µ¾î,¼ýÀÚ°¡ °¢°¢ 1°³ ÀÌ»ó Æ÷ÇÔµÈ 6 ±ÛÀÚ ÀÌ»ó 15 ±ÛÀÚ ÀÌÇÏÀÔ´Ï´Ù. ";
            
        }
    }

    bool CheckOkNickName(string nickname)
    {
        string pattern = @"^[A-Za-z0-9°¡-ÆR]{2,8}$";

        // Á¤±Ô½Ä°ú ¸ÅÄ¡µÇ´ÂÁö È®ÀÎ
        if (Regex.IsMatch(nickname, pattern))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public void validNickName()
    {
        string nickname = signUpnickname.text;
        // À¯È¿¼º °Ë»ç
        if (CheckOkNickName(nickname))
        {
            //if (id == passwordConfirm)
            //{
            Debug.Log("´Ð³×ÀÓ Á¶°Ç¸¸Á·");
            signUpInfoText.text = "";
            signUpPwChenk.interactable = true;
            signUpPw.interactable = true;
            signUpnickname.interactable = false;
            nickNameCheckBTN.interactable = false;
            //}
            //else
            //{
            //    signUpInfoText.text = "´Ð³×ÀÓÀÌ Áßº¹µË´Ï´Ù.";
            //}
        }
        else
        {
            // À¯È¿ÇÏÁö ¾ÊÀº idÀÔ´Ï´Ù.
            Debug.Log("´Ð³×ÀÓÀº 2-8 ±ÛÀÚ »çÀÌÀÇ ¿µ¾î, ¼ýÀÚ, ÇÑ±Û·Î¸¸ ÀÌ·ç¾îÁý´Ï´Ù.");
            signUpInfoText.text = "´Ð³×ÀÓÀº 2-8 ±ÛÀÚ »çÀÌÀÇ ¿µ¾î, ¼ýÀÚ, ÇÑ±Û·Î¸¸ ÀÌ·ç¾îÁý´Ï´Ù.";
            signUpPwChenk.interactable = false;
            signUpPw.interactable = false;
        }
    }
}





