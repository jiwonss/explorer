package com.explorer.realtime.gamedatahandling.inventory.event;

import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository.InventoryInfoRepository;
import com.explorer.realtime.gamedatahandling.farming.dto.InventoryInfo;
import com.explorer.realtime.gamedatahandling.farming.repository.ItemRepository;
import com.explorer.realtime.gamedatahandling.inventory.dto.InventoryResponse;
import com.explorer.realtime.gamedatahandling.inventory.exception.InventoryErrorCode;
import com.explorer.realtime.gamedatahandling.inventory.exception.InventoryException;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoveItemInInventory {

    private final InventoryInfoRepository inventoryInfoRepository;
    private final ItemRepository itemRepository;
    private final Unicasting unicasting;

    private static final String eventName = "moveItemInInventory";

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        log.info("[process] channelId : {}, userId : {}, ", channelId, userId);

        int inventoryIdxFrom = json.getInt("inventoryIdxFrom");
        int inventoryIdxTo = json.getInt("inventoryIdxTo");
        log.info("[process] inventoryIdxFrom : {}, inventoryIdxTo : {}", inventoryIdxFrom, inventoryIdxTo);

        return checkInventory(channelId, userId, inventoryIdxFrom, inventoryIdxTo)
                .then(inventoryInfoRepository.findByInventoryIdx(channelId, userId, inventoryIdxFrom).map(Object::toString))
                .zipWith(inventoryInfoRepository.findByInventoryIdx(channelId, userId, inventoryIdxTo).map(Object::toString))
                .flatMap(tuple -> {
                    String fromInventory = tuple.getT1();
                    String toInventory = tuple.getT2();
                    InventoryInfo fromItem = InventoryInfo.ofString(inventoryIdxFrom, fromInventory);
                    InventoryInfo toItem = InventoryInfo.ofString(inventoryIdxTo, toInventory);
                    InventoryResponse inventoryResponse = InventoryResponse.of(fromItem, toItem);
                    log.info("[process] inventoryResponse : {}", inventoryResponse);
                    unicasting.unicasting(
                            channelId,
                            userId,
                            MessageConverter.convert(Message.success(eventName, CastingType.UNICASTING, inventoryResponse))
                    ).subscribe();
                    return Mono.empty();
                })
                .onErrorResume(InventoryException.class, error -> {
                    log.info("[process] errorCode : {}, errorMessage : {}", error.getErrorCode(), error.getMessage());
                    unicasting.unicasting(
                            channelId,
                            userId,
                            MessageConverter.convert(Message.fail(eventName, CastingType.UNICASTING, String.valueOf(error.getErrorCode()), error.getMessage()))
                    ).subscribe();
                    return Mono.empty();
                }).then();
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

    private Mono<Void> checkInventory(String channelId, Long userId, int inventoryIdxFrom, int inventoryIdxTo) {
        Mono<String> fromNono = inventoryInfoRepository.findByInventoryIdx(channelId, userId, inventoryIdxFrom).map(Object::toString);
        Mono<String> toNono = inventoryInfoRepository.findByInventoryIdx(channelId, userId, inventoryIdxTo).map(Object::toString);

        Mono<Tuple2<String, String>> tupleMono = Mono.zip(fromNono, toNono);

        return tupleMono.publishOn(Schedulers.boundedElastic()).flatMap(tuple -> {
            String fromInventory = tuple.getT1();
            String toInventory = tuple.getT2();

            if (fromInventory.isEmpty()) {
                log.info("[checkInventory] from에 해당하는 인벤토리가 비어있습니다.");
                return Mono.error(new InventoryException(InventoryErrorCode.EMPTY_INVENTORY));
            }

            Mono<Void> resultMono;

            if (toInventory.isEmpty()) {
                resultMono = Mono.fromRunnable(() -> {
                    log.info("[checkInventory] to에 해당하는 인벤토리가 비어있습니다.");
                    inventoryInfoRepository.deleteByInventoryIdx(channelId, userId, inventoryIdxFrom).block();
                    inventoryInfoRepository.save(channelId, userId, InventoryInfo.ofString(inventoryIdxTo, String.valueOf(fromInventory))).block();
                });
            } else {
                resultMono = Mono.fromRunnable(() -> {
                    InventoryInfo fromItem = InventoryInfo.ofString(inventoryIdxFrom, fromInventory);
                    InventoryInfo toItem = InventoryInfo.ofString(inventoryIdxTo, toInventory);

                    if (fromItem.getItemCategory().equals(toItem.getItemCategory()) && fromItem.getItemId() == toItem.getItemId()) {
                        log.info("[checkInventory] from, to 아이템 종류가 같습니다.");
                        int fromItemCnt = fromItem.getItemCnt();
                        int toItemCnt = toItem.getItemCnt();
                        getItemMaxCnt(fromItem.getItemCategory(), fromItem.getItemId())
                                .doOnNext(maxCnt -> {
                                    int sum = fromItemCnt + toItemCnt;
                                     log.info("[checkInventory] sum : {}", sum);
                                    if (sum <= maxCnt) {
                                        toItem.setItemCnt(sum);
                                        inventoryInfoRepository.save(channelId, userId, toItem).subscribe();
                                        inventoryInfoRepository.deleteByInventoryIdx(channelId, userId, inventoryIdxFrom).subscribe();
                                    } else {
                                        fromItem.setItemCnt(sum - maxCnt);
                                        toItem.setItemCnt(maxCnt);
                                        inventoryInfoRepository.save(channelId, userId, toItem).subscribe();
                                        inventoryInfoRepository.save(channelId, userId, fromItem).subscribe();
                                    }
                                }).subscribe();
                    } else {
                        log.info("[checkInventory] from, to 아이템 종류가 다릅니다.");
                        fromItem.setInventoryIdx(inventoryIdxTo);
                        toItem.setInventoryIdx(inventoryIdxFrom);
                        inventoryInfoRepository.save(channelId, userId, fromItem).subscribe();
                        inventoryInfoRepository.save(channelId, userId, toItem).subscribe();
                    }
                });
            }
            return resultMono.then(Mono.empty());
        });
    }

}
