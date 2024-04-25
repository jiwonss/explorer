package com.explorer.user.domain.user.entity;

import com.explorer.user.domain.user.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_login_id", unique = true, nullable = false)
    private String loginId;

    @Column(name = "user_password", nullable = false)
    private String password;

    @Column(name = "user_nickname", unique = true, nullable = false)
    private String nickname;

    @Column(name = "user_avatar")
    private int avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private Status status;

    public void updateProfile(User user) {
        this.nickname = user.getNickname();
        this.avatar = user.getAvatar();
    }

}
