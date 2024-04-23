package com.explorer.user.domain.user.service.impl;

import com.explorer.user.domain.user.dto.LoginResponse;
import com.explorer.user.domain.user.dto.TokenInfo;
import com.explorer.user.domain.user.entity.User;
import com.explorer.user.domain.user.exception.UserErrorCode;
import com.explorer.user.domain.user.exception.UserException;
import com.explorer.user.domain.user.repository.UserRepository;
import com.explorer.user.domain.user.service.AuthService;
import com.explorer.user.global.component.jwt.JwtProvider;
import com.explorer.user.global.component.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void signup(String email, String password, String nickname) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorCode.DUPLICATED_USER);
        }

        if (userRepository.existsByNickname(nickname)) {
            throw  new UserException(UserErrorCode.DUPLICATED_NICKNAME);
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build();
        userRepository.save(user);
    }

    @Override
    public boolean checkEmailDuplicates(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean checkNicknameDuplicates(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(UserErrorCode.NOT_EXIST_USER));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.issueAccessToken(user.getId(), user.getNickname(), user.getAvartar());
        String refreshToken = jwtProvider.issueRefreshToken();

        refreshTokenRepository.save(String.valueOf(user.getId()), refreshToken);

        return LoginResponse.builder()
                .tokenInfo(
                        TokenInfo.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build()
                )
                .build();
    }


}
