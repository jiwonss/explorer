package com.explorer.realtime.gamedatahandling.tool.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ToolErrorCode {

    EMPTY_INVENTORY("인벤토리가 비어있습니다.", HttpStatus.BAD_REQUEST),
    NOT_TOOL("도구가 아닙니다.", HttpStatus.BAD_REQUEST),
    NO_ATTACHED_TOOL("현재 도구를 장착하고 있지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

}
