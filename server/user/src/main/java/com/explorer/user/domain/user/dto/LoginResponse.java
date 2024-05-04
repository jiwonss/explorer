package com.explorer.user.domain.user.dto;

import com.explorer.user.global.common.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private TokenInfo tokenInfo;
    private UserInfo userInfo;

}
