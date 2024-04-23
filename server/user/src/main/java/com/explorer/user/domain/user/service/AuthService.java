package com.explorer.user.domain.user.service;

import com.explorer.user.domain.user.dto.LoginRequest;
import com.explorer.user.domain.user.dto.LoginResponse;
import com.explorer.user.domain.user.dto.SignupRequest;
import com.explorer.user.domain.user.dto.TokenInfo;

public interface AuthService {

    void signup(SignupRequest signupRequest);
    boolean checkEmailDuplicates(String email);
    boolean checkNicknameDuplicates(String nickname);
    LoginResponse login(LoginRequest loginRequest);

}
