package com.explorer.realtime.channeldatahandling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class EndedChannelInfo {

    private String channelId;
    private String channelName;
    private String image;
    private String createdAt;
    private String endedAt;
    private List<String> playerList;

}
