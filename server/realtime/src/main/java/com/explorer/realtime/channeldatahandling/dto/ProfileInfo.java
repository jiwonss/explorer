package com.explorer.realtime.channeldatahandling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ProfileInfo {

    private String nickname;
    private int avatar;

}
