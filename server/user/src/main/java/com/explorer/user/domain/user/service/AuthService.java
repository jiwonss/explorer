package com.explorer.user.domain.user.service;

import com.explorer.user.domain.user.dto.LoginResponse;
import com.explorer.user.domain.user.dto.TokenInfo;

public interface AuthService {

    void signup(String loginId, String password, String nickname);
    boolean checkEmailDuplicates(String loginId);
    boolean checkNicknameDuplicates(String nickname);
    LoginResponse login(String loginId, String password);
    TokenInfo reissue(String accessToken, String refreshToken);

}
