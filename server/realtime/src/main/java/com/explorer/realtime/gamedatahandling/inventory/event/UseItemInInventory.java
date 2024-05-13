package com.explorer.realtime.gamedatahandling.inventory.event;

import com.explorer.realtime.gamedatahandling.component.common.boxinfo.dto.InventoryItemInfo;
import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.dto.InventoryInfo;
import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository.InventoryRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
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
public class UseItemInInventory {

    private final InventoryRepository inventoryRepository;
    private final Unicasting unicasting;

    private static final String eventName = "useItemInInventory";

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        int inventoryIdx = json.getInt("inventoryIdx");
        log.info("[process] channelId : {}, userId : {}, inventoryIdx : {}", channelId, userId, inventoryIdx);

        return inventoryRepository.findByInventoryIdx(channelId, userId, inventoryIdx)
                .flatMap(itemInfo -> {
                    InventoryInfo inventoryInfo = InventoryInfo.ofString(inventoryIdx, String.valueOf(itemInfo));
                    log.info("[process] inventoryInfo : {}", inventoryInfo);

                    int result = inventoryInfo.getItemCnt() - 1;
                    if (result > 0) {
                        inventoryInfo.setItemCnt(result);
                        return inventoryRepository.save(channelId, userId, inventoryInfo)
                                .flatMap(savedItem -> {
                                    log.info("[process] 결과 반영 후 아이템이 있는 경우 : {}", inventoryInfo);
                                    return unicasting.unicasting(
                                            channelId,
                                            userId,
                                            MessageConverter.convert(Message.success(eventName, CastingType.UNICASTING, inventoryInfo))
                                    ).then();
                                });
                    } else {
                        return inventoryRepository.deleteByInventoryIdx(channelId, userId, inventoryIdx)
                                .flatMap(deletedCount -> {
                                    InventoryInfo response = InventoryInfo.ofString(inventoryIdx, "");
                                    log.info("[process] 결과 반영 후 아이템이 없는 경우 : {}", response);
                                    return unicasting.unicasting(
                                            channelId,
                                            userId,
                                            MessageConverter.convert(Message.success(eventName, CastingType.UNICASTING, response))
                                    ).then();
                                });
                    }
                });
    }

}
