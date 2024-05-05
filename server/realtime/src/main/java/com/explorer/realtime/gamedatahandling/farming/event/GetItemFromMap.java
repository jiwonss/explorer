package com.explorer.realtime.gamedatahandling.farming.event;

import com.explorer.realtime.gamedatahandling.farming.dto.ConnectionInfo;
import com.explorer.realtime.gamedatahandling.farming.dto.ItemInfo;
import com.explorer.realtime.gamedatahandling.farming.repository.InventoryRepository;
import com.explorer.realtime.gamedatahandling.farming.repository.MapInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetItemFromMap {

    private final MapInfoRepository mapInfoRepository;
    private final InventoryRepository inventoryRepository;

    public Mono<Void> process(ConnectionInfo connectionInfo, String position) {
        log.info("getItemFromMap process");

        String channelId = connectionInfo.getChannelId();
        Long userId = connectionInfo.getUserId();
        int mapId = connectionInfo.getMapId();

        mapInfoRepository.save(channelId, mapId, position, "category", 10, 1).subscribe();

        AtomicReference<ItemInfo> itemInfo = new AtomicReference<>();;
        mapInfoRepository.find(channelId, mapId, position).subscribe(
                value -> {
                    log.info("map : {}", value);
                    itemInfo.set(ItemInfo.of((String) value, position));
                    log.info("itemInfo : {}", itemInfo.get().toString());

                    int idx = 0;
                    while (idx < 8) {

                        idx++;
                    }
                },
                error -> {
                    log.error("Error occurred: {}", error);
                }
        );
        return Mono.empty();
    }

}
