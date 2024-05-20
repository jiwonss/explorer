package com.explorer.realtime.gamedatahandling.laboratory.dto;

import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;

@Getter
@Builder
public class ItemInfo {

    private String itemCategory;
    private int itemId;

    public static ItemInfo of(JSONObject json) {

        return ItemInfo.builder()
                .itemCategory(json.getString("itemCategory"))
                .itemId(json.getInt("itemId"))
                .build();

    }
}