package com.explorer.realtime.sessionhandling.waitingroom.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum WaitingRoomErrorCode {

    FAILED_GENERATE_TEAMCODE("팀코드 생성을 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE),
    NOT_EXIST_TEAMCODE("존재하지 않는 팀코드입니다.", HttpStatus.BAD_REQUEST),
    EXIST_USER("이미 대기방에 존재하는 유저입니다.", HttpStatus.BAD_REQUEST),
    EXCEEDING_CAPACITY("대기방의 인원이 초과하였습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;
}
