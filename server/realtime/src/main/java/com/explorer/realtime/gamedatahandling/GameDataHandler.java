package com.explorer.realtime.gamedatahandling;

import com.explorer.realtime.gamedatahandling.item.event.GetItemFromMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameDataHandler {

    private final GetItemFromMap getItemFromMap;

    public Mono<Void> gameDataHandler(JSONObject json) {
        String event = json.getString("event");

        switch (event) {
        }

        return Mono.empty();
    }

}