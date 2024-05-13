package com.explorer.realtime.gamedatahandling.laboratory.event;

import com.explorer.realtime.gamedatahandling.laboratory.dto.ItemInfo;
import com.explorer.realtime.gamedatahandling.laboratory.dto.UserInfo;
import com.explorer.realtime.gamedatahandling.laboratory.repository.ElementLaboratoryRepository;
import com.explorer.realtime.gamedatahandling.logicserver.ToLogicServer;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Synthesize {

    private final ToLogicServer toLogicServer;
    private final ElementLaboratoryRepository elementLaboratoryRepository;
    private final Unicasting unicasting;
    private final Broadcasting broadcasting;

    @Value("${logic.laboratory.synthesize-url}")
    private String synthesizeUrl;


    public Mono<Void> process(JSONObject json) {

        UserInfo userInfo = UserInfo.of(json);

        return requestElementsForSynthesize(json) // Logic 서버에 합성에 필요한 element 데이터 요청
                .doOnNext(response -> {
                    checkElementsInLaboratory(response, userInfo, json).subscribe();  // 현재 원소 연구소에 element가 있는지 조회
                })
                .then();
    }

    /*
     * [LOGIC 서버에 요청 : 합성에 필요한 element 데이터 요청]
     * 파라미터 : { .. , "channelId" : {channelId}, "userId": "{userId}", "itemCategory" : "compound", "itemId" : {itemId}}
     * 반환값 :
     *  - 타입 : Mono<String>
     *  - 값 :  { {itemCategory}:{itemId} : {itemCnt}, {itemCategory}:{itemId} : {itemCnt}, .... }
     */
    private Mono<String> requestElementsForSynthesize(JSONObject json) {

        JSONObject itemInfo = new JSONObject(ItemInfo.of(json));

        log.info("Logic server Request Data: {}", itemInfo);

        return Mono.create(sink -> {
            toLogicServer.sendRequestToHttpServer(String.valueOf(itemInfo), synthesizeUrl)
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
     * [원소 연구소에 필요한 element가 있는지 확인]
     * 파라미터
     *  - String response : { {itemCategory}:{itemId} : {itemCnt}, {itemCategory}:{itemId} : {itemCnt}, .... }
     *  - UserInfo userInfo : channelId, userId
     * 반환값
     *  - 타입 : Mono<Void>
     */
    private Mono<Void> checkElementsInLaboratory(String response, UserInfo userInfo, JSONObject json) {

        JSONObject responseJson = new JSONObject(response);

        // 합성에 필요한 재료 하나씩 원소 연구소에 있는지 확인
        return Flux.fromIterable(responseJson.keySet())
                .flatMap(key ->
                        elementLaboratoryRepository.findElement(userInfo.getChannelId(), key, responseJson.optInt(key, 0))
                                .flatMap(found -> {
                                    // 특정 원소가 연구소에 없는 경우
                                    if (!found) {
                                        log.info("Fail: Key {} with required count {} is not sufficient", key, responseJson.optInt(key, 0));

                                        Map<String, String> dataBody = new HashMap<>();
                                        dataBody.put("msg", "noItem");

                                        // [Unicasting] 합성 실패 : 재료 부족
                                        unicasting.unicasting(
                                                userInfo.getChannelId(), userInfo.getUserId(),
                                                MessageConverter.convert(Message.fail("synthesizing", CastingType.UNICASTING, dataBody))).subscribe();

                                        return Mono.error(new IllegalStateException("Insufficient elements"));
                                    }
                                    return Mono.just(true);
                                })
                )
                .then()
                // 합성에 필요한 모든 원소가 있는 경우
                .doOnSuccess(unused -> {
                    log.info("Success: All elements are sufficient");
                    // 1) 사용한 원소 : redis-game에 연구소-원소 저장 상태 update
                    Mono<Void> useElements = useElementsInLaboratory(responseJson, userInfo);

                    // 2) 생성한 화합물 : redis-game에 연구소-compound 상태 update
                    Mono<Void> createCompound = createCompoundInLaboratory(json);

                    // 3) 연구소 상태 broadcasting
                    Mono.when(useElements, createCompound)
                            .doOnSuccess(done -> {
                                broadcastingLaboratory(json).subscribe();
                            })
                            .subscribe();
                })
                .onErrorResume(e -> {
                    log.info("Failure: Some elements are not sufficient");
                    return Mono.empty();
                });
    }

    /*
     * [redis-game에 합성에 사용된 element 데이터 update]
     * 파라미터
     *  - JSONObject json : { {itemCategory}:{itemId} : {itemCnt}, {itemCategory}:{itemId} : {itemCnt}, .... }
     *  - UserInfo userInfo : channelId, userId
     * 반환값
     *  - 타입 : Mono<Void>
     */
    private Mono<Void> useElementsInLaboratory(JSONObject json, UserInfo userInfo) {
        log.info("json:{}, userInfo:{}", json, userInfo);

        return Flux.fromIterable(json.keySet())
                .flatMap(key ->
                        elementLaboratoryRepository.useElement(userInfo.getChannelId(), key, json.optInt(key, 0))
                )
                .then();
    }

    /*
     * [redis-game에 생성한 compound 데이터 update]
     * 파라미터 : { .. , "channelId" : {channelId}, "userId": "{userId}", "itemCategory" : "compound", "itemId" : {itemId}}
     * 반환값
     *  - 타입 : Mono<Void>
     */
    private Mono<Void> createCompoundInLaboratory(JSONObject json) {
        String channelId = json.getString("channelId");
        String itemCategory = json.getString("itemCategory");
        int itemId = json.getInt("itemId");
        return elementLaboratoryRepository.createCompound(channelId, itemCategory, itemId).then();
    }

    /*
     * [Broadcasting : 변경된 연구소 element, compound 저장 상태]
     * 파라미터 : { .. , "channelId" : {channelId}, "userId": "{userId}", "itemCategory" : "compound", "itemId" : {itemId}}
     * 반환값
     *  - 타입 : Mono<Void>
     */
    private Mono<Void> broadcastingLaboratory(JSONObject json) {

        Map<String, Object> dataBody = new HashMap<>();
        Mono<List<Integer>> getElements = elementLaboratoryRepository.findAllElements(json)
                .doOnNext(elementList -> {
                    dataBody.put("element", elementList);
                });
        Mono<List<Integer>> getCompounds = elementLaboratoryRepository.findAllCompounds(json)
                .doOnNext(compoundList -> {
                    dataBody.put("compound", compoundList);
                });
        return Mono.when(getElements, getCompounds)
                .then(Mono.defer(() ->
                    broadcasting.broadcasting(
                            json.getString("channelId"),
                            MessageConverter.convert(Message.success("synthesizing", CastingType.BROADCASTING, dataBody))
                    )
                ));
    }
}