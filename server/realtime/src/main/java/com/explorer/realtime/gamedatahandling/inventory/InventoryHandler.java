package com.explorer.realtime.gamedatahandling.inventory;

import com.explorer.realtime.gamedatahandling.inventory.event.MoveItemInInventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryHandler {

    private final MoveItemInInventory moveItemInInventory;

    public Mono<Void> inventoryHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "moveItemInInventory":
                log.info("eventName : {}", eventName);
                moveItemInInventory.process(json).subscribe();
                break;
        }

        return Mono.empty();
    }

}
