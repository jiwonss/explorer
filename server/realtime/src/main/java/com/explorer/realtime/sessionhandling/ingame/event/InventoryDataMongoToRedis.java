package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository.InventoryRepository;
import com.explorer.realtime.global.mongo.repository.InventoryDataMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryDataMongoToRedis {

    private final InventoryDataMongoRepository inventoryDataMongoRepository;
    private final InventoryRepository inventoryRepository;

    public Mono<Void> process(String channelId, Long userId) {
        log.info("inventory mongo data");
        findMongoData(channelId, userId).subscribe();
        return Mono.empty();
    }

    private Mono<Void> findMongoData(String channelId, Long userId) {
        return inventoryDataMongoRepository.findByChannelIdAndUserId(channelId, userId)
                .flatMap(inventoryData -> {
                    String key = "inventoryData:" + channelId + ":" + userId;
                    return Mono.fromRunnable(() -> {
                        inventoryData.getInventoryData().forEach(item -> {
                            String field = String.valueOf(item.getInventoryIdx());
                            String value = item.getItemCategory() + ":" + item.getItemId() + ":" + item.getItemCnt() + ":" + item.getIsFull();
                            inventoryRepository.put(key, field, value).subscribe();
                        });
                    });
                }).then();
    }
}

