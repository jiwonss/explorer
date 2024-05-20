package com.explorer.realtime.channeldatahandling.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ChannelInfo {

    private String channelId;
    private String channelName;

}
