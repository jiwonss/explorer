package com.explorer.realtime.gamedatahandling.craft;

import com.explorer.realtime.gamedatahandling.craft.event.GetCraftingRecipeList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CraftHandler {

    private final GetCraftingRecipeList getCraftingRecipeList;

    public Mono<Void> craftHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "getCraftingRecipeList":
                log.info("eventName : {}", eventName);
                getCraftingRecipeList.process(json).subscribe();
                break;
        }
        return Mono.empty();
    }

}
