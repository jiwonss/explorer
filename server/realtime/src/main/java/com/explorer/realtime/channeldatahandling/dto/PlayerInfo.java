package com.explorer.realtime.channeldatahandling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class PlayerInfo {

    private String nickname;
    private boolean online;

    public static PlayerInfo of(String nickname, boolean online) {
        return PlayerInfo.builder()
                .nickname(nickname)
                .online(online)
                .build();
    }

}
