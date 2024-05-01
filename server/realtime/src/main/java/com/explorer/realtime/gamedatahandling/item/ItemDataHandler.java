package com.explorer.realtime.gamedatahandling.item;

import com.explorer.realtime.gamedatahandling.item.dto.ConnectionInfo;
import com.explorer.realtime.gamedatahandling.item.dto.ItemInfo;
import com.explorer.realtime.gamedatahandling.item.event.GetItemFromMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemDataHandler {

    private final GetItemFromMap getItemFromMap;

    public Mono<Void> itemDataHandler(JSONObject json) {
        String category = json.getString("category");

        switch (category) {
            case "getItemFromMap":
                log.info("event : {}", category);
                getItemFromMap.process(ConnectionInfo.of(json), ItemInfo.of(json));
                break;
        }

        return Mono.empty();
    }

}
