package com.explorer.realtime.initializing.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@NoArgsConstructor
@Document
public class Map {
    @Id
    private Integer mapId;
    private Set<String> position;

    public Map(Integer mapId, Set<String> position){
        this.mapId = mapId;
        this.position = position;
    }
}
