package com.explorer.chat.chathandling.event;

import com.explorer.chat.chathandling.exception.ChattingException;
import com.explorer.chat.chathandling.repository.UserRepository;
import com.explorer.chat.global.common.dto.Message;
import com.explorer.chat.global.common.enums.CastingType;
import com.explorer.chat.global.component.broadcasting.Broadcasting;
import com.explorer.chat.global.component.session.SessionManager;
import com.explorer.chat.global.util.MessageConverter;
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
public class LeaveChattingRoom {
    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final Broadcasting broadcasting;

    private static final String eventName = "leaveChattingRoom";

    public Mono<Void> process(JSONObject json, Connection connection) {
        String teamCode = json.getString("teamCode");
        Long userId = json.getLong("userId");
        log.info("[process] userId : {}, connection : {}", userId, connection);

        return userRepository.findAll(userId)
                .flatMap(map -> {
                    String nickname = (String) map.get("nickname");

                    return removeConnectionInfo(userId)
                            .then(Mono.fromRunnable(() -> {
                                Map<String, String> msg = new HashMap<>();
                                msg.put("nickname", nickname);
                                msg.put("content", nickname+"님이 채팅방을 퇴장했습니다.");

                                JSONObject jsonMessage = MessageConverter.convert(
                                        Message.success(eventName, CastingType.BROADCASTING, msg));

                                broadcasting.broadcasting(teamCode, jsonMessage).subscribe();
                            }));

                })
                .doOnError(ChattingException.class, error -> {
                    log.info("find userId error");
                })
                .then();
    }

    private Mono<Void> removeConnectionInfo(Long userId) {
        log.info("[removeConnectionInfo] userId : {}", userId);

        sessionManager.removeConnection(userId);
        return Mono.empty();
    }
}
