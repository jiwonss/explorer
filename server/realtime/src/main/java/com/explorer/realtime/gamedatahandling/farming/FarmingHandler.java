package com.explorer.realtime.gamedatahandling.farming;

import com.explorer.realtime.gamedatahandling.farming.dto.ConnectionInfo;
import com.explorer.realtime.gamedatahandling.farming.dto.ItemInfo;
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

    public Mono<Void> farmingHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "getItemFromMap":
                log.info("eventName : {}", eventName);
                getItemFromMap.process(ConnectionInfo.of(json), ItemInfo.of(json));
                break;
        }

        return Mono.empty();
    }

}
