package com.explorer.user.domain.user.dto;

import jakarta.validation.constraints.NotNull;

public record LogoutRequest(

       @NotNull String refreshToken

) {
}
