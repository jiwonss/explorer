package com.explorer.realtime.gamedatahandling.map;

import com.explorer.realtime.gamedatahandling.map.event.SavePositionToMongoDB;
import com.explorer.realtime.gamedatahandling.map.event.SavePositionsToMongoDB;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class MapHandler {

    private final SavePositionToMongoDB savePositionToMongoDB;
    private final SavePositionsToMongoDB savePositionsToMongoDB;

    public Mono<Void> mapHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "savePositionToMongoDB":
                log.info("eventName : {}", eventName);
                savePositionToMongoDB.process(json).subscribe();
                break;

            case "savePositionsToMongoDB":
                log.info("eventName : {}", eventName);
                savePositionsToMongoDB.process(json).subscribe();
                break;
        }

        return Mono.empty();
    }


}
