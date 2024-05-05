package com.explorer.realtime.gamedatahandling;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.event.InitialMapObject;
import com.explorer.realtime.gamedatahandling.farming.FarmingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameDataHandler {

    private final FarmingHandler farmingHandler;
    private final InitialMapObject initialMapObject;

    public Mono<Void> gameDataHandler(JSONObject json) {
        String category = json.getString("category");

        switch (category) {
            case "farming":
                log.info("category : {}", category);
                farmingHandler.farmingHandler(json);
                break;

            case "mapObject":
                log.info("category : {}", category);
                String channelId = json.getString("channel");
                initialMapObject.initialMapObject(channelId).subscribe();
        }

        return Mono.empty();
    }

}