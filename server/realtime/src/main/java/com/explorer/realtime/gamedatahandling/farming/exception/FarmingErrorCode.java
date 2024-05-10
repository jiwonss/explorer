package com.explorer.realtime.gamedatahandling.farming.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FarmingErrorCode {

    EXCEEDING_CAPACITY("인벤토리가 가득 차서 아이템을 추가할 수 없습니다.", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus httpStatus;

}
