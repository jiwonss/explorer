package com.explorer.realtime.gamedatahandling.laboratory.dto;

import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;

@Getter
@Builder
public class UserInfo {

    private String channelId;
    private Long userId;

    public static UserInfo of(JSONObject json) {

        return UserInfo.builder()
                .channelId(json.getString("channelId"))
                .userId(json.getLong("userId"))
                .build();
    }
}
