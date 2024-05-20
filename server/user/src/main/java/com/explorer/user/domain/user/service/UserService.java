package com.explorer.user.domain.user.service;

import com.explorer.user.domain.user.dto.ProfileResponse;
import com.explorer.user.domain.user.entity.User;
import com.explorer.user.global.common.dto.UserInfo;

public interface UserService {

    void logout(Long userId, String accessToken, String refreshToken);
    ProfileResponse getProfileInfo(Long userId);
    void updateProfileInfo(Long userId, User user);
    UserInfo getUserInfo(String accessToken);

}
