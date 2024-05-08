package com.explorer.realtime.gamedatahandling.laboratory.event;

import com.explorer.realtime.gamedatahandling.laboratory.dto.UserInfo;
import com.explorer.realtime.gamedatahandling.laboratory.repository.ElementLaboratoryRepository;
import com.explorer.realtime.gamedatahandling.laboratory.repository.InventoryRepositoryForLab;
import com.explorer.realtime.gamedatahandling.logicserver.ToLogicServer;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class Extract {

    private final InventoryRepositoryForLab inventoryRepositoryForLab;
    private final ElementLaboratoryRepository elementLaboratoryRepository;
    private final Unicasting unicasting;
    private final ToLogicServer toLogicServer;

    @Value("${logic.laboratory.extract-url}")
    private String extractUrl;

    public Mono<Void> process(JSONObject json) {
        /*
         * 1) Parsing
         */
        UserInfo userInfo = UserInfo.of(json);
        log.info("userInfo : {} in channel {}", userInfo.getUserId(), userInfo.getChannelId());

        /*
         * 2) redis-ingame inventory에서 추출 재료가 있는지 확인
         */
        findAllInventoryItemByUserInfo(userInfo)
                .flatMap(map -> {
                    if (map.isEmpty()) {
                        // inventory에 아이템이 없는 경우
                        return Mono.fromRunnable(() -> {
                            log.warn("No inventory data found for :{} in {} channel", userInfo.getUserId(), userInfo.getChannelId());
                            // [unicasting] : FAIL output data
                            unicasting.unicasting(userInfo.getChannelId(), userInfo.getUserId(), MessageConverter.convert(Message.fail("extracting", CastingType.UNICASTING)));
                        });
                    } else {
                        // inventory에 아이템이 있는 경우
                        List<Object> hasItemInventoryIds = new ArrayList<>();
                        AtomicInteger totalCnt = new AtomicInteger();
                        String itemCategory = json.getString("itemCategory");
                        int itemId = json.getInt("itemId");

                        return Flux.fromIterable(map.entrySet())
                                .doOnNext(entry -> {
                                    // {itemId}와 일치하는지 확인
                                    log.info("Field:{}, Value:{}", entry.getKey(), entry.getValue());
                                    // 1) entry.getValue() 파싱하기 : itemCategory(String), itemId(int), itemCnt(int)
                                    String[] parsedValues = entry.getValue().toString().split(":");
                                    // 2) itemCategory와 itemId 가 동일한지 확인
                                    if(parsedValues[0].equals(itemCategory) && Integer.parseInt(parsedValues[1]) == itemId) {
                                        hasItemInventoryIds.add(entry.getKey());
                                        totalCnt.addAndGet(Integer.parseInt(parsedValues[2]));
                                    }

                                })
                                .then(Mono.fromRunnable(() -> {
                                    // 추출할 아이템이 없는 경우
                                    if (totalCnt.get() == 0) {
                                        log.warn("No items to extract for user {} in channel {}", userInfo.getUserId(), userInfo.getChannelId());
                                        // [unicasting] : FAIL output data
                                        unicasting.unicasting(userInfo.getChannelId(), userInfo.getUserId(), MessageConverter.convert(Message.fail("extracting", CastingType.UNICASTING)));
                                    }
                                    // 추출할 아이템이 있는 경우 : logic server에 확률 계산 요청
                                    else {
                                        log.info("Requesting logic server with total count: {} for user {} in channel {}", totalCnt, userInfo.getUserId(), userInfo.getChannelId());

                                        JSONObject requestPayload = new JSONObject();
                                        requestPayload.put("itemCategory", itemCategory);
                                        requestPayload.put("itemId", itemId);
                                        requestPayload.put("itemCnt", totalCnt.get());

                                        toLogicServer.sendRequestToHttpServer(requestPayload.toString(), extractUrl)
                                                .subscribe(response -> {
                                                    log.info("Logic server response: {}", response);
                                                    elementLaboratoryRepository.updateValueAtIndex(userInfo.getChannelId(), response).subscribe();
                                                });
                                    }
                                        })
                                );
                    }
                })
                .subscribe();

        return Mono.empty();
    }

    private Mono<Map<Object, Object>> findAllInventoryItemByUserInfo(UserInfo userInfo) {
        return inventoryRepositoryForLab.findAll(userInfo);
    }
}
