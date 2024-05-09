package com.explorer.realtime.gamedatahandling.farming.exception;

import lombok.Getter;

@Getter
public class FarmingException extends RuntimeException {

    private final FarmingErrorCode errorCode;

    public FarmingException(FarmingErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
