package com.explorer.realtime.sessionhandling.waitingroom.event;

import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.component.teamcode.TeamCodeGenerator;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateWaitingRoom {

    private final ChannelRepository channelRepository;
    private final SessionManager sessionManager;
    private final TeamCodeGenerator teamCodeGenerator;
    private final UserRepository userRepository;
    private final Unicasting unicasting;

    private final static String eventName = "createWaitingRoom";

    public Mono<Void> process(JSONObject json, Connection connection) {
        UserInfo userInfo = UserInfo.ofJson(json);
        log.info("[process] userInfo : {}, connection : {}", userInfo, connection);

        return createTeamCode()
                .flatMap(teamCode -> {
                    log.info("[process] teamCode : {}", teamCode);
                    return createConnectionInfo(teamCode, userInfo.getUserId(), connection)
                            .then(userRepository.save(userInfo))
                            .then(Mono.fromRunnable(() -> {
                                Map<String, String> map = new HashMap<>();
                                map.put("teamCode", teamCode);
                                unicasting.unicasting(
                                        teamCode,
                                        String.valueOf(userInfo.getUserId()),
                                        MessageConverter.convert(Message.success(eventName, CastingType.UNICASTING, map))
                                ).subscribe();
                            }));
                })
                .doOnError(WaitingRoomException.class, error -> {
                    log.info("[createTeamCode] code : {}, message : {}", error.getErrorCode(), error.getMessage());
                })
                .then();
    }

    private Mono<Void> createConnectionInfo(String teamCode, Long userId, Connection connection) {
        log.info("[createConnectionInfo] teamCode : {}, userId : {}", teamCode, userId);

        sessionManager.setConnection(String.valueOf(userId), connection);
        return channelRepository.save(teamCode, userId, 0).then();
    }

    private Mono<String> createTeamCode() {
        log.info("[createTeamCode] message : createTeamCode");

        return teamCodeGenerator.getCode()
                .switchIfEmpty(Mono.error(new WaitingRoomException(WaitingRoomErrorCode.FAILED_GENERATE_TEAMCODE)))
                .doOnSuccess(code -> {
                    log.info("[createTeamCode] message : success, teamCode : {}", code);
                })
                .doOnError(error -> {
                    log.info("[createTeamCode] message : failed generate teamCode");
                });
    }

}
