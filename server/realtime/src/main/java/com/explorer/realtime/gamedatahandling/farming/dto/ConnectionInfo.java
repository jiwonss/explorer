package com.explorer.realtime.gamedatahandling.farming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;

@Getter
@Builder
@AllArgsConstructor
public class ConnectionInfo {

    private Long userId;
    private int mapId;
    private String channelId;

    public static ConnectionInfo of(JSONObject json) {
        return ConnectionInfo.builder()
                .userId(json.getLong("userId"))
                .mapId(json.getInt("mapId"))
                .channelId(json.getString("channelId"))
                .build();
    }

}
