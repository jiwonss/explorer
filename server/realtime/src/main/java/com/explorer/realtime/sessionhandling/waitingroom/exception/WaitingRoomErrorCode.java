package com.explorer.realtime.sessionhandling.waitingroom.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum WaitingRoomErrorCode {

    EXCEEDING_CAPACITY("대기방의 인원이 초과하였습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;
}
