package com.explorer.realtime.sessionhandling.ingame;

import com.explorer.realtime.sessionhandling.waitingroom.WaitingRoomSessionHandler;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class InGameSessionHandler {

    private static final Logger log = LoggerFactory.getLogger(WaitingRoomSessionHandler.class);

    private final StartGame startGame;
//    private final RestartGame restartGame;

    public Mono<Void> inGameHandler(JSONObject json) {
        String event = json.getString("event");

        switch (event) {
            case "startGame" :
                log.info("start game");
                String teamCode = json.getString("teamCode");
                String channelName = json.getString("channelName");
                startGame.process(teamCode, channelName);
                break;

            case "restartGame":
                log.info("restart game");
                break;
        }

        return Mono.empty();
    }

}
