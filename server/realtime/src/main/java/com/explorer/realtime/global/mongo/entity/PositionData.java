package com.explorer.realtime.global.mongo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PositionData {
    private String position;
    private String itemCategory;
    private String isFarmable;
    private Integer itemId;


    public PositionData(String position, String itemCategory, String isFarmable, Integer itemId) {
        this.position = position;
        this.itemCategory = itemCategory;
        this.isFarmable = isFarmable;
        this.itemId = itemId;
    }
}
