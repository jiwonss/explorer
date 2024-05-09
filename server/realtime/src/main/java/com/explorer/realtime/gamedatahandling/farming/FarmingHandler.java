package com.explorer.realtime.gamedatahandling.farming;

import com.explorer.realtime.gamedatahandling.farming.event.Farm;
import com.explorer.realtime.gamedatahandling.farming.event.GetItemFromMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class FarmingHandler {

    private final GetItemFromMap getItemFromMap;
    private final Farm farm;

    public Mono<Void> farmingHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "getItemFromMap":
                log.info("eventName : {}", eventName);
                getItemFromMap.process(json).subscribe();
                break;
            case "farm":
                log.info("eventName : {}", eventName);
                farm.process(json).subscribe();
                break;
        }

        return Mono.empty();
    }

}
