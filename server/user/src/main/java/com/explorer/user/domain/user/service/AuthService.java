package com.explorer.user.domain.user.service;

import com.explorer.user.domain.user.dto.LoginResponse;

public interface AuthService {

    void signup(String email, String password, String nickname);
    boolean checkEmailDuplicates(String email);
    boolean checkNicknameDuplicates(String nickname);
    LoginResponse login(String email, String password);

}
