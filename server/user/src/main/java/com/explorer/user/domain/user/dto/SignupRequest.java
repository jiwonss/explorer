package com.explorer.user.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest (

        @NotBlank
        @Size(min = 6, max = 15)
        String loginId,

        @NotBlank
        @Size(min = 8, max = 15)
        String password,

        @NotBlank
        @Size(min = 2, max = 8)
        String nickname

) {
}
