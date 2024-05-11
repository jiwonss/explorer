package com.explorer.realtime.gamedatahandling.farming.event;

import com.explorer.realtime.gamedatahandling.farming.dto.FarmingItemInfo;
import com.explorer.realtime.gamedatahandling.farming.repository.MapInfoRepository;
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
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Farm {

    private final MapInfoRepository mapInfoRepository;
    private final ToLogicServer toLogicServer;
    private final Unicasting unicasting;

    @Value("${logic.farm.farm-url}")
    private String farmUrl;

    public Mono<Void> process(JSONObject json) {
        /*
         * 1) Parsing, channelId, mapId, position
         */
        FarmingItemInfo farmingItemInfo = FarmingItemInfo.of(json);

        /*
         * 2) redis-ingame에서 현재 map에 파밍할 아이템이 있는지 확인
         */
        return checkItemInMap(farmingItemInfo).then();

    }

    /*
     * [map의 {position}에 오브젝트가 있는지 확인]
     * 없는 경우 : empty Mono 반환
     * 있는 경우 : {itemCategory}:{isFarmable}:{itemId} 반환
     */
    private Mono<Void> checkItemInMap(FarmingItemInfo farmingItemInfo) {

        return mapInfoRepository.findByPosition(farmingItemInfo.getChannelId(), farmingItemInfo.getMapId(), farmingItemInfo.getPosition())
                .flatMap(itemInfo ->
                    isFarmable(itemInfo)
                            .flatMap(isFarmable -> {
                                if (!isFarmable) {
                                    return FailFarmingUnicasting(farmingItemInfo);
                                }
                                return requestDroppedItems(itemInfo)
                                        .flatMap(response -> successFarmingUnicasting(farmingItemInfo, response));
                            })
                )
                .then();
    }

    /*
     * [파밍 가능한 오브젝트인지 확인]
     * False : 정보가 부족한 경우 또는 notFarmable 한 경우
     * True : isFarmable 한 경우
     */
    private Mono<Boolean> isFarmable(String itemInfo) {

        log.info("isFarmable start...");
        String[] parsedItemInfo = itemInfo.split(":");

        boolean isFarmable = parsedItemInfo.length > 1 && parsedItemInfo[1].equals("isFarmable");

        log.info("{} is isFarmable? : {}", parsedItemInfo[1], isFarmable);

        return Mono.just(isFarmable);
    }

    /*
     * [로직 서버에 드랍 아이템 데이터를 요청]
     */
    private Mono<String> requestDroppedItems(String itemInfo) {

        log.info("Logic server Request Data : {}", itemInfo);

        return Mono.create(sink -> {
            toLogicServer.sendRequestToHttpServer(itemInfo, farmUrl)
                    .subscribe(response -> {
                        // logic 서버로부터 추출된 원소 데이터 수신
                        log.info("Logic server response: {}", response);

                        sink.success(response);
                    }, error -> {
                        log.error("Error in retrieving data from logic server");
                        sink.error(error);
                    });
        });

    }

    private Mono<Void> successFarmingUnicasting(FarmingItemInfo farmingItemInfo, String response) {

        log.info("SUCCESS TO FARM in position {}", farmingItemInfo.getPosition());

        Map<String, Object> dataBody = new HashMap<>();
        dataBody.put("map", farmingItemInfo.getMapId());
        dataBody.put("position", farmingItemInfo.getPosition());

        Map<String, String> droppedItems = new HashMap<>();

        String[] pairs = response.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split("\":");
            String key = keyValue[0].split("\"")[1];
            String value = keyValue[1];
            droppedItems.put(key, value);
        }

        dataBody.put("droppedItems", droppedItems);

        return unicasting.unicasting(
                farmingItemInfo.getChannelId(),
                farmingItemInfo.getUserId(),
                MessageConverter.convert(Message.success("farm", CastingType.UNICASTING, dataBody)));
    }

    private Mono<Void> FailFarmingUnicasting(FarmingItemInfo farmingItemInfo) {

        log.info("FAIL TO FARM in position {}", farmingItemInfo.getPosition());

        Map<String, Object> dataBody = new HashMap<>();
        dataBody.put("msg", "noItem");
        return unicasting.unicasting(
                farmingItemInfo.getChannelId(),
                farmingItemInfo.getUserId(),
                MessageConverter.convert(Message.fail("farm", CastingType.UNICASTING, dataBody)));
    }

}
