package com.explorer.user.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TokenRequest(

        @NotBlank
        String accessToken,

        @NotBlank
        String refreshToken

) {
    
}