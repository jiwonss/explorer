package com.explorer.user.domain.user.service;

public interface UserService {

    boolean checkEmailDuplicates(String email);
    boolean checkNicknameDuplicates(String nickname);

}
