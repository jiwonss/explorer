package com.explorer.realtime.staticdatahandling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AvailableInventoryItemInfo {

    private String category;
    private int id;

    public static AvailableInventoryItemInfo of(String itemInfo) {
        String[] result = itemInfo.split(":");
        return AvailableInventoryItemInfo.builder()
                .category(result[0])
                .id(Integer.parseInt(result[1]))
                .build();
    }

}
