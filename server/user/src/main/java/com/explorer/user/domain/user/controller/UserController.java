package com.explorer.user.domain.user.controller;

import com.explorer.user.domain.user.service.UserService;
import com.explorer.user.global.common.dto.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/email")
    public ResponseEntity<?> checkEmailDuplicates(@RequestParam String email) {
        return ResponseEntity.ok(Message.success(userService.checkEmailDuplicates(email)));
    }

    @GetMapping("/nickname")
    public ResponseEntity<?> checkNicknameDuplicates(@RequestParam String nickname) {
        return ResponseEntity.ok(Message.success(userService.checkNicknameDuplicates(nickname)));
    }

}
