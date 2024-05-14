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

@Slf4j
@Service
@RequiredArgsConstructor
public class JoinChattingRoom {
    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final Broadcasting broadcasting;

    private static final String eventName = "joinChattingRoom";

    public Mono<Void> process(JSONObject json, Connection connection) {
        String teamCode = json.getString("teamCode");
        Long userId = json.getLong("userId");
        log.info("[process] userId : {}, connection : {}", userId, connection);

        return userRepository.findAll(userId)
                .flatMap(map -> {
                    return createConnectionInfo(teamCode, userId, connection)
                            .then(Mono.fromRunnable(() -> {
                                String nickname = (String) map.get("nickname");
                                JSONObject messageDataBody = new JSONObject();
                                messageDataBody.put("nickname", nickname);
                                messageDataBody.put("content", nickname+"님이 채팅방에 입장했습니다.");

                                JSONObject jsonMessage = MessageConverter.convert(
                                        Message.success(eventName, CastingType.BROADCASTING, messageDataBody));

                                broadcasting.broadcasting(teamCode, jsonMessage).subscribe();
                            }));

                })
                .doOnError(ChattingException.class, error -> {
                    log.info("find userId error");
                })
                .then();
    }

    private Mono<Void> createConnectionInfo(String teamCode, Long userId, Connection connection) {
        log.info("[createConnectionInfo] teamCode : {}, userId : {}", teamCode, userId);

        sessionManager.setConnection(userId, connection);
        return Mono.empty();
    }
}
