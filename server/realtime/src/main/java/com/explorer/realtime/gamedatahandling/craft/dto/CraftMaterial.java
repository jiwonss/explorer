package com.explorer.realtime.gamedatahandling.craft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class CraftMaterial {

    private String itemCategory;
    private int itemId;
    private int itemCnt;

    public static CraftMaterial of(String itemCategory, int itemId, int itemCnt) {
        return CraftMaterial.builder()
                .itemCategory(itemCategory)
                .itemId(itemId)
                .itemCnt(itemCnt)
                .build();
    }

}
