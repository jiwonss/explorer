package com.explorer.realtime.sessionhandling.waitingroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;

@Getter
@Builder
@AllArgsConstructor
public class UserInfo {

    private Long userId;
    private String nickname;
    private int avatar;
    private boolean isLeader;

    public static UserInfo ofUserIdAndNicknameAndAvatar(JSONObject json) {
        return UserInfo.builder()
                .userId(json.getLong("userId"))
                .nickname(json.getString("nickname"))
                .avatar(json.getInt("avatar"))
                .build();
    }

    public static UserInfo ofUserIdAndIsLeader(JSONObject json) {
        return UserInfo.builder()
                .userId(json.getLong("userId"))
                .isLeader(json.getBoolean("isLeader"))
                .build();
    }

}
