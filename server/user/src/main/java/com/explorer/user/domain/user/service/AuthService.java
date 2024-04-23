package com.explorer.user.domain.user.service;

import com.explorer.user.domain.user.dto.LoginRequest;
import com.explorer.user.domain.user.dto.LoginResponse;
import com.explorer.user.domain.user.dto.SignupRequest;

public interface AuthService {

    void signup(SignupRequest signupRequest);
    LoginResponse login(LoginRequest loginRequest);

}
