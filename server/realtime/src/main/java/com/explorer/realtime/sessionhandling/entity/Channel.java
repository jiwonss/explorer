package com.explorer.realtime.sessionhandling.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document
public class Channel {
    @Id
    private String id;
    private String name;
    private List<Long> memberIds;

    public Channel(String id, String name, List<Long> memberIds) {
        this.id = id;
        this.name = name;
        this.memberIds = memberIds;
    }
}
