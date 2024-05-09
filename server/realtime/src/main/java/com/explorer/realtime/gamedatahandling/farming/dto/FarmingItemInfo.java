package com.explorer.realtime.gamedatahandling.farming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class FarmingItemInfo {

    private String channelId;
    private int mapId;
    private String position;
    private Long userId;

    public static FarmingItemInfo of(JSONObject json) {

        return FarmingItemInfo.builder()
                .channelId(json.getString("channelId"))
                .mapId(json.getInt("mapId"))
                .position(json.getString("position"))
                .userId(json.getLong("userId"))
                .build();
    }

}
