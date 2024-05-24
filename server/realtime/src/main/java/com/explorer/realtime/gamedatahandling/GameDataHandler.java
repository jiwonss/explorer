package com.explorer.realtime.gamedatahandling;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.MapDataHandler;
import com.explorer.realtime.gamedatahandling.craft.CraftHandler;
import com.explorer.realtime.gamedatahandling.ending.EndingHandler;
import com.explorer.realtime.gamedatahandling.farming.FarmingHandler;
import com.explorer.realtime.gamedatahandling.inventory.InventoryHandler;
import com.explorer.realtime.gamedatahandling.laboratory.LaboratoryHandler;
import com.explorer.realtime.gamedatahandling.moving.MovingHandler;
import com.explorer.realtime.gamedatahandling.tool.ToolHandler;
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
    private final InventoryHandler inventoryHandler;
    private final ToolHandler toolHandler;
    private final CraftHandler craftHandler;
    private final EndingHandler endingHandler;

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
                laboratoryHandler.laboratoryHandler(json).subscribe();
                break;

            case "inventory":
                log.info("category : {}", category);
                inventoryHandler.inventoryHandler(json);
                break;

            case "tool":
                log.info("category : {}", category);
                toolHandler.toolHandler(json);
                break;

            case "craft":
                log.info("category : {}", category);
                craftHandler.craftHandler(json);
                break;

            case "ending":
                log.info("category : {}", category);
                endingHandler.endingHandler(json);
                break;
        }

        return Mono.empty();
    }

}
