package com.explorer.realtime.sessionhandling.waitingroom.event;

import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Multicasting;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.exception.ExceedingCapacityException;
import com.explorer.realtime.sessionhandling.waitingroom.exception.WaitingRoomErrorCode;
import com.explorer.realtime.sessionhandling.waitingroom.exception.WaitingRoomException;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JoinWaitingRoom {

    private final ChannelRepository channelRepository;
    private final SessionManager sessionManager;
    private final UserRepository userRepository;
    private final Unicasting unicasting;
    private final Multicasting multicasting;

    private static final String eventName = "joinWaitingRoom";

    public Mono<Void> process(String teamCode, UserInfo userInfo, Connection connection) {
        log.info("joinWaitingRoom teamCode : {}", teamCode);
        check(teamCode)
                .doOnError(WaitingRoomException.class, error -> {
                    unicasting.unicasting(
                            teamCode,
                            String.valueOf(userInfo.getUserId()),
                            MessageConverter.convert(
                                    Message.fail(eventName, CastingType.UNICASTING, String.valueOf(error.getErrorCode()), error.getMessage()))
                    ).subscribe();
                })
                .flatMap(value -> {
                            createConnectionInfo(teamCode, userInfo.getUserId(), connection);
                            return userRepository.save(userInfo);
                        }
                )
                .doOnSuccess(value -> {
                    // 참가한 유저 정보
                    multicasting.multicasting(
                            teamCode,
                            String.valueOf(userInfo.getUserId()),
                            MessageConverter.convert(Message.success(eventName, CastingType.MULTICASTING, userInfo))
                    ).subscribe();

                    // 이미 참가 중인 유저 정보
                    findAllUserInfoByTeamcode(teamCode, userInfo.getUserId())
                            .subscribe(
                                    userInfoList -> {
                                        log.info("userInfoList : {}", userInfoList);
                                        unicasting.unicasting(
                                                teamCode,
                                                String.valueOf(userInfo.getUserId()),
                                                MessageConverter.convert(Message.success(eventName, CastingType.UNICASTING, userInfoList))
                                        ).subscribe();
                                    });
                }).subscribe();
        return Mono.empty();
    }

    private void createConnectionInfo(String teamCode, Long userId, Connection connection) {
        sessionManager.setConnection(String.valueOf(userId), connection);
        channelRepository.save(teamCode, userId, 0).subscribe();
    }

    private Mono<Long> check(String teamCode) {
        return channelRepository.count(teamCode).flatMap(count -> {
            if (count >= 6) {
                return Mono.error(new WaitingRoomException(WaitingRoomErrorCode.EXCEEDING_CAPACITY));
            }
            return Mono.just(count);
        });
    }

    private Mono<List<UserInfo>> findAllUserInfoByTeamcode(String teamcode, Long userId) {
        return channelRepository.findAllFields(teamcode)
                .flatMap(id -> {
                    if (!Long.valueOf(String.valueOf(id)).equals(userId)) {
                        log.info("teamcode : {} , userId : {}", teamcode, id);
                        return userRepository.findAll(Long.valueOf(String.valueOf(id)))
                                .map(userInfo ->
                                        UserInfo.of(
                                                Long.valueOf(String.valueOf(id)),
                                                (String) userInfo.get("nickname"),
                                                Integer.parseInt(String.valueOf(userInfo.get("avatar")))
                                        ));
                    } else {
                        return Mono.empty();
                    }
                })
                .collectList();
    }

}
