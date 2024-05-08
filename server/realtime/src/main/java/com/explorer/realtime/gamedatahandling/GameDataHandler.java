package com.explorer.realtime.gamedatahandling;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.event.InitializeMapObject;
import com.explorer.realtime.gamedatahandling.farming.FarmingHandler;
import com.explorer.realtime.gamedatahandling.laboratory.LaboratoryHandler;
import com.explorer.realtime.gamedatahandling.moving.MovingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameDataHandler {

    private final MovingHandler movingHandler;
    private final FarmingHandler farmingHandler;
    private final InitializeMapObject initializeMapObject;
    private final LaboratoryHandler laboratoryHandler;

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
                initializeMapObject.initializeMapObject(channelId).subscribe();

            case "moving":
                log.info("category : {}", category);
                movingHandler.movingHandler(json);
                break;

            case "laboratory":
                log.info("category : {}", category);
                laboratoryHandler.laboratoryHandler(json);
                break;
        }

        return Mono.empty();
    }

}
