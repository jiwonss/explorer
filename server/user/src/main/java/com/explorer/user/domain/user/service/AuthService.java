package com.explorer.user.domain.user.service;

import com.explorer.user.domain.user.dto.SignupRequest;

public interface AuthService {

    void signup(SignupRequest signupRequest);

}
