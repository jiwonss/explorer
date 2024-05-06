package com.explorer.realtime.sessionhandling.waitingroom.event;

import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveWaitingRoom {

    private final ChannelRepository channelRepository;
    private final SessionManager sessionManager;
    private final UserRepository userRepository;
    private final Broadcasting broadcasting;

    private static final String eventName = "leaveWaitingRoom";

    public Mono<Void> process(JSONObject json) {
        String teamCode = json.getString("teamCode");
        Long userId = json.getLong("userId");
        boolean isLeader = json.getBoolean("isLeader");
        log.info("[process] teamCode : {}, userId : {}, isLeader : {}", teamCode, userId, isLeader);

        if (isLeader) {
            return deleteWaitingRoom(teamCode);
        } else {
            return leaveWaitingRoom(teamCode, userId);
        }
    }

    private Mono<Void> deleteWaitingRoom(String teamCode) {
        log.info("[deleteWaitingRoom] teamCode : {}", teamCode);

        return broadcasting.broadcasting(
                        teamCode,
                        MessageConverter.convert(Message.success(eventName, CastingType.BROADCASTING))
                )
                .then(channelRepository.findAllFields(teamCode)
                        .flatMap(value -> userRepository.delete(Long.valueOf(String.valueOf(value))))
                        .then(channelRepository.deleteAll(teamCode))
                ).then();
    }

    private Mono<Void> leaveWaitingRoom(String teamCode, Long userId) {
        log.info("[leaveWaitingRoom] teamCode : {}, userId : {}", teamCode, userId);

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        return broadcasting.broadcasting(
                        teamCode,
                        MessageConverter.convert(Message.success(eventName, CastingType.BROADCASTING, map))
                )
                .then(userRepository.delete(userId))
                .then(channelRepository.deleteByUserId(teamCode, userId))
                .then(Mono.fromRunnable(() -> sessionManager.removeConnection(userId)));
    }

}
