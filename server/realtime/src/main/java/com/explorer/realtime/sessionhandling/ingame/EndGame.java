package com.explorer.realtime.sessionhandling.ingame;

import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndGame {

    private final SessionManager sessionManager;
    private final RedisService redisService;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final Broadcasting broadcasting;

    public Mono<Void> process(String channel, UserInfo userInfo) {
        // 만약 channelRepository에 user가 나 밖에 없을 때는 delete해야한다.
//        Mono<Void> removeConnection = Mono.fromRunnable(() -> sessionManager.removeConnection(String.valueOf(userInfo.getUserId())));
        return channelRepository.count(channel)
                .flatMap(userCount -> {
                    if (userCount == 1) {
                        return deleteAndLeave(channel, userInfo.getUserId());
                    } else {
                        return leave(channel, userInfo.getUserId())
                                .then(Mono.fromRunnable(() -> sessionManager.removeConnection(String.valueOf(userInfo.getUserId()))));
                    }
                });
    }

    private Mono<Void> deleteAndLeave(String channel, Long userId) {
        // 채널을 삭제하고 유저를 채널에서 제거합니다.
        return delete(channel)
                .then(leave(channel, userId))
//                .then(sessionManager.removeConnection(String.valueOf(userId)))
//                .then();
                .then(Mono.fromRunnable(() -> sessionManager.removeConnection(String.valueOf(userId))));
    }


    private Mono<Void> delete(String channel) {
        return channelRepository.find(channel)
                .flatMap(userId -> userRepository.delete(Long.valueOf(userId)))
                .then();
    }

    private Mono<Void> leave(String channel, Long userId) {
        Mono<Void> removeUserFromChannel = channelRepository.leave(channel, userId).then();
        Mono<Void> deleteUser = userRepository.delete(userId).then();
        Mono<Void> deleteUidFromRedis = redisService.deleteUidFromTeamCode(channel, String.valueOf(userId)).then();
        Mono<Void> removeConnection = Mono.fromRunnable(() -> sessionManager.removeConnection(String.valueOf(userId)));

        Map<String, String> map = new HashMap<>();
        map.put("userId", String.valueOf(userId));

        Mono<Void> broadcast = broadcasting.broadcasting(
                channel,
                MessageConverter.convert(Message.success( map))
        );
        return Mono.when(removeUserFromChannel, deleteUser, deleteUidFromRedis, removeConnection, broadcast)
                .then();
    }
}
