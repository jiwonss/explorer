package com.explorer.user.domain.user.service.impl;

import com.explorer.user.domain.user.dto.SignupRequest;
import com.explorer.user.domain.user.entity.User;
import com.explorer.user.domain.user.exception.UserErrorCode;
import com.explorer.user.domain.user.exception.UserException;
import com.explorer.user.domain.user.repository.UserRepository;
import com.explorer.user.domain.user.service.AuthService;
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

    @Override
    public void signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new UserException(UserErrorCode.DUPLICATED_USER);
        }

        if (userRepository.existsByNickname(signupRequest.nickname())) {
            throw  new UserException(UserErrorCode.DUPLICATED_NICKNAME);
        }

        User user = User.builder()
                .email(signupRequest.email())
                .password(passwordEncoder.encode(signupRequest.password()))
                .nickname(signupRequest.nickname())
                .build();
        userRepository.save(user);
    }


}
