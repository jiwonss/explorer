package com.explorer.realtime.sessionhandling.waitingroom.event;

import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.redis.RedisService;
import com.explorer.realtime.global.component.session.SessionManager;
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
public class LeaveWaitingRoom {

    private final RedisService redisService;
    private final SessionManager sessionManager;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final Broadcasting broadcasting;

    public Mono<Void> process(String teamCode, UserInfo userInfo) {
        if (userInfo.isLeader()) {
            delete(teamCode);
            channelRepository.find(teamCode).subscribe(
                    userId -> {
                        sessionManager.getConnection(userId).dispose();
                    });
            channelRepository.delete(teamCode).subscribe();
        } else {
            leave(teamCode, userInfo.getUserId());
            sessionManager.getConnection(String.valueOf(userInfo.getUserId())).dispose();
        }
        return Mono.empty();
    }

    private void delete(String teamCode) {
        channelRepository.find(teamCode).subscribe(
                userId -> {
                    userRepository.delete(Long.valueOf(userId)).subscribe();
                });

        Map<String, String> map = new HashMap<>();
        map.put("message", "방이 삭제되었습니다.");

        broadcasting.broadcasting(teamCode, MessageConverter.convert(Message.success(map))).subscribe();

        redisService.deleteFromTeamCode(teamCode).subscribe();
    }

    private void leave(String teamCode, Long userId) {
        channelRepository.leave(teamCode, userId).subscribe();
        userRepository.delete(userId).subscribe();
        redisService.deleteUidFromTeamCode(teamCode, String.valueOf(userId)).subscribe();
        sessionManager.removeConnection(String.valueOf(userId));

        Map<String, String> map = new HashMap<>();
        map.put("userId", String.valueOf(userId));

        broadcasting.broadcasting(teamCode, MessageConverter.convert(Message.success(map))).subscribe();
    }

}
