package com.explorer.realtime.gamedatahandling.component.common.boxinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;

@Getter
@Builder
public class UserConnectionInfo {

    private Long userId;
    private String channelId;

    public static UserConnectionInfo of(JSONObject json) {
        return UserConnectionInfo.builder()
                .userId(json.getLong("userId"))
                .channelId(json.getString("channelId"))
                .build();
    }
}
