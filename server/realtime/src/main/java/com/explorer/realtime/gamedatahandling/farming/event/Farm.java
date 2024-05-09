package com.explorer.realtime.gamedatahandling.farming.event;

import com.explorer.realtime.gamedatahandling.farming.dto.FarmingItemInfo;
import com.explorer.realtime.gamedatahandling.farming.repository.FarmableRepository;
import com.explorer.realtime.gamedatahandling.farming.repository.MapInfoRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Farm {

    private final MapInfoRepository mapInfoRepository;
    private final FarmableRepository farmableRepository;
    private final Unicasting unicasting;

    public Mono<Void> process(JSONObject json) {
        /*
         * 1) Parsing, channelId, mapId, position
         */
        FarmingItemInfo farmingItemInfo = FarmingItemInfo.of(json);

        /*
         * 2) redis-ingame에서 현재 map에 파밍할 아이템이 있는지 확인
         */
        return checkItemInMap(farmingItemInfo)
                // {position}에 아이템이 있는 경우
                .flatMap(item -> {
                    // 파밍 가능한 아이템인지 확인
                    String[] itemInfo = item.split(":");
                    log.info("itemCategory : {}", itemInfo[0]);
                    return isFarmable(itemInfo[0])
                            .flatMap(isFarmable -> {
                                if (isFarmable) {
                                    log.info("Is Farmable");
                                    return Mono.just("farmable");
                                } else {
                                    return Mono.empty();
                                }
                            });
                })
                // FAIL : {position}에 object가 없는 경우
                .switchIfEmpty(FailFarmingUnicasting(farmingItemInfo).then(Mono.just("Failure")))
                .then();

        /*
         * 3) 파밍 대상인지 확인
         */

        /*
         * 서버에 확률 계산 요청
         */

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

    private Mono<String> checkItemInMap(FarmingItemInfo farmingItemInfo) {

        return mapInfoRepository.findByPosition(farmingItemInfo.getChannelId(), farmingItemInfo.getMapId(), farmingItemInfo.getPosition());
    }

    private Mono<Boolean> isFarmable(String itemCategory) {
        log.info("isFarmable start...");
        return farmableRepository.isFarmable(itemCategory);
    }
}
