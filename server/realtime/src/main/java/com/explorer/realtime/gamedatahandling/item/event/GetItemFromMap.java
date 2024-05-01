package com.explorer.realtime.gamedatahandling.item.event;

import com.explorer.realtime.gamedatahandling.item.dto.ConnectionInfo;
import com.explorer.realtime.gamedatahandling.item.dto.ItemInfo;
import com.explorer.realtime.gamedatahandling.item.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetItemFromMap {

    private final InventoryRepository inventoryRepository;

    public Mono<Void> process(ConnectionInfo connectionInfo, ItemInfo itemInfo) {

        return Mono.empty();
    }

}
