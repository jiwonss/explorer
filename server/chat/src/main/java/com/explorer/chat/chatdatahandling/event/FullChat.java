package com.explorer.chat.chatdatahandling.event;

import com.explorer.chat.chatdatahandling.repository.UserRepository;
import com.explorer.chat.global.common.dto.Message;
import com.explorer.chat.global.common.enums.CastingType;
import com.explorer.chat.global.component.broadcasting.Broadcasting;
import com.explorer.chat.global.util.MessageConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class FullChat {
    private final UserRepository userRepository;
    private final Broadcasting broadcasting;
    private final ObjectMapper objectMapper;

    private static final String eventName = "chat";

    public Mono<Void> process(JSONObject json, Connection connection) {
        Long userId = json.getLong("userId");
        String content = json.getString("content");
        String teamCode = json.getString("teamCode");
        log.info("[process] userId : {}, connection : {}", userId, connection);

        return userRepository.findAll(userId)
                .flatMap(map -> {
                    String nickname = (String) map.get("nickname");
                    Map<String, Object> messageDataBody = new HashMap<>();
                    messageDataBody.put("nickname", nickname);
                    messageDataBody.put("content", content);

                    try {
                        String jsonMessage = objectMapper.writeValueAsString(
                                Message.success(eventName, CastingType.BROADCASTING, messageDataBody)
                        );
                        return broadcasting.broadcasting(teamCode, jsonMessage);
                    } catch (JsonProcessingException e) {
                        log.error("JSON writing error", e);
                        return Mono.error(e);
                    }
                })
                .then();
    }

}
