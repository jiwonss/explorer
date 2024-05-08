package com.explorer.realtime.sessionhandling.ingame;

import com.explorer.realtime.sessionhandling.ingame.event.EndGame;
import com.explorer.realtime.sessionhandling.ingame.event.RestartGame;
import com.explorer.realtime.sessionhandling.ingame.event.StartGame;
import com.explorer.realtime.sessionhandling.waitingroom.WaitingRoomSessionHandler;
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
public class InGameSessionHandler {

    private static final Logger log = LoggerFactory.getLogger(WaitingRoomSessionHandler.class);

    private final StartGame startGame;
    private final RestartGame restartGame;
    private final EndGame endGame;

    public Mono<Void> inGameHandler(JSONObject json, Connection connection) {
        String eventName = json.getString("eventName");
        String channel = json.optString("channel");

        switch (eventName) {
            case "startGame" :
                log.info("start game");
                String teamCode = json.getString("channelId");
                String channelName = json.getString("channelName");
                startGame.process(teamCode, channelName);
                break;

            case "restartGame":
                log.info("restart game");
                return restartGame.process(channel, UserInfo.ofJson(json), connection);

            case "endGame":
                log.info("end game");
                return endGame.process(channel, UserInfo.ofJson(json));
        }

        return Mono.empty();
    }

}
