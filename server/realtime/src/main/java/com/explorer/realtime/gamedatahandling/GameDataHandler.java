package com.explorer.realtime.gamedatahandling;

import com.explorer.realtime.gamedatahandling.item.ItemDataHandler;
import com.explorer.realtime.gamedatahandling.item.dto.ItemInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameDataHandler {

    private final ItemDataHandler itemDataHandler;

    public Mono<Void> gameDataHandler(JSONObject json) {
        String category = json.getString("item");

        switch (category) {
            case "item":
                log.info("category : {}", category);
                itemDataHandler.itemDataHandler(json);
                break;
        }

        return Mono.empty();
    }

}