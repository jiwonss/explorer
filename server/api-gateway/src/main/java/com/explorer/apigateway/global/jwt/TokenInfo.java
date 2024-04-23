package com.explorer.apigateway.global.jwt;

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
