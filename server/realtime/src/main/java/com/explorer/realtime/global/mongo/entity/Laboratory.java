package com.explorer.realtime.global.mongo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document
public class Laboratory {
    @Id
    private String id;
    private String channelId;
    private Integer labId;
    private String itemCategory;
    private List<Integer> itemCnt;

    public Laboratory(String itemCategory, List<Integer> itemCnt) {
        this.itemCategory = itemCategory;
        this.itemCnt = itemCnt;
    }
}
