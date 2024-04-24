package com.explorer.user.domain.user.service.impl;

import com.explorer.user.domain.user.repository.UserRepository;
import com.explorer.user.domain.user.service.UserService;
import com.explorer.user.global.component.jwt.JwtProvider;
import com.explorer.user.global.component.jwt.repository.BlackListRepository;
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
    private final BlackListRepository blackListRepository;

    @Transactional
    @Override
    public void logout(Long userId, String accessToken, String refreshToken) {
        blackListRepository.save(accessToken, jwtProvider.getExpirationDate(accessToken));
        refreshTokenRepository.delete(String.valueOf(userId));
    }

}
