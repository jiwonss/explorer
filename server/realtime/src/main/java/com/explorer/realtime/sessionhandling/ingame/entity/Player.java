package com.explorer.realtime.sessionhandling.ingame.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class Player {
    @Id
    private String id;
    private Long userId;
    private String channelId;

    //닉네임과 아바타는 클라이언트가 유저가 로그인 할 때 redis에 저장을 해 놓을 것이므로 굳이 저장해 놓을 필요가 없을 것 같다.
//    private String nickname;
//
//    private Integer avartar;

}
