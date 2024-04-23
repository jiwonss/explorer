package com.explorer.user.domain.user.service.impl;

import com.explorer.user.domain.user.repository.UserRepository;
import com.explorer.user.domain.user.service.UserService;
import com.explorer.user.global.component.jwt.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public void logout(Long userId, String refreshToken) {
        refreshTokenRepository.delete(String.valueOf(userId));
    }

}
