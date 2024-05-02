package com.explorer.realtime.sessionhandling.waitingroom.event;

import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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

    public Mono<Void> process(String teamCode, UserInfo userInfo) {
        if (userInfo.isLeader()) {
            delete(teamCode);
            channelRepository.findAll(teamCode)
                    .flatMapMany(map -> Flux.fromIterable(map.keySet()))
                    .flatMap(key -> Mono.fromRunnable(() -> sessionManager.getConnection((String) key).dispose()))
                    .subscribe();
            channelRepository.deleteAll(teamCode).subscribe();
        } else {
            leave(teamCode, userInfo.getUserId());
            sessionManager.getConnection(String.valueOf(userInfo.getUserId())).dispose();
        }
        return Mono.empty();
    }

    private void delete(String teamCode) {
        channelRepository.findAll(teamCode).subscribe(
                value -> {
                    userRepository.delete((Long) value.get(0)).subscribe();
                });

        Map<String, String> map = new HashMap<>();
        map.put("message", "방이 삭제되었습니다.");

        broadcasting.broadcasting(
                teamCode,
                MessageConverter.convert(Message.success("leaveWaitingRoom", CastingType.BROADCASTING, map))
        ).subscribe();
    }

    private void leave(String teamCode, Long userId) {
        userRepository.delete(userId).subscribe();
        channelRepository.deleteByUserId(teamCode, userId).subscribe();
        sessionManager.removeConnection(String.valueOf(userId));

        Map<String, String> map = new HashMap<>();
        map.put("userId", String.valueOf(userId));

        broadcasting.broadcasting(
                teamCode,
                MessageConverter.convert(Message.success("leaveWaitingRoom", CastingType.BROADCASTING, map))
        ).subscribe();
    }

}
