package com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.dto;


import lombok.*;

import java.util.StringJoiner;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class InventoryInfo {

    private int inventoryIdx;
    private String itemCategory;
    private int itemId;
    private int itemCnt;
    private int isFull;

    public static InventoryInfo of(int inventoryIdx, String itemCategory, int itemId, int itemCnt, int isFull) {
        return InventoryInfo.builder()
                .inventoryIdx(inventoryIdx)
                .itemCategory(itemCategory)
                .itemId(itemId)
                .itemCnt(itemCnt)
                .isFull(isFull)
                .build();
    }

    public static InventoryInfo ofString(int inventoryIdx, String inventoryInfo) {
        if (inventoryInfo.isEmpty()) {
            return InventoryInfo.builder()
                    .inventoryIdx(inventoryIdx)
                    .itemCategory("none")
                    .build();
        }
        String[] result = inventoryInfo.split(":");
        return InventoryInfo.builder()
                .inventoryIdx(inventoryIdx)
                .itemCategory(result[0])
                .itemId(Integer.parseInt(result[1]))
                .itemCnt(Integer.parseInt(result[2]))
                .isFull(Integer.parseInt(result[3]))
                .build();
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(":");
        stringJoiner.add(itemCategory)
                .add(String.valueOf(itemId))
                .add(String.valueOf(itemCnt))
                .add(String.valueOf(isFull));
        return stringJoiner.toString();
    }

}
