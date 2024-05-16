package com.explorer.realtime.staticdatahandling;

import com.explorer.realtime.staticdatahandling.event.SaveStaticDataToMongoDB;
import com.explorer.realtime.staticdatahandling.event.SaveStaticDataToRedis;
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
    private final SaveStaticDataToRedis saveStaticDataToRedis;

    public Mono<Void> staticDataHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "saveStaticDataToMongoDB":
                log.info("eventName : {}", eventName);
                saveStaticDataToMongoDB.process(json).subscribe();
                break;

            case "saveStaticDataTorRedis":
                log.info("eventName : {}", eventName);
                saveStaticDataToRedis.process().subscribe();
                break;
        }

        return Mono.empty();
    }


}
