package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
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
        log.info("들오오니?");
        // 만약 channelRepository에 user가 나 밖에 없을 때는 delete해야한다.
//        Mono<Void> removeConnection = Mono.fromRunnable(() -> sessionManager.removeConnection(String.valueOf(userInfo.getUserId())));
        check(channel).subscribe(
                count -> {
                    if (count == 1) {
                        log.info("한명입니다.");
                        delete(channel);
                        leave(channel, userInfo.getUserId());
                    } else {
                        log.info("하명이 아닙ㅁ니다.");
                        leave(channel, userInfo.getUserId());
                    }
                }
        );
        return Mono.empty();
    }


    private void delete(String channel) {
        channelRepository.find(channel).subscribe(
                userId -> userRepository.delete(Long.valueOf(userId)).subscribe()
                );
        channelRepository.delete(String.valueOf(channel)).subscribe();
        redisService.deleteFromTeamCode(channel).subscribe();
    }

    private void leave(String channel, Long userId) {
        channelRepository.leave(channel, userId).subscribe();
        userRepository.delete(userId).subscribe();
        redisService.deleteUidFromTeamCode(channel, String.valueOf(userId)).subscribe();
        sessionManager.removeConnection(String.valueOf(userId));

        Map<String, String> map = new HashMap<>();
        map.put("userId", String.valueOf(userId));

        broadcasting.broadcasting(channel, MessageConverter.convert(Message.success("endGame", CastingType.BROADCASTING, map))).subscribe();
    }

    private Mono<Long> check(String channel) {
        return channelRepository.count(channel).flatMap(
                count -> {
                    return Mono.just(count);
                }
        );
    }
}
