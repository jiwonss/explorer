package com.explorer.user.domain.user.controller;

import com.explorer.user.domain.user.dto.ChangePasswordRequest;
import com.explorer.user.domain.user.dto.LoginRequest;
import com.explorer.user.domain.user.dto.SignupRequest;
import com.explorer.user.domain.user.dto.TokenRequest;
import com.explorer.user.domain.user.service.AuthService;
import com.explorer.user.global.common.dto.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest signupRequest) {
        authService.signup(signupRequest.loginId(), signupRequest.password(), signupRequest.nickname());
        return ResponseEntity.ok(Message.success());
    }

    @GetMapping("/email")
    public ResponseEntity<?> checkEmailDuplicates(@RequestParam String email) {
        return ResponseEntity.ok(Message.success(authService.checkEmailDuplicates(email)));
    }

    @GetMapping("/nickname")
    public ResponseEntity<?> checkNicknameDuplicates(@RequestParam String nickname) {
        return ResponseEntity.ok(Message.success(authService.checkNicknameDuplicates(nickname)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(Message.success(authService.login(loginRequest.loginId(), loginRequest.password())));
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody TokenRequest tokenRequest) {
        return ResponseEntity.ok(Message.success(authService.reissue(tokenRequest.accessToken(), tokenRequest.refreshToken())));
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        authService.changePassword(changePasswordRequest.loginId(), changePasswordRequest.newPassword(), changePasswordRequest.confirmNewPassword());
        return ResponseEntity.ok(Message.success());
    }

}
