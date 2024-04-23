package com.explorer.user.domain.user.dto;

import lombok.Builder;

@Builder
public record TokenRequest(

        String accessToken, String refreshToken

) {
    
}