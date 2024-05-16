package com.explorer.realtime.gamedatahandling.laboratory.event;

import com.explorer.realtime.gamedatahandling.laboratory.repository.ElementLaboratoryRepository;
import com.explorer.realtime.gamedatahandling.laboratory.repository.InventoryRepository;
import com.explorer.realtime.gamedatahandling.laboratory.repository.LaboratoryLevelRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Upgrade {

    @Value("${logic.laboratory.upgrade-url}")
    private String upgradeUrl;

    private final LaboratoryLevelRepository laboratoryLevelRepository;
    private final ElementLaboratoryRepository elementLaboratoryRepository;
    private final InventoryRepository inventoryRepository;
    private final Unicasting unicasting;
    private final ToLogicServer toLogicServer;

    public Mono<Void> process(JSONObject json) {

        return checkLabLevel(json) // 1) 연구소 레벨 조회
                .flatMap(labLevel -> {
                    if (labLevel < 0 || labLevel >= 3) { // 2-1) 업그레이드 불가능한 레벨인 경우
                        return unicastingFailData(json, "cannotUpdate");
                    } else { // 2-2) 업그레이드 가능한 레벨인 경우
                        return requestMaterialsForUpgrade(labLevel)
                                .flatMap(response ->
                                        hasRequiredMaterials(json, response)
                                                .flatMap(hasMaterials -> {
                                                    if (!hasMaterials) {
                                                        // 재료 부족으로 인한 실패 처리
                                                        return unicastingFailData(json, "noItem");
                                                    } else {
                                                        // 재료가 충분한 경우 성공 로직 처리
                                                        return useMaterialsForUpgrade(json, response)
                                                                .then(upgradeLaboratory(json))
                                                                .then(getLaboratoryInventoryInfo(json))
                                                                .flatMap(dataBody -> unicastingSuccessData(json, dataBody));
                                                    }
                                                })
                                );
                    }
                });
    }

    /*
     * [연구소의 레벨을 확인한다]
     *
     * 파라미터
     * - 타입 : JSONObject
     * - 값 : {..., "channelId":{channelId}, "userId":{userId}, "labId":{labId}}
     *
     * 반환값
     * - 타입 : Mono<Object>
     * - 값 : {level}
     *
     * 연구소 레벨 데이터 (redis-game)
     * key : labLevel:{channelId}:{labId}
     * value: {level}
     */
    private Mono<Integer> checkLabLevel(JSONObject json) {
        String channelId = json.getString("channelId");
        int labId = json.getInt("labId");
        return laboratoryLevelRepository.findLabLevel(channelId, labId)
                .map(levelStr -> {
                    try {
                        return Integer.parseInt(levelStr.toString());
                    } catch (NumberFormatException e) {
                        log.error("Failed to parse lab leve: {}", levelStr, e);
                        return -1;
                    }
                });
    }

    /*
     * [LOGIC 서버에 요청 : 합성에 필요한 element 데이터 요청]
     * 파라미터
     * - JSONObject json : { .. , "channelId" : {channelId}, "userId" : {userId}, "labId" : {labId}}
     * - int labLevel : {level}
     *
     * Logic Server :: POST data (String)
     * {"labId":{labId}, "labLevel":{labLevel}}
     *
     * Logic Server :: GET data (String)
     * {
     *  {itemCategory}:{itemId}  :  {itemCnt},
     *  {itemCategory}:{itemId}  :  {itemCnt},
     *                ...
     * }
     *
     * 반환값 :
     *  - 타입 : Mono<String>
     *  - 값 :  { {itemCategory}:{itemId} : {itemCnt}, {itemCategory}:{itemId} : {itemCnt}, .... }
     */
    private Mono<String> requestMaterialsForUpgrade(int labLevel) {

        JSONObject request = new JSONObject().put("labId", 0).put("labLevel",labLevel);
        log.info("Logic server Request Data: {}", request);

        return Mono.create(sink -> {
            toLogicServer.sendRequestToHttpServer(String.valueOf(request), upgradeUrl)
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
     * [upgrade 재료가 있는지 확인]
     * 파리미터 : json
     * - 값 :  { ..., channelId, userId, labId }
     *
     *  파라미터 : MaterialList
     *  - 값 :  { {itemCategory}:{itemId} : {itemCnt}, {itemCategory}:{itemId} : {itemCnt}, .... }
     */
    private Mono<Boolean> hasRequiredMaterials(JSONObject json, String materialList) {

        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        JSONObject materialListJson = new JSONObject(materialList);
        List<Mono<Boolean>> checks = new ArrayList<>();

        materialListJson.keys().forEachRemaining(key -> {
            String itemCategory = key.split(":")[0];
            int itemCnt = Integer.parseInt(materialListJson.get(key).toString());
            switch (itemCategory) {
                // 재료가 연구소의 element/compound 인 경우
                case "element":
                case "compound":
                    checks.add(elementLaboratoryRepository.findMaterial(channelId, key, itemCnt));
                    break;
                // 재료가 인벤토리에 있는 아이템인 경우
                default:
                    checks.add(inventoryRepository.findMaterial(channelId, userId, key, itemCnt));
                    break;
            }
        });

        // 모든 재료가 충분한지 여부를 확인
        return Flux.merge(checks)
                .all(result -> result) // 모든 결과가 true인 경우에만 true 반환
                .doOnError(error -> log.error("[ERROR] checking materials : {}", error.getMessage()));
    }

    /*
     * [연구소 upgrade 성공 -> 인벤토리 및 연구소에 있는 재료 소진 :: redis-game 업데이트]
     */
    private Mono<Void> useMaterialsForUpgrade(JSONObject json, String materialList) {

        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        JSONObject materialListJson = new JSONObject(materialList);

        List<Mono<Void>> useMaterialMonos = new ArrayList<>();

        materialListJson.keys().forEachRemaining(key -> {
            String itemCategory = key.split(":")[0];
            int itemCnt = Integer.parseInt(materialListJson.get(key).toString());
            if (itemCategory.equals("element") || itemCategory.equals("compound")) {
                useMaterialMonos.add(elementLaboratoryRepository.useMaterial(channelId, key, itemCnt));
            } else {
                useMaterialMonos.add(inventoryRepository.useMaterial(channelId, userId, key, itemCnt));
            }
        });
        // 모든 재료 사용 명령을 실행하고 모두 완료되기를 기다립니다.
        return Flux.concat(useMaterialMonos).then();
    }

    /*
     * [연구소 레벨업 후 redis-game에 업데이트]
     * 파라미터
     * - 타입 : JSONObject
     * - 값 : {..., "channelId":{channelId}, "userId":{userId}, "labId":{labId}}
     *
     * 연구소 레벨 데이터 (redis-game)
     * key : labLevel:{channelId}:{labId}
     * value: {level}
     */
    private Mono<Void> upgradeLaboratory(JSONObject json) {
        return laboratoryLevelRepository.incLabLevel(json);
    }

    /*
     * [연구소 정보 조회]
     * 연구소 입장 시 전송되는 연구소의 정보를 조회한다
     * - 연구소 레벨
     * - 연구소 저장 상태::element
     * - 연구소 저장 상태::compound
     * - 인벤토리 상태
     */
    private Mono<Map<Object, Object>> getLaboratoryInventoryInfo(JSONObject json) {

        Map<Object, Object> dataBody = new HashMap<>();
        String channelId = json.getString("channelId");
        int labId = json.getInt("labId");

        // 각 항목의 Mono를 생성
        Mono<List<Integer>> elementsMono = elementLaboratoryRepository.findAllElements(json); // 연구소 저장 상태 :: element 조회
        Mono<List<Integer>> compoundsMono = elementLaboratoryRepository.findAllCompounds(json); // 연구소 저장 상태 :: compound 조회

        // 모든 Mono를 결합하여 하나의 Map에 저장
        // elements와 compounds를 먼저 합친 후, labLevel 정보를 추가
        return Mono.zip(elementsMono, compoundsMono, (elements, compounds) -> {
            dataBody.put("element", elements);
            dataBody.put("compound", compounds);
            return dataBody;
        })
                .flatMap(combinedData ->
                laboratoryLevelRepository.findLabLevel(channelId, labId).map(Object::toString).map(labLevel -> {
                    combinedData.put("labLevel", labLevel);
                    return combinedData;
                }))
                .flatMap(combinedData ->
                        inventoryRepository.findAll(json).map(inventoryInfo -> {
                            combinedData.put("inventoryData", inventoryInfo);
                            return combinedData;
                        })
        );
    }

    /*
     * [UNICASTING : 변경된 연구소 element/compound 저장 상태 및 인벤토리 상태]
     * 파라미터
     * - JSONObject json : { .. , "channelId" : {channelId}, "userId" : {userId}, "labId" : {labId}}
     *
     * 파라미터
     * - Map dataBody :
     *  "element" : [ {itemCnt}, {itemCnt}, ... ],
     *  "compound" : [ {itemCnt}, {itemCnt}, ... ],
     *  "inventoryData" : {
     *              {inventoryId} : {itemCategory}:{itemId}:{itemCnt},
     *              {inventoryId} : {itemCategory}:{itemId}:{itemCnt}
     *                              ...
     *              }
     */
    private Mono<Void> unicastingSuccessData(JSONObject json, Map<Object, Object> dataBody) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");

        return unicasting.unicasting(channelId, userId,
                MessageConverter.convert(Message.success("upgrade", CastingType.UNICASTING, dataBody)))
                .then();
    }

    /*
     * [Unicasting : fail output data]
     * 파라미터
     * - JSONObject json : {..., "channelId":{channelId}, "userId":{userId}, "labId" : {labId}}
     * - String msg : "cannotUpgrade" 또는 "noItem"
     */
    private Mono<Void> unicastingFailData(JSONObject json, String msg) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        Map<String, String> dataBody = new HashMap<>();
        dataBody.put("msg", msg);

        return unicasting.unicasting(channelId, userId,
                MessageConverter.convert(Message.fail("upgrade", CastingType.UNICASTING, dataBody)))
                .then();
    }
}
