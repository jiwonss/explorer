package com.explorer.realtime.sessionhandling.waitingroom.event;

import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Multicasting;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.exception.WaitingRoomErrorCode;
import com.explorer.realtime.sessionhandling.waitingroom.exception.WaitingRoomException;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
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

    public Mono<Void> process(JSONObject json, Connection connection) {
        String teamCode = json.getString("teamCode").replace("\\u200b", "");
        UserInfo userInfo = UserInfo.ofJson(json);
        log.info("[process] teamCode : {}, userInfo : {}", teamCode, userInfo);

        return check(teamCode)
                .flatMap(count -> createConnectionInfo(teamCode, userInfo.getUserId(), connection)
                .then(userRepository.save(userInfo))
                .then(Mono.defer(() -> multicasting.multicasting(
                        teamCode,
                        String.valueOf(userInfo.getUserId()),
                        MessageConverter.convert(Message.success(eventName, CastingType.MULTICASTING, userInfo))
                )))
                .then(findAllUserInfoByTeamCode(teamCode, userInfo.getUserId()))
                .flatMap(userInfoList -> {
                    log.info("[process] userInfoList : {}", userInfoList);
                    return unicasting.unicasting(
                            teamCode,
                            userInfo.getUserId(),
                            MessageConverter.convert(Message.success(eventName, CastingType.UNICASTING, userInfoList))
                    );
                })
                .onErrorResume(WaitingRoomException.class, error -> {
                    switch (error.getErrorCode()) {
                        case EXIST_USER:
                            unicasting(
                                    connection,
                                    userInfo.getUserId(),
                                    MessageConverter.convert(Message.fail(eventName, CastingType.UNICASTING, String.valueOf(error.getErrorCode()), error.getMessage()))
                            ).subscribe();
                            return Mono.empty();
                        case EXCEEDING_CAPACITY:
                            unicasting.unicasting(
                                    teamCode,
                                    userInfo.getUserId(),
                                    MessageConverter.convert(Message.fail(eventName, CastingType.UNICASTING, String.valueOf(error.getErrorCode()), error.getMessage()))
                            ).subscribe();
                            return Mono.empty();
                        default:
                            return Mono.empty();
                    }
                }));
    }

    private Mono<Void> createConnectionInfo(String teamCode, Long userId, Connection connection) {
        log.info("[createConnectionInfo] teamCode : {}, userId : {}", teamCode, userId);

        return channelRepository.existByUserId(teamCode, userId)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new WaitingRoomException(WaitingRoomErrorCode.EXIST_USER));
                    } else {
                        sessionManager.setConnection(userId, connection);
                        return channelRepository.save(teamCode, userId, 0).then();
                    }
                });
    }

    private Mono<Long> check(String teamCode) {
        log.info("[check] teamCode : {}", teamCode);

        return channelRepository.count(teamCode)
                .flatMap(count -> {
                    if (count >= 6) {
                        return Mono.error(new WaitingRoomException(WaitingRoomErrorCode.EXCEEDING_CAPACITY));
                    }
                    return Mono.just(count);
                });
    }

    private Mono<List<UserInfo>> findAllUserInfoByTeamCode(String teamCode, Long userId) {
        log.info("[findAllUserInfoByTeamCode] teamCode : {}, userId : {}", teamCode, userId);

        return channelRepository.findAllFields(teamCode)
                .flatMap(id -> {
                    if (!Long.valueOf(String.valueOf(id)).equals(userId)) {
                        return userRepository.findAll(Long.valueOf(String.valueOf(id)))
                                .map(userInfo -> UserInfo.of(
                                        Long.valueOf(String.valueOf(id)),
                                        (String) userInfo.get("nickname"),
                                        Integer.parseInt(String.valueOf(userInfo.get("avatar")))
                                ));
                    } else {
                        return Mono.empty();
                    }
                }).collectList();
    }

}
