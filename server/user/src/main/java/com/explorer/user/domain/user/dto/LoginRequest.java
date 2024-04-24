package com.explorer.user.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank
        String loginId,

        @NotBlank
        String password

) {
}
