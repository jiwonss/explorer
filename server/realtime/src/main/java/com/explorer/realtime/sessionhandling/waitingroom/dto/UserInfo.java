package com.explorer.realtime.sessionhandling.waitingroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

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

    public static UserInfo ofJson(JSONObject json) {
        return UserInfo.builder()
                .userId(json.getLong("userId"))
                .nickname(json.getString("nickname"))
                .avatar(json.getInt("avatar"))
                .build();
    }

}
