package com.explorer.user.domain.user.dto;

import com.explorer.user.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProfileResponse {

    private String nickname;
    private int avatar;

    public static ProfileResponse fromUser(User user) {
        return ProfileResponse.builder()
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }

}
