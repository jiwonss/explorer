package com.explorer.user.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(

       @NotBlank
       String refreshToken

) {
}
