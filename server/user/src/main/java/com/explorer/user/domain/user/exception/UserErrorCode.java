package com.explorer.user.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@AllArgsConstructor
public enum UserErrorCode {

    DUPLICATED_USER("이미 존재하는 유저입니다.", BAD_REQUEST),
    DUPLICATED_NICKNAME("이미 존재하는 닉네임입니다.", BAD_REQUEST),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다.", BAD_REQUEST),
    INVALID_NEW_PASSWORD("비밀번호가 일치하지 않습니다.", BAD_REQUEST),
    NOT_EXIST_USER("존재하지 않는 유저입니다.", BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;
    
}
