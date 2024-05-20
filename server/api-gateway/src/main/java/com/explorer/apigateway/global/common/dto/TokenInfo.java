package com.explorer.apigateway.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenInfo {

    private String userId;
    private String nickname;
    private int avatar;

}
