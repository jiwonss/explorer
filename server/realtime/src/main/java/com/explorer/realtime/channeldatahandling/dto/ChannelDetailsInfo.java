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
public class ChannelDetailsInfo {

    private int headcount;
    private String createdAt;

    public static ChannelDetailsInfo of(int headcount, String createdAt) {
        return ChannelDetailsInfo.builder()
                .headcount(headcount)
                .createdAt(createdAt)
                .build();
    }

}
