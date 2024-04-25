package com.explorer.user.domain.user.service;

import com.explorer.user.domain.user.dto.ProfileRequest;
import com.explorer.user.domain.user.dto.ProfileResponse;
import com.explorer.user.domain.user.entity.User;

public interface UserService {

    void logout(Long userId, String accessToken, String refreshToken);
    ProfileResponse selectDetailUserInfo(Long userId);
    void updateUserInfo(Long userId, User user);

}
