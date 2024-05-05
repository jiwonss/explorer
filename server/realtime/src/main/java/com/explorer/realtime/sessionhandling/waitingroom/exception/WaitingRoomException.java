package com.explorer.realtime.sessionhandling.waitingroom.exception;

import lombok.Getter;

@Getter
public class WaitingRoomException extends RuntimeException {

    private final WaitingRoomErrorCode errorCode;

    public WaitingRoomException(WaitingRoomErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
