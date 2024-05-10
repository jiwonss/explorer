package com.explorer.realtime.gamedatahandling.farming.event;

import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository.InventoryInfoRepository;
import com.explorer.realtime.gamedatahandling.component.personal.playerInfo.repository.PlayerInfoRepository;
import com.explorer.realtime.gamedatahandling.farming.dto.InventoryInfo;
import com.explorer.realtime.gamedatahandling.farming.exception.FarmingErrorCode;
import com.explorer.realtime.gamedatahandling.farming.exception.FarmingException;
import com.explorer.realtime.gamedatahandling.farming.repository.ItemRepository;
import com.explorer.realtime.gamedatahandling.farming.repository.MapInfoRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetItemFromMap {

    private final MapInfoRepository mapInfoRepository;
    private final ItemRepository itemRepository;
    private final PlayerInfoRepository playerInfoRepository;
    private final InventoryInfoRepository inventoryInfoRepository;
    private final Unicasting unicasting;
    private final Broadcasting broadcasting;

    private static final String eventName = "getItemFromMap";

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        int mapId = json.getInt("mapId");
        String position = json.getString("position");

        log.info("[process] channelId : {}, userId : {}, mapId : {}, position : {}", channelId, userId, mapId, position);

        Mono<Map<String, Object>> itemInfoMono = getItemInfoByPosition(channelId, mapId, position);

        Mono<Integer> inventoryCntMono = getInventoryCntByUserId(channelId, userId);

        Mono<InventoryInfo> checkInventoryMono = Mono.zip(itemInfoMono, inventoryCntMono)
                .flatMap(tuple -> {
                    Map<String, Object> itemInfo = tuple.getT1();
                    int inventoryCnt = tuple.getT2();
                    String itemCategory = (String) itemInfo.get("itemCategory");
                    int itemId = (int) itemInfo.get("itemId");
                    return checkInventory(channelId, userId, itemCategory, itemId, inventoryCnt);
                });

        return checkInventoryMono
                .flatMap(result -> {
                    log.info("[process] result : {}", result);

                    unicasting.unicasting(
                            channelId,
                            userId,
                            MessageConverter.convert(Message.success(eventName, CastingType.UNICASTING, result))
                    ).subscribe();

                    Map<String, Object> map = new HashMap<>();
                    map.put("position", position);

                    broadcasting.broadcasting(
                            channelId,
                            MessageConverter.convert(Message.success(eventName, CastingType.BROADCASTING, map))
                    ).subscribe();
                    return Mono.empty();
                })
                .onErrorResume(FarmingException.class, error -> {
                    log.info("[process] errorCode : {}, errorMessage : {}", error.getErrorCode(), error.getMessage());
                    unicasting.unicasting(
                            channelId,
                            userId,
                            MessageConverter.convert(Message.fail(eventName, CastingType.UNICASTING, String.valueOf(error.getErrorCode()), error.getMessage()))
                    ).subscribe();
                    return Mono.empty();
                })
                .then();
    }

    private Mono<Map<String, Object>> getItemInfoByPosition(String channelId, int mapId, String position) {
        log.info("[getItemInfoByPosition] channelId : {}, mapId : {}, position : {}", channelId, mapId, position);

        return mapInfoRepository.findByPosition(channelId, mapId, position)
                .map(result -> {
                    log.info("[getItemInfoByPosition] result : {}", result);
                    String[] itemInfo = String.valueOf(result).split(":");
                    Map<String, Object> map = new HashMap<>();
                    map.put("itemCategory", itemInfo[0]);
                    map.put("itemId", Integer.parseInt(itemInfo[1]));
                    return map;
                });
    }

    private Mono<Integer> getInventoryCntByUserId(String channelId, Long userId) {
        log.info("[getInventoryCntByUserId] channelId : {}, userId : {}", channelId, userId);

        return playerInfoRepository.findInventoryCnt(channelId, userId)
                .map(map -> Integer.parseInt(String.valueOf(map)));
    }

    private Mono<Integer> getItemMaxCnt(String itemCategory, int itemId) {
        log.info("[getItemMaxCnt] itemCategory : {}, itemId : {}", itemCategory, itemId);

        return itemRepository.findByItemCategoryAndItemId(itemCategory, itemId)
                .flatMap(map -> {
                    int maxCnt = Integer.parseInt(String.valueOf(map));
                    log.info("[getItemMaxCnt] maxCnt : {}", maxCnt);
                    return Mono.just(maxCnt);
                });
    }

    private Mono<InventoryInfo> putItemInInventory(InventoryInfo result, String channelId, Long userId, String itemCategory, int itemId) {
        return getItemMaxCnt(itemCategory, itemId)
                .flatMap(maxCnt -> {
                    if (result.getItemCnt() == maxCnt) {
                        result.setIsFull(1);
                    }
                    return inventoryInfoRepository.save(channelId, userId, result).thenReturn(result);
                });
    }

    private Mono<InventoryInfo> checkInventory(String channelId, Long userId, String itemCategory, int itemId, int inventoryCnt) {
        log.info("[checkInventory] channelId : {}, userId : {}, itemCategory : {}, itemId : {}, inventoryCnt : {}", channelId, userId, itemCategory, itemId, inventoryCnt);

        return Flux.range(0, inventoryCnt)
                .concatMap(idx -> {
                    log.info("[checkInventory] idx : {}", idx);
                    return inventoryInfoRepository.findByInventoryIdx(channelId, userId, idx)
                            .flatMap(inventoryInfo -> {
                                log.info("[checkInventory] inventoryInfo : {}", inventoryInfo);
                                if (inventoryInfo == null || String.valueOf(inventoryInfo).isEmpty()) {
                                    InventoryInfo result = InventoryInfo.of(idx, itemCategory, itemId, 1, 0);
                                    return putItemInInventory(result, channelId, userId, itemCategory, itemId);
                                }
                                InventoryInfo result = InventoryInfo.ofString(idx, String.valueOf(inventoryInfo));
                                if (result.getIsFull() == 0 && (itemCategory.equals(result.getItemCategory()) && itemId == result.getItemId())) {
                                    result.setItemCnt(result.getItemCnt() + 1);
                                    return putItemInInventory(result, channelId, userId, itemCategory, itemId);
                                }
                                return Mono.empty();
                            });
                })
                .next()
                .switchIfEmpty(Mono.error(new FarmingException(FarmingErrorCode.EXCEEDING_CAPACITY)));
    }

}
