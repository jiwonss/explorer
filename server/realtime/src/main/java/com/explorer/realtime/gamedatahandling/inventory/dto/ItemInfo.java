package com.explorer.realtime.gamedatahandling.inventory.dto;

import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.dto.InventoryInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ItemInfo {

    private String itemCategory;
    private int itemId;
    private int itemCnt;

    public static ItemInfo of(String itemCategory, int itemId, int itemCnt) {
        return ItemInfo.builder()
                .itemCategory(itemCategory)
                .itemId(itemId)
                .itemCnt(itemCnt)
                .build();
    }

}
