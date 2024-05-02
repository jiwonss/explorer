package com.explorer.realtime.sessionhandling.ingame.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@NoArgsConstructor
@Document
public class Channel {
    @Id
    private String id;
    private String name;
    private Set<Long> memberIds;

    public Channel(String id, String name, Set<Long> memberIds) {
        this.id = id;
        this.name = name;
        this.memberIds = memberIds;
    }
}
