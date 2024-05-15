package com.explorer.user.domain.user.dto;

import com.explorer.user.domain.user.entity.User;
import jakarta.validation.constraints.*;

public record ProfileRequest(

        @NotBlank
        @Size(min = 2, max = 8)
        String nickname,

        @NotNull
        @Min(value = 0)
        @Max(value = 10)
        Integer avatar

) {

    public User toEntity() {
        return User.builder()
                .nickname(nickname)
                .avatar(avatar)
                .build();
    }

}
