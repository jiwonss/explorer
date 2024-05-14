package com.explorer.chat.chatdatahandling;

import com.explorer.chat.chatdatahandling.event.FullChat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatDataHandler {
    private final FullChat fullChat;

    public Mono<Void> chatDataHandler(JSONObject json, Connection connection) {
        fullChat.process(json, connection).subscribe();

        return Mono.empty();
    }
}
