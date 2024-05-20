package com.explorer.chat.chathandling.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChattingErrorCode {
    FAILED_SEND_CHAT("채팅 전송을 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

    private final String message;
    private final HttpStatus httpStatus;
}
