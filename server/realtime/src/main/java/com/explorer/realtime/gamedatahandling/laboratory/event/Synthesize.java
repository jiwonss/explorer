package com.explorer.realtime.gamedatahandling.laboratory.event;

import com.explorer.realtime.gamedatahandling.laboratory.dto.ItemInfo;
import com.explorer.realtime.gamedatahandling.laboratory.dto.UserInfo;
import com.explorer.realtime.gamedatahandling.laboratory.repository.ElementLaboratoryRepository;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Synthesize {

    private final ToLogicServer toLogicServer;
    private final ElementLaboratoryRepository elementLaboratoryRepository;
    private final Unicasting unicasting;

    @Value("${logic.laboratory.synthesize-url}")
    private String synthesizeUrl;


    public Mono<Void> process(JSONObject json) {

        UserInfo userInfo = UserInfo.of(json);

        return requestElementsForSynthesize(json)
                .doOnNext(response -> {
                    checkElementsInLaboratory(response, userInfo).subscribe();
                })
                .then();
    }

    private Mono<String> requestElementsForSynthesize(JSONObject json) {

        JSONObject itemInfo = new JSONObject(ItemInfo.of(json));

        log.info("Logic server Request Data: {}", itemInfo);

//        return Mono.empty();
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

    private Mono<Void> checkElementsInLaboratory(String response, UserInfo userInfo) {
        JSONObject json = new JSONObject(response);
        return Flux.fromIterable(json.keySet())
                .flatMap(key ->
                        elementLaboratoryRepository.findElement(userInfo.getChannelId(), key, json.optInt(key, 0))
                                .flatMap(found -> {
                                    if (!found) {
                                        log.info("Fail: Key {} with required count {} is not sufficient", key, json.optInt(key, 0));
                                        Map<String, String> dataBody = new HashMap<>();
                                        dataBody.put("msg", "noItem");
                                        unicasting.unicasting(
                                                userInfo.getChannelId(), userInfo.getUserId(),
                                                MessageConverter.convert(Message.fail("synthesizing", CastingType.UNICASTING, dataBody))).subscribe();
                                        return Mono.error(new IllegalStateException("Insufficient elements"));
                                    }
                                    return Mono.just(true);
                                })
                )
                .then()
                .doOnSuccess(unused -> log.info("Success: All elements are sufficient"))
                .onErrorResume(e -> {
                    log.info("Failure: Some elements are not sufficient");
                    return Mono.empty();
                });
    }

}