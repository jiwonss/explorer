package com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.event;

import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository.InventoryInfoRepository;
import com.explorer.realtime.global.redis.ChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SetInitialInventory {

    private final ChannelRepository channelRepository;
    private final InventoryInfoRepository inventoryInfoRepository;

    public Mono<Void> process(String channelId, int inventoryCnt) {
        log.info("[process] channelId : {}", channelId);

        return saveAllInitInventoryInfoByChannelId(channelId, inventoryCnt);
    }

    private Mono<Void> saveAllInitInventoryInfoByChannelId(String channelId, int inventoryCnt) {
        log.info("[saveAllInitInventoryInfoByChannelId] channelId : {}", channelId);

        return channelRepository.findAllFields(channelId)
                .flatMap(userId -> {
                    return inventoryInfoRepository.init(channelId, Long.valueOf(String.valueOf(userId)), inventoryCnt);
                })
                .then();
    }

}
