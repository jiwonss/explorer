package com.explorer.realtime.global.mongo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document
public class Inventory {
    @Id
    private String id;
    private String channelId;
    private Long userId;
    private List<InventoryData> inventoryData;
}
