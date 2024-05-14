package com.explorer.realtime.staticdatahandling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DroppedItemInfo {

    private String category;
    private int id;
    private int cnt;

    public static DroppedItemInfo of(String itemInfo) {
        String[] result = itemInfo.split(":");
        return DroppedItemInfo.builder()
                .category(result[0])
                .id(Integer.parseInt(result[1]))
                .cnt(Integer.parseInt(result[2]))
                .build();
    }

}
