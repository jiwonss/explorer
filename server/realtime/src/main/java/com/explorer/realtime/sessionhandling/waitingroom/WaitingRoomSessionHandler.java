package com.explorer.realtime.sessionhandling.waitingroom;

import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.event.*;
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
    private final JoinWaitingRoom joinWaitingRoom;
    private final LeaveWaitingRoom leaveWaitingRoom;
    private final BroadcastPosition broadcastPosition;
    private final GetWaitingRoomHeadcount getWaitingRoomHeadcount;

    public Mono<Void> waitingRoomSessionHandler(JSONObject json, Connection connection) {
        String eventName = json.getString("eventName");

        switch(eventName) {
            case "createWaitingRoom" :
                log.info("event : {}", eventName);
                createWaitingRoom.process(json, connection).subscribe();
                break;

            case "joinWaitingRoom":
                log.info("event : {}", eventName);
                joinWaitingRoom.process(json, connection).subscribe();
                break;

            case "leaveWaitingRoom":
                log.info("event : {}", eventName);
                leaveWaitingRoom.process(json, connection).subscribe();
                break;

            case "broadcastPosition":
                log.info("event : {}", eventName);
                broadcastPosition.process(json).subscribe();
                break;

            case "getWaitingRoomHeadcount":
                log.info("event : {}", eventName);
                getWaitingRoomHeadcount.process(json).subscribe();
                break;
        }

        return Mono.empty();
    }

}
