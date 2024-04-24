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
    public GameObject loginPage;// 초기 로그인 화면 오브젝트
    public GameObject signPage;// 회원가입을 눌렀을때 나타나는 오브젝트
    public GameObject ChannelChoose; // 로그인 후에 보이는 채녈 목록
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
        // 회원가입 취소 시 입력 필드 및 피드백 텍스트 초기화
        loginID.text = "";
        loginPW.text = "";
    }
    public void CancelSignUp()
    {
        // 회원가입 취소 시 입력 필드 및 피드백 텍스트 초기화
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
        // 영문자, 숫자, 특수문자 중에서 최소 하나씩을 포함하며, 길이가 8에서 15 사이인 문자열을 나타내는 정규식
        string pattern = @"^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,15}$";

        // 정규식과 매치되는지 확인
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

        // 유효성 검사
        if (CheckOkPw(password))
        {
            if (password == passwordConfirm)
            {
                Debug.Log("회원가입 성공");
                CancelSignUp();
                signPage.SetActive(false);
                loginPage.SetActive(true);
            }
            else
            {
                signUpInfoText.text = "비밀번호와 비밀번호 확인이 일치하지 않습니다.";
            }
        }
        else
        {
            // 유효하지 않은 비밀번호입니다.
            Debug.Log("비밀번호는 알파벳, 숫자,@$!%*?&를 각각 1개 이상 포함해야합니다. ");
            signUpInfoText.text = "비밀번호는 알파벳, 숫자, 특수문자(@$!%*?&)를 각각 1개 이상 포함해야합니다.";
        }
    }

    public void SignUpCancel()
    {
        CancelSignUp();
        Debug.Log("이거 캔슬사인업 찍혔어");
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

        // 정규식과 매치되는지 확인
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
        // 유효성 검사
        if (CheckOkId(id))
        {
            //if (id == passwordConfirm)
            //{
                Debug.Log("아이디 조건만족");
                signUpInfoText.text = "";
                nickNameCheckBTN.interactable = true;
                signUpnickname.interactable = true;
                signUpId.interactable = false;
                idCheckBTN.interactable = false;
            //}
            //else
            //{
            //    signUpInfoText.text = "아이디가 중복됩니다.";
            //}
        }
        else
        {
            // 유효하지 않은 id입니다.
            Debug.Log("id는 영어,숫자가 각각 1개 이상 포함된 6 글자 이상 15 글자 이하입니다. ");
            signUpInfoText.text = "id는 영어,숫자가 각각 1개 이상 포함된 6 글자 이상 15 글자 이하입니다. ";
            
        }
    }

    bool CheckOkNickName(string nickname)
    {
        string pattern = @"^[A-Za-z0-9가-힣]{2,8}$";

        // 정규식과 매치되는지 확인
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
        // 유효성 검사
        if (CheckOkNickName(nickname))
        {
            //if (id == passwordConfirm)
            //{
            Debug.Log("닉네임 조건만족");
            signUpInfoText.text = "";
            signUpPwChenk.interactable = true;
            signUpPw.interactable = true;
            signUpnickname.interactable = false;
            nickNameCheckBTN.interactable = false;
            //}
            //else
            //{
            //    signUpInfoText.text = "닉네임이 중복됩니다.";
            //}
        }
        else
        {
            // 유효하지 않은 id입니다.
            Debug.Log("닉네임은 2-8 글자 사이의 영어, 숫자, 한글로만 이루어집니다.");
            signUpInfoText.text = "닉네임은 2-8 글자 사이의 영어, 숫자, 한글로만 이루어집니다.";
            signUpPwChenk.interactable = false;
            signUpPw.interactable = false;
        }
    }
}





