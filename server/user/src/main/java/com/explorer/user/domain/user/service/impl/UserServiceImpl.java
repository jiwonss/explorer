package com.explorer.user.domain.user.service.impl;

import com.explorer.user.domain.user.dto.ProfileResponse;
import com.explorer.user.domain.user.entity.User;
import com.explorer.user.domain.user.exception.UserErrorCode;
import com.explorer.user.domain.user.exception.UserException;
import com.explorer.user.domain.user.repository.UserRepository;
import com.explorer.user.domain.user.service.UserService;
import com.explorer.user.global.component.jwt.JwtProvider;
import com.explorer.user.global.component.jwt.exception.JwtErrorCode;
import com.explorer.user.global.component.jwt.exception.JwtException;
import com.explorer.user.global.component.jwt.producer.BlackListProducer;
import com.explorer.user.global.component.jwt.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListProducer blackListProducer;

    @Transactional
    @Override
    public void logout(Long userId, String accessToken, String refreshToken) {
        long remainingTime = jwtProvider.getRefreshTokenExpirationDate(refreshToken).getTime() - System.currentTimeMillis();
        boolean isExist = refreshTokenRepository.exist(refreshToken);
        boolean isEqual = refreshTokenRepository.find(String.valueOf(userId)).get().equals(refreshToken);

        if (isExist || !isEqual || remainingTime <= 0) {
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }

        blackListProducer.sendAccessToken(accessToken);
        refreshTokenRepository.delete(String.valueOf(userId));
    }

    @Override
    public ProfileResponse selectDetailUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.NOT_EXIST_USER));
        return ProfileResponse.fromUser(user);
    }

    @Transactional
    @Override
    public void updateUserInfo(Long userId, User user) {
        User target = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.NOT_EXIST_USER));
        target.updateProfile(user);
    }

}
