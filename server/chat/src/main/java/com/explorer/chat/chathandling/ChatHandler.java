package com.explorer.chat.chathandling;

import com.explorer.chat.chathandling.event.JoinChattingRoom;
import com.explorer.chat.chathandling.event.LeaveChattingRoom;
import com.explorer.chat.chathandling.event.SendChat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

@Component
@RequiredArgsConstructor
public class ChatHandler {
    private static final Logger log = LoggerFactory.getLogger(ChatHandler.class);

    private final JoinChattingRoom joinChattingRoom;
    private final SendChat sendChat;
    private final LeaveChattingRoom leaveChattingRoom;

    public Mono<Void> chatDataHandler(JSONObject json, Connection connection) {
        String eventName = json.getString("eventName");

        switch(eventName) {
            case "joinChattingRoom" :
                joinChattingRoom.process(json, connection).subscribe();
                log.info("event : {}", eventName);
                break;

            case "sendChat":
                log.info("event : {}", eventName);
                sendChat.process(json, connection).subscribe();
                break;

            case "leaveChattingRoom" :
                log.info("event : {}", eventName);
                leaveChattingRoom.process(json, connection).subscribe();
                break;
        }

        return Mono.empty();
    }
}
