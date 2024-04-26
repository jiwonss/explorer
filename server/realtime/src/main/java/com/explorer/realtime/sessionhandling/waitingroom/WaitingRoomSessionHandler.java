package com.explorer.realtime.sessionhandling.waitingroom;

import com.explorer.realtime.sessionhandling.ingame.IngameSessionHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class WaitingRoomSessionHandler {

    private static final Logger log = LoggerFactory.getLogger(IngameSessionHandler.class);

    public Mono<Void> waitingRoomHandler(JSONObject json) {
        String event = json.getString("event");
        String nickname = json.getString("nickname");
        int avatar = json.getInt("avatar");

        switch(event) {
            case "createWaitingRoom" :
                log.info("create waiting room");
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
