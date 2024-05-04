    using System;
    using System.Collections;
    using System.Collections.Generic;
    using UnityEngine;
    using UnityEngine.UI;
    using TMPro;
    using UnityEngine.SceneManagement;
    using System.Text.RegularExpressions;
    using static UnityEngine.UIElements.UxmlAttributeDescription;
    using UnityEngine.Networking;
    using Newtonsoft.Json;
    using System.Net.Sockets;

    public class AuthControl : MonoBehaviour
    {
        private TCPClientManager tcpClientManager;

        private string accessToken;
        private string refreshToken;

        [Header("LoginPage")]
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
        private string URL = "localhost:8000";


        [Header("LoginField")]
        public TMP_InputField loginID;
        public TMP_InputField loginPW;
   
        [Header("SignupField")]
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

        [Header("LogOutModal")]
        public GameObject LogOutModal;
        public bool LogOutOnOff = false;
        public Button LogOut;
        public Button Exit;

        [Header("JoinRoomInput")]
        public TextMeshProUGUI JoinRoomInput;


        // Start is called before the first frame update
        private void Start()
        {
            SignUpBtn.onClick.AddListener(GoSignUp);
            SignUpYesBTN.onClick.AddListener(() => SignUp());
            SignUpNoBTN.onClick.AddListener(SignUpCancel);
            LoginBTN.onClick.AddListener(Login);
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
            LogOut.onClick.AddListener(FuncLogOut);
            Exit.onClick.AddListener(FuncExit);
            title.SetActive(true);
            loginPage.SetActive(true);
            loginPW.contentType = TMP_InputField.ContentType.Password;
            signUpPw.contentType = TMP_InputField.ContentType.Password;
            signUpPwChenk.contentType = TMP_InputField.ContentType.Password;
            signUpPwChenk.interactable = false;
            signUpPw.interactable = false;
            nickNameCheckBTN.interactable = false;
            signUpnickname.interactable = false;
            SignUpYesBTN.interactable = false;
            loginID.characterLimit = 15;
            loginPW.characterLimit = 15;
            signUpId.characterLimit = 15;
            signUpnickname.characterLimit = 8;
            signUpPw.characterLimit = 15;
            signUpPwChenk.characterLimit = 15;
        
        }
    public void FuncLogOut()
    {
        // accessToken과 refreshToken을 현재 클래스의 private 변수에서 가져옴
        StartCoroutine(SendLogoutRequest(TokenManager.Instance.GetAccessToken(), TokenManager.Instance.GetRefreshToken()));
    }

    public void FuncExit()
    {
        Debug.Log("나가기 들어옴");
        // accessToken과 refreshToken을 현재 클래스의 private 변수에서 가져옴
        StartCoroutine(SendLogoutRequest(TokenManager.Instance.GetAccessToken(), TokenManager.Instance.GetRefreshToken()));
        Application.Quit();
    }





        IEnumerator SendLogoutRequest(string accessToken,string refreshToken)
        {
            // 1. 로그인 상태를 확인하고 유효한 토큰이 있는지 확인
            if (string.IsNullOrEmpty(accessToken) || string.IsNullOrEmpty(refreshToken))
            {
                Debug.LogError("로그인되지 않았습니다. 로그인 후 다시 시도하세요.");
                yield break;
            }

            // 2. 만료된 토큰을 갱신하거나 새로운 토큰을 얻을 수 있는지 확인
            // 이 부분은 필요에 따라 추가되어야 합니다.
            // 만약 토큰 갱신이나 새로운 토큰을 얻는 기능이 있다면 이 곳에 해당 코드를 추가해야 합니다.

            // 3. 로그인되어 있는 상태에서만 로그아웃을 시도
            string url = URL + "/user/users/logout";

            string jsonData = "{\"refreshToken\": \"" + refreshToken + "\"}";

            UnityWebRequest request = new UnityWebRequest(url, "POST");
            byte[] bodyRaw = System.Text.Encoding.UTF8.GetBytes(jsonData);
            request.uploadHandler = new UploadHandlerRaw(bodyRaw);
            request.downloadHandler = new DownloadHandlerBuffer();
            request.SetRequestHeader("Content-Type", "application/json");
            request.SetRequestHeader("Authorization", "Bearer " + accessToken); // AccessToken을 헤더에 추가

            yield return request.SendWebRequest();

            if (request.result == UnityWebRequest.Result.Success)
            {
                // 성공적으로 로그아웃 요청을 보낸 경우
                // 서버 응답 데이터 처리
                Debug.Log("로그아웃 성공");
                ChannelChoose.SetActive(false);
                title.SetActive(true);
                loginPage.SetActive(true);

        }
            else
            {
                // 로그아웃 요청 실패한 경우
                Debug.LogError("로그아웃 실패: " + request.error);
            }   
        }




        private void Update()
        {
            if (Input.GetKeyDown(KeyCode.Escape))
            {
                ToggleObject();
            }
            LogOutModal.SetActive(LogOutOnOff);
        }
        void ToggleObject()
        {
            // isActive 값을 반대로 변경하고 그에 따라 게임 오브젝트를 활성화 또는 비활성화
            LogOutOnOff = !LogOutOnOff;
        }

        public void CancelLogin()
        {
            //로그인 안하고 회원가입 페이지로 이동시 원래 있던 입력값 없애기
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
            signUpPwChenk.interactable = false;
            signUpPw.interactable = false;
            nickNameCheckBTN.interactable = false;
            signUpnickname.interactable = false;
            signUpId.interactable = true;
            idCheckBTN.interactable = true;
            SignUpYesBTN.interactable = false;
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

        [System.Serializable]
        public class DataHeader
        {
            public int successCode;
            public string resultCode;
            public string resultMessage;
        }
        [System.Serializable]
        public class DataBody
        {
            public TokenInfo tokenInfo;
            public UserInfo userInfo;
        }

        [System.Serializable]
        public class SignUpResponse
        {
            public DataHeader dataHeader;
            public DataBody dataBody;
        }

        private IEnumerator SignUpRequest(string loginId, string password, string nickname)
        {
            // 요청할 URL 설정
            string requestUrl = URL + "/user/auth/signup";

            // JSON 데이터 생성
            string jsonData = "{\"loginId\": \"" + loginId + "\", \"password\": \"" + password + "\", \"nickname\": \"" + nickname + "\"}";

            // UnityWebRequest 생성
            UnityWebRequest request = new UnityWebRequest(requestUrl, "POST");

            // JSON 데이터를 바이트 배열로 변환하여 요청에 추가
            byte[] bodyRaw = System.Text.Encoding.UTF8.GetBytes(jsonData);
            request.uploadHandler = new UploadHandlerRaw(bodyRaw);
            request.downloadHandler = new DownloadHandlerBuffer();

            // 요청 헤더 설정
            request.SetRequestHeader("Content-Type", "application/json");

            // 요청 보내기
            yield return request.SendWebRequest();

            // 요청 완료 후 처리
            if (request.result == UnityWebRequest.Result.Success)
            {
                // 서버 응답 확인
                if (request.responseCode == 200)
                {
                    SignUpResponse response = JsonUtility.FromJson< SignUpResponse > (request.downloadHandler.text);
                
                    if (response.dataHeader.successCode == 0)
                    {
                        Debug.Log("회원가입 성공");
                        Debug.Log("응답 데이터: " + request.downloadHandler.text);
                        Debug.Log(request.responseCode);
                        // 성공한 경우 처리
                        CancelSignUp();
                        signPage.SetActive(false);
                        loginPage.SetActive(true);
                    }
                    else
                    {
                        string message = response.dataHeader.resultMessage;
                        signUpInfoText.text = message;
                        if(message == "이미 존재하는 유저입니다.")
                        {
                            signUpId.interactable = true;
                            idCheckBTN.interactable = true;
                        }
                        else
                        {
                            nickNameCheckBTN.interactable = true;
                            signUpnickname.interactable = true;
                        }
                    }
                
                }
            
                else
                {
                    Debug.LogError("서버 오류: " + request.responseCode);
                    // 서버 오류 처리
                }
            }
            else
            {
                Debug.LogError("Error: " + request.error);
                // 실패한 경우 처리
            }
        }

        public void SignUp()
        {
            string loginId = signUpId.text;
            string password = signUpPw.text;
            string passwordConfirm = signUpPwChenk.text;
            string nickname = signUpnickname.text;

            // 유효성 검사
            if (CheckOkPw(password))
            {
                if (password == passwordConfirm)
                {
                    StartCoroutine(SignUpRequest(loginId, password, nickname));
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

        public void Login()
        {

            string loginId = loginID.text;
            string password = loginPW.text;
            StartCoroutine(LoginRequest(loginId, password));

            loginID.text = "";
            loginPW.text = "";

        }

        [System.Serializable]
        public class LoginResponse
        {
            public DataHeader dataHeader;
            public DataBody dataBody;
        }

        [System.Serializable]
        public class TokenInfo
        {
            public string accessToken;
            public string refreshToken;
        }

        [System.Serializable]
        public class UserInfo{
            public int userId;
            public string nickname;
            public int avatar;
        }


    // 로그인 요청
    private IEnumerator LoginRequest(string loginId, string password)
        {
            string requestUrl = URL + "/user/auth/login";
            string jsonData = "{\"loginId\": \"" + loginId + "\", \"password\": \"" + password + "\"}";
            UnityWebRequest request = new UnityWebRequest(requestUrl, "POST");
            request.uploadHandler = new UploadHandlerRaw(System.Text.Encoding.UTF8.GetBytes(jsonData));
            request.downloadHandler = new DownloadHandlerBuffer();
            request.SetRequestHeader("Content-Type", "application/json");
            Debug.Log("Request value" + request + "++++request.DownHandler" + request.downloadHandler.text);
            yield return request.SendWebRequest();

            if (request.result == UnityWebRequest.Result.Success && request.responseCode == 200)
            {
                // Debug.Log("서버 응답 데이터: " + request.downloadHandler.text);
                LoginResponse response = JsonUtility.FromJson<LoginResponse>(request.downloadHandler.text);
                if (response.dataHeader.successCode == 0)
                {
                    TokenManager.Instance.SetTokens(response.dataBody.tokenInfo.accessToken, response.dataBody.tokenInfo.refreshToken);
                    // Debug.Log("Access Token: " + TokenManager.Instance.GetAccessToken());
                    // Debug.Log("Refresh Token: " + TokenManager.Instance.GetRefreshToken());

                    // 유저 정보 저장
                    int userId = response.dataBody.userInfo.userId;
                    string nickname = response.dataBody.userInfo.nickname;
                    int avatar = response.dataBody.userInfo.avatar;;

                    UserInfoManager.Instance.SetUserInfo(userId, nickname, avatar);

                    //TCP
                    string ip = ServerConfigLoader.serverIp;
                    int port = int.Parse(ServerConfigLoader.serverPort);

                    // TCPClientManager의 인스턴스 초기화
                    TCPClientManager.Instance.Init(ip, port);

                    if (TCPClientManager.Instance.Connect())
                    {
                        Debug.Log("TCP 연결 성공 !!");

                        // TCP 연결 시 채널 목록 조회
                        loginPage.SetActive(false);
                        ChannelChoose.SetActive(true);
                        // TCP 이벤트 수신 시작
                        TCPClientManager.Instance.StartReceiving();
                    }
                    else
                    {
                        Debug.LogError("TCP 연결 실패!");
                    }
                }
                else
                {
                    Debug.LogError("로그인 실패: " + response.dataHeader.resultMessage);
                }
            }
            else
            {
                Debug.LogError("Login request failed: " + request.error);
            }
        }

        public void SetTokens(string accessToken, string refreshToken)
        {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            Debug.Log("Access Token Updated: " + accessToken);
            Debug.Log("Refresh Token Updated: " + refreshToken);
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

        // 팀코드 입력 모달
        public void JoinAnotherRoom()
        {
            JoinNewChannel.SetActive(true);
        }

        public class JoinRoomRequest
        {
            public string type;
            public string eventName;
            public string teamCode;
            public int userId;
            public string nickname;
            public int avatar;

            public JoinRoomRequest(string type, string eventName, string teamCode, int userId, string nickname, int avatar)
            {
                this.type = type;
                this.eventName = eventName;
                this.teamCode = teamCode;
                this.userId = userId;
                this.nickname = nickname;
                this.avatar = avatar;
            }

        }
        // 팀코드로 입장 요청
        public void JoinOk()
        {
            JoinNewChannel.SetActive(false);
            string teamCode = JoinRoomInput.text;
            Debug.Log("teamCode input : " + teamCode);

            // TCP Check
            tcpClientManager = TCPClientManager.Instance;
            if (tcpClientManager == null)
            {
                Debug.LogError("TCPClientManager가 초기화되지 않았습니다.");
                return;
            }
            if (tcpClientManager == null)
            {
                Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
                return;
            }
            NetworkStream stream = tcpClientManager.GetStream();
            if (stream == null)
            {
                Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
                return;
            }
            // 유저 정보
            UserInfoManager userInfoManager = UserInfoManager.Instance;
            int userId = userInfoManager.GetUserId();
            string nickname = userInfoManager.GetNickname();
            int avatar = userInfoManager.GetAvatar();
            
            JoinRoomRequest request = new JoinRoomRequest("waitingRoomSession", "joinWaitingRoom", teamCode, userId, nickname, avatar);
            string json = JsonConvert.SerializeObject(request);
            tcpClientManager.SendTCPRequest(json);

            // SceneManager.LoadScene("GameRoom");
        }

        public void JoinCancel()
        {
            JoinNewChannel.SetActive(false);
        }

        public void MakeAnotherRoom()
        {
            NewMakeChannel.SetActive(true);
        }

        // 방 생성 요청
        public class MakeRoomRequest
        {
            public string type;
            public string eventName;
            public int userId;
            public string nickname;
            public int avatar;

            public MakeRoomRequest(string type, string eventName, int userId, string nickname, int avatar)
            {
                this.type = type;
                this.eventName = eventName;
                this.userId = userId;
                this.nickname = nickname;
                this.avatar = avatar;
            }
        }
        public void MakeOk()
        {
            NewMakeChannel.SetActive(false);
            // TCP Check
            tcpClientManager = TCPClientManager.Instance;
            if (tcpClientManager == null)
            {
                Debug.LogError("TCPClientManager가 초기화되지 않았습니다.");
                return;
            }
            if (tcpClientManager == null)
            {
                Debug.LogError("TCPClientManager가 설정되지 않았습니다.");
                return;
            }
            NetworkStream stream = tcpClientManager.GetStream();
            if (stream == null)
            {
                Debug.LogError("TCPClientManager의 NetworkStream이 존재하지 않습니다.");
                return;
            }
            // 저장된 유저 정보
            UserInfoManager userInfoManager = UserInfoManager.Instance;
            int userId = userInfoManager.GetUserId();
            string nickname = userInfoManager.GetNickname();
            int avatar = userInfoManager.GetAvatar();


            // 방 생성 메시지 발신
            MakeRoomRequest request = new MakeRoomRequest("waitingRoomSession", "createWaitingRoom", userId, nickname, avatar);
            string json = JsonConvert.SerializeObject(request);
            tcpClientManager.SendTCPRequest(json);
        }

        public void MakeRoom()
        {   
            SceneManager.LoadScene("TempScene2");
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
                SignUpYesBTN.interactable = true;
                signUpnickname.interactable = false;
                nickNameCheckBTN.interactable = false;

                //}
                //else
                //{
                //    signUpInfoText.text = "닉네임이 중복됩니다."a;
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





