package com.explorer.realtime.sessionhandling.ingame.document;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "channels")
public class Channel {

    @Id
    private String id;

    private String name;

    private Set<Long> playerList;

    @CreatedDate
    private LocalDateTime createdAt;

    public static Channel from(String name, Set<Long> playerList) {
        return Channel.builder()
                .name(name)
                .playerList(playerList)
                .build();
    }

}
