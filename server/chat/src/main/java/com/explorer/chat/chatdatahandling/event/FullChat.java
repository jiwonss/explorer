package com.explorer.chat.chatdatahandling.event;

import com.explorer.chat.global.component.broadcasting.Broadcasting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FullChat {
    private final Broadcasting broadcasting;

    public Mono<Void> process(JSONObject json, Connection connection) {
        Long userId = json.getLong("userId");
        log.info("[process] userId : {}, connection : {}", userId, connection);

        return Mono.empty();
    }

}
