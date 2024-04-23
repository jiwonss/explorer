package com.explorer.user.global.error;

import com.explorer.user.domain.user.exception.UserException;
import com.explorer.user.global.common.dto.Message;
import com.explorer.user.global.component.jwt.exception.JwtException;
import com.explorer.user.global.error.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> invalidInputExceptionHandler(MethodArgumentNotValidException e) {
        log.debug(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.ok(Message.fail(String.valueOf(ErrorCode.INVALID_INPUT), ErrorCode.INVALID_INPUT.getMessage()));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> jwtExceptionHandler(JwtException e) {
        log.debug(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.ok(Message.fail(String.valueOf(e.getErrorCode()), e.getMessage()));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> userExceptionHandler(UserException e) {
        log.debug(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.ok(Message.fail(String.valueOf(e.getErrorCode()), e.getMessage()));
    }

}