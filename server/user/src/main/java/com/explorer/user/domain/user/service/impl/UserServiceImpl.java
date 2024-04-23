package com.explorer.user.domain.user.service.impl;

import com.explorer.user.domain.user.repository.UserRepository;
import com.explorer.user.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public boolean checkEmailDuplicates(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean checkNicknameDuplicates(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
