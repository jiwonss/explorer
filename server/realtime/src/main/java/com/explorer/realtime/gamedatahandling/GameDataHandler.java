package com.explorer.realtime.gamedatahandling;

import com.explorer.realtime.gamedatahandling.component.common.boxinfo.InstallHandler;
import com.explorer.realtime.gamedatahandling.component.common.mapinfo.MapDataHandler;
import com.explorer.realtime.gamedatahandling.farming.FarmingHandler;
import com.explorer.realtime.gamedatahandling.inventory.InventoryHandler;
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
    private final MapDataHandler mapDataHandler;
    private final LaboratoryHandler laboratoryHandler;
    private final InstallHandler installHandler;
    private final InventoryHandler inventoryHandler;

    public Mono<Void> gameDataHandler(JSONObject json) {
        String category = json.getString("category");

        switch (category) {
            case "farming":
                log.info("category : {}", category);
                farmingHandler.farmingHandler(json);
                break;

            case "map":
                log.info("category : {}, :{}", category, json);
                mapDataHandler.mapDataHandler(json);
                break;

            case "moving":
                log.info("category : {}", category);
                movingHandler.movingHandler(json);
                break;

            case "laboratory":
                log.info("category : {}", category);
                laboratoryHandler.laboratoryHandler(json);
                break;

            case "install":
                log.info("category : {}", category);
                installHandler.boxHandler(json);
                break;

            case "inventory":
                log.info("category : {}", category);
                inventoryHandler.inventoryHandler(json);
                break;
        }

        return Mono.empty();
    }

}
