package com.explorer.realtime.global.mongo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InventoryData {
    private Integer inventoryIdx;
    private String itemCategory;
    private Integer itemId;
    private Integer itemCnt;
    private String isFull;

    public InventoryData(Integer inventoryIdx, String itemCategory, Integer itemId, Integer itemCnt, String isFull) {
        this.inventoryIdx = inventoryIdx;
        this.itemCategory = itemCategory;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
        this.isFull = isFull;
    }
}
