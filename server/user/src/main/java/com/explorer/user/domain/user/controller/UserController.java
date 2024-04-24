package com.explorer.user.domain.user.controller;

import com.explorer.user.domain.user.dto.LogoutRequest;
import com.explorer.user.domain.user.service.UserService;
import com.explorer.user.global.common.dto.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("X-Authorization-Id") Long userId,
                                 @RequestBody LogoutRequest request) {
        userService.logout(userId, request.refreshToken());
        return ResponseEntity.ok().body(Message.success());
    }

}
