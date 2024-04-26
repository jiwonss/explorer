package com.explorer.realtime.sessionhandling.ingame;


import com.explorer.realtime.serverManaging.RequestHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class IngameSessionHandler {

    private static final Logger log = LoggerFactory.getLogger(IngameSessionHandler.class);

    public Mono<Void> ingameHandler(JSONObject json) {
        String event = json.getString("event");

        switch(event) {
            case "startGame" :
                log.info("start game");
                break;

            case "restartGame":
                log.info("restart game");
                break;

            case "endGame":
                log.info("endGame");
                break;
        }

        return Mono.empty();
    }

}
