package com.explorer.realtime.gamedatahandling.farming.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FarmingErrorCode {

    INVALID_ITEM_CATEGORY_IN_INVENTORY("인벤토리에 들어갈 수 없는 아이템입니다.", HttpStatus.BAD_REQUEST),
    EXCEEDING_CAPACITY("인벤토리가 가득 차서 아이템을 추가할 수 없습니다.", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus httpStatus;

}
