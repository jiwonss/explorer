package com.explorer.realtime.gamedatahandling.component.common.boxinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class InventoryItemInfo {

    private String itemCategory;
    private int itemId;

    public static InventoryItemInfo of(String inventoryItemInfo) {
        String[] result = inventoryItemInfo.split(":");
        return InventoryItemInfo.builder()
                .itemCategory(result[0])
                .itemId(Integer.parseInt(result[1]))
                .build();
    }
}
