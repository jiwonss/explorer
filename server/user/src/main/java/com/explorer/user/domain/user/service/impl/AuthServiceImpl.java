package com.explorer.user.domain.user.service.impl;

import com.explorer.user.domain.user.dto.LoginResponse;
import com.explorer.user.domain.user.dto.TokenInfo;
import com.explorer.user.domain.user.entity.User;
import com.explorer.user.domain.user.exception.UserErrorCode;
import com.explorer.user.domain.user.exception.UserException;
import com.explorer.user.domain.user.repository.UserRepository;
import com.explorer.user.domain.user.service.AuthService;
import com.explorer.user.global.common.dto.UserInfo;
import com.explorer.user.global.component.jwt.JwtProps;
import com.explorer.user.global.component.jwt.JwtProvider;
import com.explorer.user.global.component.jwt.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
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

    private final JwtProps jwtProps;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void signup(String loginId, String password, String nickname) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new UserException(UserErrorCode.DUPLICATED_USER);
        }

        if (userRepository.existsByNickname(nickname)) {
            throw  new UserException(UserErrorCode.DUPLICATED_NICKNAME);
        }

        User user = User.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build();
        userRepository.save(user);
    }

    @Override
    public boolean checkEmailDuplicates(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    @Override
    public boolean checkNicknameDuplicates(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public LoginResponse login(String loginId, String password) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserException(UserErrorCode.NOT_EXIST_USER));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.issueAccessToken(user.getId(), user.getNickname(), user.getAvatar());
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

    @Override
    public TokenInfo reissue(String accessToken, String refreshToken) {
        UserInfo userInfo = jwtProvider.parseAccessTokenByBase64(accessToken);

        User user = userRepository.findById(userInfo.getUserId())
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_EXIST_USER));

        String newAccessToken = jwtProvider.issueAccessToken(user.getId(), user.getNickname(), user.getAvatar());
        String newRefreshToken = jwtProvider.issueRefreshToken();

        refreshTokenRepository.save(String.valueOf(userInfo.getUserId()), newRefreshToken);

        return TokenInfo.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

    }

    @Transactional
    @Override
    public void changePassword(String loginId, String newPassword, String confirmNewPassword) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserException(UserErrorCode.NOT_EXIST_USER));

        if (!newPassword.equals(confirmNewPassword)) {
            throw new UserException(UserErrorCode.INVALID_NEW_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
    }


}
