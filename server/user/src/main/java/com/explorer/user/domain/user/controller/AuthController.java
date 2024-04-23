package com.explorer.user.domain.user.controller;

import com.explorer.user.domain.user.dto.SignupRequest;
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
        authService.signup(signupRequest);
        return ResponseEntity.ok(Message.success());
    }

}
