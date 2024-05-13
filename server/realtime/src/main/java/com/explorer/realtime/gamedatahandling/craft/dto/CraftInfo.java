package com.explorer.realtime.gamedatahandling.craft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class CraftInfo {

    private int craftId;
    private String itemCategory;
    private int itemId;
    private List<CraftMaterial> materialList;

    public static CraftInfo of(int craftId, String itemCategory, int itemId) {
        return CraftInfo.builder()
                .craftId(craftId)
                .itemCategory(itemCategory)
                .itemId(itemId)
                .materialList(new ArrayList<>())
                .build();
    }

}
