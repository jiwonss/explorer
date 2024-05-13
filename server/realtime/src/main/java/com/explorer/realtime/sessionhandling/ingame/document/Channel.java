package com.explorer.realtime.sessionhandling.ingame.document;

import com.explorer.realtime.sessionhandling.ingame.dto.UserInfo;
import com.explorer.realtime.sessionhandling.ingame.enums.Status;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "channels")
public class Channel {

    @Id
    private String id;

    private String name;

    private Status status;

    private String image;

    private Set<UserInfo> playerList;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Channel from(String name, Set<UserInfo> playerList) {
        return Channel.builder()
                .name(name)
                .status(Status.IN_PROGRESS)
                .playerList(playerList)
                .build();
    }

}
