package com.explorer.realtime.sessionhandling.ingame;

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
        String event = json.getString("event");

        switch (event) {
            case "startGame" :
                log.info("start game");
                String teamCode = json.getString("channel");
                String channelName = json.getString("channelName");
                startGame.process(teamCode, channelName);
                break;

            case "restartGame":
                log.info("restart game");
                String channel = json.getString("channel");
                restartGame.process(channel, UserInfo.ofUserIdAndNicknameAndAvatar(json), connection);
                break;

            case "endGame":
                log.info("end game");
                channel = json.getString("channel");
                endGame.process(channel, UserInfo.ofUserIdAndNicknameAndAvatar(json));
                break;
        }

        return Mono.empty();
    }

}
