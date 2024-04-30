package com.explorer.realtime.sessionhandling.ingame;

import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.redis.RedisService;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.netty.Connection;


@Slf4j
@Service
@RequiredArgsConstructor
public class RestartGame {

//    private final ChannelMongoRepository channelMongoRepository;
    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final RedisService redisService;

    public void process(String channel, UserInfo userInfo, Connection connection) {
        // 클라이언트 측에서 받아온 avatar와 nickname으로 redis userId value 업데이트
        userRepository.save(userInfo);

        // channelId값은 클라이언트가 가지고 있으니까 redis에 검색 key로 있으면 추가 없으면 생성
        createConnectionInfo(channel, String.valueOf(userInfo.getUserId()), connection);
    }

    private void createConnectionInfo(String channel, String userId, Connection connection) {
        sessionManager.setConnection(userId, connection);
        redisService.saveUidToTeamCode(channel, userId, "restart game").subscribe();
    }


}

