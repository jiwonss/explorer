package com.explorer.realtime.global.mongo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
public class LaboratoryLevel {
    @Id
    private String id;
    private String channelId;
    private Integer labId;
    private String level;

    public LaboratoryLevel(String channelId, Integer labId, String level) {
            this.channelId = channelId;
            this.labId = labId;
            this.level = level;
    }
}
