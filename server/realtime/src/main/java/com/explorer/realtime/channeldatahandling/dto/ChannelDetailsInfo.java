package com.explorer.realtime.channeldatahandling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ChannelDetailsInfo {

    private Long userId;
    private boolean online;

    public static ChannelDetailsInfo of( Long userId, boolean online) {
        return ChannelDetailsInfo.builder()
                .userId(userId)
                .online(online)
                .build();
    }

}
