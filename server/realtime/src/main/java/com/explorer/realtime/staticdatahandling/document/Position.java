package com.explorer.realtime.staticdatahandling.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Getter
@Builder
@ToString
@AllArgsConstructor
@Document(collection = "positions")
public class Position {

    @Id
    private String _id;

    private int mapId;

    private Set<String> positions;

    public static Position from(int mapId) {
        return Position.builder()
                .mapId(mapId)
                .build();
    }

    public static Position from(int mapId, Set<String> positions) {
        return Position.builder()
                .mapId(mapId)
                .positions(positions)
                .build();
    }

    public void addPosition(String position) {
        this.positions.add(position);
    }

}
