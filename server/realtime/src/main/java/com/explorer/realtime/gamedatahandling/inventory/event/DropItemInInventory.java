package com.explorer.realtime.gamedatahandling.inventory.event;

import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.dto.InventoryInfo;
import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository.InventoryRepository;
import com.explorer.realtime.gamedatahandling.farming.repository.MapInfoRepository;
import com.explorer.realtime.gamedatahandling.inventory.dto.ItemInfo;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DropItemInInventory {

    private final InventoryRepository inventoryRepository;
    private final MapInfoRepository mapInfoRepository;
    private final Unicasting unicasting;
    private final Broadcasting broadcasting;

    private static final String eventName = "dropItemInInventory";

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        int mapId = json.getInt("mapId");
        Long userId = json.getLong("userId");
        int inventoryIdx = json.getInt("inventoryIdx");
        int itemCnt = json.getInt("itemCnt");
        String position = json.getString("position");
        log.info("[process] channelId : {}, mapId : {} , userId : {}, inventoryIdx : {}, itemCnt : {}, position : {}", channelId, mapId, userId, inventoryIdx, itemCnt, position);

        return inventoryRepository.findByInventoryIdx(channelId, userId, inventoryIdx)
                .flatMap(inventory -> {
                    InventoryInfo inventoryInfo = InventoryInfo.ofString(inventoryIdx, String.valueOf(inventory));
                    String itemCategory = inventoryInfo.getItemCategory();
                    int itemId = inventoryInfo.getItemId();
                    log.info("[process] inventoryInfo : {}", inventoryInfo);

                    int result = inventoryInfo.getItemCnt() - itemCnt;
                    if (result <= 0) {
                        return inventoryRepository.deleteByInventoryIdx(channelId, userId, inventoryIdx)
                                .then(Mono.just(InventoryInfo.ofString(inventoryIdx, "")))
                                .flatMap(emptyInventoryInfo -> {
                                    return mapInfoRepository.save(channelId, mapId, position, itemCategory, itemId, itemCnt)
                                            .then(unicasting(channelId, userId, emptyInventoryInfo))
                                            .then(broadcasting(channelId, position, ItemInfo.of(itemCategory, itemId, itemCnt)));
                                });
                    } else {
                        inventoryInfo.setItemCnt(result);
                        return inventoryRepository.save(channelId, userId, inventoryInfo)
                                .then(mapInfoRepository.save(channelId, mapId, position, itemCategory, itemId, itemCnt))
                                .then(unicasting(channelId, userId, inventoryInfo))
                                .then(broadcasting(channelId, position, ItemInfo.of(itemCategory, itemId, itemCnt)));
                    }
                });
    }

    public Mono<Void> unicasting(String channelId, Long userId, InventoryInfo inventoryInfo) {
        log.info("[unicasting] channelId : {}, userId : {}, inventoryInfo : {}", channelId, userId, inventoryInfo);

        return unicasting.unicasting(
                channelId,
                userId,
                MessageConverter.convert(Message.success(eventName, CastingType.UNICASTING, inventoryInfo))
        );
    }

    public Mono<Void> broadcasting(String channelId, String position, ItemInfo itemInfo) {
        log.info("[broadcasting] channelId : {}, position : {}, itemInfo : {}", channelId, position, itemInfo);

        Map<String, Object> map = new HashMap<>();
        map.put("position", position);
        map.put("itemInfo", itemInfo);
        log.info("[broadcasting] map : {}", map);

        return broadcasting.broadcasting(
                channelId,
                MessageConverter.convert(Message.success(eventName, CastingType.BROADCASTING, map))
        );
    }

}
