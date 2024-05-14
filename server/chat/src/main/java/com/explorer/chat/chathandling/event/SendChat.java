package com.explorer.chat.chathandling.event;

import com.explorer.chat.chathandling.repository.UserRepository;
import com.explorer.chat.global.common.dto.Message;
import com.explorer.chat.global.common.enums.CastingType;
import com.explorer.chat.global.component.broadcasting.Broadcasting;
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
public class SendChat {
    private final UserRepository userRepository;
    private final Broadcasting broadcasting;

    private static final String eventName = "sendChat";

    public Mono<Void> process(JSONObject json, Connection connection) {
        String teamCode = json.getString("teamCode");
        Long userId = json.getLong("userId");
        String content = json.getString("content");
        log.info("[process] userId : {}, connection : {}", userId, connection);

        return userRepository.findAll(userId)
                .flatMap(map -> {
                    String nickname = (String) map.get("nickname");
                    JSONObject messageDataBody = new JSONObject();
                    messageDataBody.put("nickname", nickname);
                    messageDataBody.put("content", content);

                    JSONObject jsonMessage = MessageConverter.convert(
                            Message.success(eventName, CastingType.BROADCASTING, messageDataBody));

                    return broadcasting.broadcasting(teamCode, jsonMessage);
                })
                .then();
    }

}
