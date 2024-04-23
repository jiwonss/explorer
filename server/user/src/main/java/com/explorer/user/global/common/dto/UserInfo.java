package com.explorer.user.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserInfo {

    private Long userId;
    private String nickname;
    private int avartar;

}
