package com.explorer.realtime.sessionhandling.waitingroom.event;

import com.explorer.realtime.global.redis.RedisService;
import com.explorer.realtime.global.session.SessionManager;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.exception.ExceedingCapacityException;
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
public class JoinWaitingRoom {

    private final RedisService redisService;
    private final SessionManager sessionManager;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public Mono<Void> process(String teamCode, UserInfo userInfo, Connection connection) {
        check(teamCode)
                .doOnError(Throwable::printStackTrace)
                .subscribe(
                        value -> {
                            createConnectionInfo(teamCode, String.valueOf(userInfo.getUserId()), connection);
                            channelRepository.save(teamCode, userInfo.getUserId()).subscribe();
                            userRepository.save(userInfo).subscribe();
                        },
                        error -> {}
        );
        return Mono.empty();
    }

    private void createConnectionInfo(String teamCode, String userId, Connection connection) {
        sessionManager.setConnection(userId, connection);
        redisService.saveUidToTeamCode(teamCode, userId, "waitingRoom").subscribe();
    }

    private Mono<Long> check(String teamCode) {
        return channelRepository.count(teamCode).flatMap(
                count -> {
                    if (count >= 6) {
                        return Mono.error(new ExceedingCapacityException());
                    }
                    return Mono.just(count);
                });
    }

}
