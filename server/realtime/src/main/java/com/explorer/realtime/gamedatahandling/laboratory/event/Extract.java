package com.explorer.realtime.gamedatahandling.laboratory.event;

import com.explorer.realtime.gamedatahandling.laboratory.dto.ItemInfo;
import com.explorer.realtime.gamedatahandling.laboratory.repository.ElementLaboratoryRepository;
import com.explorer.realtime.gamedatahandling.laboratory.repository.InventoryRepository;
import com.explorer.realtime.gamedatahandling.logicserver.ToLogicServer;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class Extract {

    private final ToLogicServer toLogicServer;
    private final InventoryRepository inventoryRepository;
    private final ElementLaboratoryRepository elementLaboratoryRepository;
    private final Unicasting unicasting;

    @Value("${logic.laboratory.extract-url}")
    private String extractUrl;

    /*
     * 파라미터 JSONObject json: channelId, userId, itemCategory, itemId
     */
    public Mono<Void> process(JSONObject json) {
        // 인벤토리에 추출 가능한 아이템이 있는지 확인
        return checkExtractionMaterialInInventory(json)
                .flatMap(result -> {
                    List<Integer> inventoryIdList = result.getFirst();
                    int count = result.getSecond();

                    // 추출 가능한 아이템이 없는 경우
                    if (inventoryIdList.isEmpty() && count == 0) {
                        return unicastingFailData(json, "noItem");
                    }
                    // 추출 가능한 아이템이 있는 경우
                    else if (!inventoryIdList.isEmpty() && count > 0) {
                        // 로직 서버에 추출 결과 데이터를 요청
                        return requestElementsForExtract(json)
                                .flatMap(response ->
                                        updateExtractionResultInLaboratory(json, response)  // [redis-game] 연구소에 추출 원소 저장
                                                .then(deleteMaterialInInventory(json, inventoryIdList)) // [redis-game] 인벤토리에서 사용한 재료 제거
                                                .then(unicastingSuccessData(json, response))    // 성공 결과 UNICASTING
                                );
                    }
                    return Mono.empty();
                })
                .doOnError(error -> log.error("ERROR process: {}", error.getMessage()))
                .then();
    }

    /*
     * [inventory에 추출할 아이템이 있는지 확인]
     * 파라미터 JSONObject json: channelId, userId, itemCategory, itemId
     */
    private Mono<Pair<List<Integer>, Integer>> checkExtractionMaterialInInventory(JSONObject json) {
        log.info("checkExtractionMaterialInInventory start...");

        int itemId = json.getInt("itemId");

        List<Integer> hasItemInventoryIds = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(0);

        return getInventoryData(json)
                .flatMap(inventoryData -> {
                    inventoryData.forEach((key, value) -> {
                        log.info("key:{}, value:{}", key, value);
                        String[] parts = value.toString().split(":");
                        if (parts.length >= 4) {
                            String itemCategory = parts[0];
                            int parsedItemId = Integer.parseInt(parts[1]);
                            int itemCnt = Integer.parseInt(parts[2]);

                            if ("extractionMaterial".equals(itemCategory) && itemId == parsedItemId) {
                                hasItemInventoryIds.add(Integer.parseInt(key.toString()));
                                count.addAndGet(itemCnt);
                            }
                        }
                    });
                    return Mono.just(Pair.of(hasItemInventoryIds, count.get()));
                });
    }

    /*
     * [redis-game에서 inventory 데이터 조회]
     * 파라미터 JSONObject json: channelId, userId, itemCategory, itemId
     *
     * reids-game inventory 상태 데이터 형식
     * - key: inventoryData:{channelId}:{userId}
     * - value(hash)
     *   - field: {inventoryId}
     *   - value: {itemCategory}:{itemId}:{itemCnt}:{isFull}
     */
    private Mono<Map<Object, Object>> getInventoryData(JSONObject json) {
        return inventoryRepository.findAll(json)
                .doOnSuccess(success -> log.info("SUCCESS getInventoryData: {}", success))
                .doOnError(error -> log.error("ERROR getInventoryData: {}", error.getMessage()));
    }

    /*
     * [LOGIC 서버에 요청 : 추출 결과 데이터 요청]
     * 파라미터 JSONObject json: channelId, userId, itemCategory, itemId
     * 반환값 :
     *  - 타입 : Mono<String>
     *  - 값 :  { {itemCategory}:{itemId} : {itemCnt}, {itemCategory}:{itemId} : {itemCnt}, .... }
     */
    private Mono<String> requestElementsForExtract(JSONObject json) {

        JSONObject itemInfo = new JSONObject(ItemInfo.of(json));

        log.info("Logic server Request Data for Extract: {}", itemInfo);

        return Mono.create(sink -> {
            toLogicServer.sendRequestToHttpServer(String.valueOf(itemInfo), extractUrl)
                    .subscribe(response -> {
                        log.info("Logic server response: {}", response);
                        sink.success(response);
                    }, error -> {
                        log.error("Error in retrieving data from logic server");
                        sink.error(error);
                    });
        });
    }

    /*
     * [redis-game의 laboratoryData에 추출 결과 저장]
     * 파라미터
     * - JSONObject json: channelId, userId, itemCategory, itemId
     * String response: { {itemCategory}:{itemId} : {itemCnt}, {itemCategory}:{itemId} : {itemCnt}, .... }
     *
     * redis-game 연구소 상태 데이터 형식
     * - key: labData:{channelId}:0:element
     * - value: List
     *   - index: {itemId}
     *   - value : {itemCnt}
     */
    private Mono<Void> updateExtractionResultInLaboratory(JSONObject json, String response) {
        log.info("updateExtractionResultInLaboratory start...");

        String channelId = json.getString("channelId");

        String trimmedResponse = response.trim().replaceAll("[{}]", "");
        String[] keyValuePairs = trimmedResponse.split(",");

        return Flux.fromArray(keyValuePairs)
                .flatMap(pair -> {
                    String[] result = pair.replaceAll("\"", "").split(":");
                    String itemCategory = result[0];
                    int itemId = Integer.parseInt(result[1]);
                    int itemCnt = Integer.parseInt(result[2]);

                    log.info("itemId:{}, itemCnt:{}", itemId, itemCnt);
                    return elementLaboratoryRepository.UpdateItemCnt(channelId, itemCategory, itemId, itemCnt);
                })
                .then();
    }

    /*
     * [redis-game 인벤토리 데이터 update: 추출 후 사용한 아이템 제거]
     * 파라미터
     * - JSONObject json: channelId, userId, itemCategory, itemId
     * - List<Integer> inventoryIdList : 추출에 사용된 아이템이 들어있었던 인벤토리 아이디
     *
     * reids-game inventory 상태 데이터 형식
     * - key: inventoryData:{channelId}:{userId}
     * - value(hash)
     *   - field: {inventoryId}
     *   - value: {itemCategory}:{itemId}:{itemCnt}:{isFull}
     */
    private Mono<Void> deleteMaterialInInventory(JSONObject json, List<Integer> inventoryIdList) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");

        return Flux.fromIterable(inventoryIdList)
                .flatMap(inventoryId -> inventoryRepository.deleteField(channelId, userId, inventoryId))
                .then()
                .doOnSuccess(success -> log.info("SUCCESS deleteMaterialInInventory: {}", success))
                .doOnError(error -> log.error("ERROR deleteMaterialInInventory: {}", error.getMessage()));
    }

    /*
     * [Unicasting : inventoryData, extractResult, labData:element]
     * 파라미터
     * - JSONObject json: JSONObject json: channelId, userId, itemCategory, itemId
     * - String response
     * 반환값
     *  - 타입 : Mono<Void>
     */
    private Mono<Void> unicastingSuccessData(JSONObject json, String response) {

        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        Map<String, Object> dataBody = new HashMap<>();

        // 각 항목의 Mono를 생성
        Mono<Map<Object, Object>> inventoryMono = inventoryRepository.findAll(json); // 인벤토리 저장 상태 :: element 조회
        Mono<List<Integer>> labDataElementMono = elementLaboratoryRepository.findAllElements(json); // 연구소 저장 상태 :: compound 조회

        // 모든 Mono를 결합하여 하나의 Map에 저장
        // elements와 compounds를 먼저 합친 후, labLevel 정보를 추가
        return Mono.zip(inventoryMono, labDataElementMono, (inventorys, elements) -> {
            dataBody.put("inventoryData",  inventorys.isEmpty() ? "noItem" : inventorys);
            dataBody.put("labData:element", elements);
            return dataBody;
        }).flatMap(combinedData ->
                {
                    String trimmedResponse = response.trim().replaceAll("[{}]", "");
                    String[] keyValuePairs = trimmedResponse.split(",");

                    Map<String, Integer> extractResult = new HashMap<>();
                    for (String pair : keyValuePairs) {
                        String[] result = pair.replaceAll("\"", "").split(":");

                        String itemCategory = result[0];
                        String itemId = result[1];
                        int itemCnt = Integer.parseInt(result[2]);

                        // Map의 키를 {itemCategory}:{itemId}로 설정
                        String key = itemCategory + ":" + itemId;
                        extractResult.put(key, itemCnt);
                        log.info("[extractResult] key:{}, value:{}", key, itemCnt);

                    }
                    combinedData.put("extractResult", extractResult);
                    return Mono.just(combinedData);
                })
                .flatMap(combinedData ->
                        unicasting.unicasting(
                                channelId, userId,
                                MessageConverter.convert(Message.success("extracting", CastingType.UNICASTING, combinedData))
                        )
                )
                .then();
    }

    /*
     * [Unicasting : fail output data]
     * 파라미터
     * - JSONObject json: channelId, userId, itemCategory, itemId
     * - String msg : "noItem"
     */
    private Mono<Void> unicastingFailData(JSONObject json, String msg) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        Map<String, String> dataBody = new HashMap<>();
        dataBody.put("msg", msg);

        return unicasting.unicasting(channelId, userId,
                        MessageConverter.convert(Message.fail("extracting", CastingType.UNICASTING, dataBody)))
                .then();
    }
}
