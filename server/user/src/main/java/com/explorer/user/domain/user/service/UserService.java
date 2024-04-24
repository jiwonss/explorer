package com.explorer.user.domain.user.service;

public interface UserService {

    void logout(Long userId, String refreshToken);

}
