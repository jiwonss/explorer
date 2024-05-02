package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.redis.RedisService;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.repository.ChannelRepository;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;


@Slf4j
@Service
@RequiredArgsConstructor
public class RestartGame {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final RedisService redisService;
    private final Unicasting unicasting;

    public Mono<Void> process(String channel, UserInfo userInfo, Connection connection) {
        // 사용자 정보를 Redis에 저장
        createConnectionInfo(channel, String.valueOf(userInfo.getUserId()), connection);
//        channelRepository.save(channel, userInfo.getUserId()).subscribe();
        userRepository.save(userInfo).subscribe();
        unicasting.unicasting(
                channel,
                String.valueOf(userInfo.getUserId()),
                MessageConverter.convert(Message.success("restartGame", CastingType.UNICASTING))
        );
        return Mono.empty();
    }

    private void createConnectionInfo(String channel, String userId, Connection connection) {
        sessionManager.setConnection(userId, connection);
        log.info("{}", sessionManager.getConnection(userId));
        redisService.saveUidToTeamCode(channel, userId, "restartGame").subscribe();
    }
}

