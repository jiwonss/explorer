package com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.event;

import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository.InventoryInfoRepository;
import com.explorer.realtime.gamedatahandling.component.personal.playerInfo.repository.PlayerInfoRepository;
import com.explorer.realtime.gamedatahandling.farming.dto.InventoryInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitializeInventory {

    private final PlayerInfoRepository playerInfoRepository;
    private final InventoryInfoRepository inventoryInfoRepository;

    public Mono<Void> process(String channelId, Long userId) {
        log.info("[process] channelId : {}, userId : {}", channelId, userId);

        return getInventoryCntByUserId(channelId, userId)
                .flatMap(inventoryCnt -> initInventory(channelId, userId, inventoryCnt));
    }

    private Mono<Integer> getInventoryCntByUserId(String channelId, Long userId) {
        log.info("[getInventoryCntByUserId] channelId : {}, userId : {}", channelId, userId);

        return playerInfoRepository.findInventoryCnt(channelId, userId)
                .map(map -> Integer.parseInt(String.valueOf(map)));
    }

    private Mono<Void> initInventory(String channelId, Long userId, int inventoryCnt) {
        log.info("[initInventory] channelId : {}, userId : {}, inventoryCnt : {}", channelId, userId, inventoryCnt);

        return inventoryInfoRepository.init(channelId, userId, inventoryCnt).then();
    }


}
