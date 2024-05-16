package com.explorer.realtime.gamedatahandling.tool.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ToolErrorCode {

    OUT_OF_RANGE_INDEX("인덱스가 인벤토리 최대 인덱스의 범위를 벗어났습니다.", HttpStatus.BAD_REQUEST),
    ATTACHED_TOOL("이미 장작된 도구입니다.", HttpStatus.BAD_REQUEST),
    MISMATCHED_INVENTORY_IDX("현재 장착된 도구와의 인벤토리 idx가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    EMPTY_INVENTORY("인벤토리가 비어있습니다.", HttpStatus.BAD_REQUEST),
    NOT_TOOL("도구가 아닙니다.", HttpStatus.BAD_REQUEST),
    NO_ATTACHED_TOOL("현재 도구를 장착하고 있지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

}
