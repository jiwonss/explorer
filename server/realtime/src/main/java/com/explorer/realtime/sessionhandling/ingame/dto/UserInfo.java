package com.explorer.realtime.sessionhandling.ingame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class UserInfo {

    private Long userId;
    private String nickname;
    private int avatar;

    public static UserInfo of(Long userId, String nickname, int avatar) {
        return UserInfo.builder()
                .userId(userId)
                .nickname(nickname)
                .avatar(avatar)
                .build();
    }

}
