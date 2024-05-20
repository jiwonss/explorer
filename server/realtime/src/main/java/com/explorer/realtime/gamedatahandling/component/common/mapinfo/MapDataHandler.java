package com.explorer.realtime.gamedatahandling.component.common.mapinfo;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.event.AsteroidMapObject;
import com.explorer.realtime.gamedatahandling.component.common.mapinfo.event.GetMapData;
import com.explorer.realtime.gamedatahandling.component.common.mapinfo.event.ReturnMainMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class MapDataHandler {

    private final AsteroidMapObject asteroidMapObject;
    private final ReturnMainMap returnMainMap;
    private final GetMapData getMapData;

    public Mono<Void> mapDataHandler(JSONObject json) {
        String eventName = json.getString("eventName");

        switch (eventName) {
            case "exploration":
                String channelId = json.getString("channelId");
                Integer mapId = json.getInt("mapId");
                if (mapId == 4) {
                    asteroidMapObject.asteroidMapObject(channelId).subscribe();
                } else {
                    getMapData.getMapData(channelId, mapId).subscribe();
                }
                break;

            case "returnMainMap":
                log.info("returnMainMap : {}", eventName);
                returnMainMap.returnMainMap(json).subscribe();
                break;
        }
        return Mono.empty();
    }
}
