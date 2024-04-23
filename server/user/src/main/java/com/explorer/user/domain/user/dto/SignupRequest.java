package com.explorer.user.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignupRequest (

        @NotNull @Size(min = 6, max = 15) String email,
        @NotNull @Size(min = 8, max = 15) String password,
        @NotNull @Size(min = 2, max = 8) String nickname

) {
}
