package com.explorer.realtime.staticdatahandling;

import com.explorer.realtime.staticdatahandling.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaticDataHandler {

    private final SaveStaticDataToMongoDB saveStaticDataToMongoDB;
    private final SaveNewPositionsToMongoDB saveNewPositionsToMongoDB;
    private final SaveStaticDataToRedis saveStaticDataToRedis;
    private final SavePositionToMongoDB savePositionToMongoDB;
    private final SavePositionsToMongoDB savePositionsToMongoDB;

    public Mono<Void> staticDataHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "saveStaticDataToMongoDB":
                log.info("eventName : {}", eventName);
                saveStaticDataToMongoDB.process(json).subscribe();
                break;

            case "saveStaticDataToRedis":
                log.info("eventName : {}", eventName);
                saveStaticDataToRedis.process().subscribe();
                break;

            case "savePositionToMongoDB":
                log.info("eventName : {}", eventName);
                savePositionToMongoDB.process(json).subscribe();
                break;

            case "savePositionsToMongoDB":
                log.info("eventName : {}", eventName);
                savePositionsToMongoDB.process(json).subscribe();
                break;

            case "saveNewPositionsToMongoDB":
                log.info("eventName : {}", eventName);
                saveNewPositionsToMongoDB.process(json).subscribe();
                break;
        }

        return Mono.empty();
    }


}
