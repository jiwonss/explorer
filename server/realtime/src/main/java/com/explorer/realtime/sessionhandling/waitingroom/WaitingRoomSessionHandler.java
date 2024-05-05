package com.explorer.realtime.sessionhandling.waitingroom;

import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.event.CreateWaitingRoom;
import com.explorer.realtime.sessionhandling.waitingroom.event.BroadcastPosition;
import com.explorer.realtime.sessionhandling.waitingroom.event.JoinWaitingRoom;
import com.explorer.realtime.sessionhandling.waitingroom.event.LeaveWaitingRoom;
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

    public Mono<Void> waitingRoomHandler(JSONObject json, Connection connection) {
        String eventName = json.getString("eventName");

        switch(eventName) {
            case "createWaitingRoom" :
                log.info("create waiting room");
                createWaitingRoom.process(UserInfo.ofJson(json), connection);
                break;

            case "joinWaitingRoom":
                log.info("join waiting room");
                String joinTeamCode = json.getString("teamCode");
                joinWaitingRoom.process(joinTeamCode, UserInfo.ofJson(json), connection);
                break;

            case "leaveWaitingRoom":
                log.info("leave waiting room");
                String leaveTeamCode = json.getString("teamCode");
                boolean isLeader = json.getBoolean("isLeader");
                leaveWaitingRoom.process(leaveTeamCode, UserInfo.ofJson(json), isLeader);
                break;

            case "broadcastPosition":
                log.info("broadcast position");
                String teamCode = json.getString("teamCode");
                Long userId = json.getLong("userId");
                String position = json.getString("position");
                broadcastPosition.process(teamCode, userId, position);
                break;
        }

        return Mono.empty();
    }

}
