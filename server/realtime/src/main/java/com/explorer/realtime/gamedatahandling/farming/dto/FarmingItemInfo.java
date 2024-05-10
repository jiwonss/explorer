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
    private String oldPosition;
    private Long userId;
    private String itemCategory;
    private int itemId;

    public static FarmingItemInfo of(JSONObject json) {

        return FarmingItemInfo.builder()
                .channelId(json.getString("channelId"))
                .mapId(json.getInt("mapId"))
                .position(json.getString("position"))
                .userId(json.getLong("userId"))
                .build();
    }

    public static FarmingItemInfo droppedOf(JSONObject json) {

        return FarmingItemInfo.builder()
                .channelId(json.getString("channelId"))
                .mapId(json.getInt("mapId"))
                .position(json.getString("position"))
                .oldPosition(json.getString("oldPosition"))
                .itemCategory(json.getString("itemCategory"))
                .itemId(json.getInt("itemId"))
                .build();
    }

}
