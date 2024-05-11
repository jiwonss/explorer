package com.explorer.realtime.global.mongo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document
public class MapData {
    @Id
    private String id;
    private String channelId;
    private Integer mapId;
    private List<PositionData> positions;
}
