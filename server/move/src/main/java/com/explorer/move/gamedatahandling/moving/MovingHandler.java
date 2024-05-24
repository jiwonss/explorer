package com.explorer.move.gamedatahandling.moving;

import com.explorer.move.gamedatahandling.moving.event.Moving;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovingHandler {

    private final Moving moving;

    public Mono<Void> movingHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "move":
                log.info("eventName : {}", eventName);
                moving.process(json).subscribe();
                break;
        }

        return Mono.empty();
    }

}
