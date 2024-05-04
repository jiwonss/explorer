package com.explorer.user.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class UserInfo {

    private Long userId;
    private String nickname;
    private int avatar;

}
