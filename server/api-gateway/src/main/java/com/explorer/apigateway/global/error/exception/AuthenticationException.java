package com.explorer.apigateway.global.error.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {

    private final AuthenticationErrorCode errorCode;

    public AuthenticationException(AuthenticationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}