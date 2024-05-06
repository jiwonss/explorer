package com.explorer.realtime.gamedatahandling.farming.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ItemInfo {

    private String category;
    private int itemId;
    private int itemCnt;
    private String position;

    public static ItemInfo of(String itemInfo, String position) {
        String[] result = itemInfo.split(":");
        return ItemInfo.builder()
                .category(result[0])
                .itemId(Integer.parseInt(result[1]))
                .itemCnt(Integer.parseInt(result[2]))
                .position(position)
                .build();
    }

}
