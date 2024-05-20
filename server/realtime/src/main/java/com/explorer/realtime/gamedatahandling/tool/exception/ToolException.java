package com.explorer.realtime.gamedatahandling.tool.exception;

import lombok.Getter;

@Getter
public class ToolException extends RuntimeException {

    private final ToolErrorCode errorCode;

    public ToolException(ToolErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
