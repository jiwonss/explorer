package com.explorer.user.domain.user.controller;

import com.explorer.user.domain.user.dto.LogoutRequest;
import com.explorer.user.domain.user.dto.ProfileRequest;
import com.explorer.user.domain.user.service.UserService;
import com.explorer.user.global.common.dto.Message;
import jakarta.validation.Valid;
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
                                 @RequestHeader("Authorization") String accessToken,
                                 @RequestBody LogoutRequest request) {
        userService.logout(userId, accessToken.substring(7), request.refreshToken());
        return ResponseEntity.ok().body(Message.success());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> selectDetailUserInfo(@RequestHeader("X-Authorization-Id") Long userId) {
        return ResponseEntity.ok(Message.success(userService.selectDetailUserInfo(userId)));
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateUserInfo(@RequestHeader("X-Authorization-Id") Long userId,
                                            @RequestBody @Valid ProfileRequest profileRequest) {
        userService.updateUserInfo(userId, profileRequest.toEntity());
        return ResponseEntity.ok(Message.success());
    }

}
