package com.explorer.realtime.sessionhandling.waitingroom;

import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

@Component
@RequiredArgsConstructor
public class WaitingRoomSessionHandler {

    private static final Logger log = LoggerFactory.getLogger(WaitingRoomSessionHandler.class);

    private final CreateWaitingRoom createWaitingRoom;

    public Mono<Void> waitingRoomHandler(JSONObject json, Connection connection) {
        String event = json.getString("event");
        UserInfo userInfo = UserInfo.of(json);

        switch(event) {
            case "createWaitingRoom" :
                log.info("create waiting room");
                createWaitingRoom.process(userInfo, connection);
                break;

            case "joinWaitingRoom":
                log.info("join waiting room");
                break;

            case "leaveWaitingRoom":
                log.info("leave waiting room");
                break;
        }

        return Mono.empty();
    }

}
