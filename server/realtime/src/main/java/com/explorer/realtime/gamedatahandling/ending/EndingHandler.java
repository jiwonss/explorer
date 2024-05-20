package com.explorer.realtime.gamedatahandling.ending;

import com.explorer.realtime.gamedatahandling.ending.event.HandleGameEnding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class EndingHandler {

    private final HandleGameEnding handleGameEnding;

    public Mono<Void> endingHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "handleGameEnding":
                log.info("eventName : {}", eventName);
                handleGameEnding.process(json).subscribe();
                break;
        }

        return Mono.empty();
    }

}
