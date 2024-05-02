package com.explorer.realtime.sessionhandling.waitingroom.event;

import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.exception.ExceedingCapacityException;
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

    private final ChannelRepository channelRepository;
    private final SessionManager sessionManager;
    private final UserRepository userRepository;
    private final Unicasting unicasting;

    public Mono<Void> process(String teamCode, UserInfo userInfo, Connection connection) {
        check(teamCode)
                .doOnError(Throwable::printStackTrace)
                .subscribe(
                        value -> {
                            createConnectionInfo(teamCode, userInfo.getUserId(), connection);
                            userRepository.save(userInfo).subscribe();
                            unicasting.unicasting(
                                    teamCode,
                                    String.valueOf(userInfo.getUserId()),
                                    MessageConverter.convert(Message.success("joinWaitingRoom", CastingType.UNICASTING))
                            );
                        },
                        error -> {}
        );
        return Mono.empty();
    }

    private void createConnectionInfo(String teamCode, Long userId, Connection connection) {
        sessionManager.setConnection(String.valueOf(userId), connection);
        channelRepository.save(teamCode, userId, 0).subscribe();
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
