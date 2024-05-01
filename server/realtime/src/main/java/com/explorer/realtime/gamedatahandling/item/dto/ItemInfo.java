package com.explorer.realtime.gamedatahandling.item.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;

@Getter
@Builder
@AllArgsConstructor
public class ItemInfo {

    private int itemIdx;
    private int itemCnt;
    private PositionInfo positionInfo;

    public static ItemInfo of(JSONObject json) {
        PositionInfo positionInfo = PositionInfo.of(json);
        return ItemInfo.builder()
                .itemIdx(json.getInt("itemIdx"))
                .itemCnt(json.getInt("itemCnt"))
                .positionInfo(positionInfo)
                .build();
    }

}
