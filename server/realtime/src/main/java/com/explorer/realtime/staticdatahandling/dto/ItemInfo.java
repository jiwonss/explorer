package com.explorer.realtime.staticdatahandling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ItemInfo {

    private int id;
    private int cnt;

    public static ItemInfo of(int id, int cnt) {
        return ItemInfo.builder()
                .id(id)
                .cnt(cnt)
                .build();
    }

}
