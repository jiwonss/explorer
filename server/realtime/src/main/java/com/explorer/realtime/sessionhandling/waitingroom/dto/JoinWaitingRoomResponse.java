package com.explorer.realtime.sessionhandling.waitingroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JoinWaitingRoomResponse {

    private String nickname;
    private int avatar;
    private PositionInfo positionInfo;

    public static JoinWaitingRoomResponse of(String nickname, int avatar, PositionInfo positionInfo) {
        return  JoinWaitingRoomResponse.builder()
                .nickname(nickname)
                .avatar(avatar)
                .positionInfo(positionInfo)
                .build();
    }

}
