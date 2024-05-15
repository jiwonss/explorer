package com.explorer.realtime.gamedatahandling.laboratory.event;

import com.explorer.realtime.gamedatahandling.laboratory.repository.UseLaboratoryRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnterLab {

    private final UseLaboratoryRepository useLaboratoryRepository;
    private final Unicasting unicasting;

    public Mono<Void> process(JSONObject json) {

        log.info("EnterLab process start...");

        /*
         * 1) 해당 연구소를 사용하고 있는 사람이 있는지 확인
         */
        return findPlayersUsingLaboratory(json)
                .flatMap(playerInfos -> {
                    if(playerInfos.isEmpty()) {
                        log.info("No one is currently using the laboratory");
                    }
                    // 연구소는 이미 사용 중
                    else {
                        log.warn("Laboratory is currently being used by: {}", playerInfos);
                        return unicastingCannotUseLaboratory(json, playerInfos);
                    }
                    return Mono.empty();
                })
                .doOnError(error -> log.error("Error during processing: {}", error.getMessage()))
                .then();

    }

    /*
     * [연구소 사용 중인 사람 조회]
     * redis-game
     * key: useLab:{channelId}:{labId}
     * field: {userId}
     * value: {nickname}
     *
     * # (반환) 사용 중인 사람이 있는 경우
     * - Map 타입
     * - key : {userId}
     * - value: {nickname}
     *
     * # (반환) 사용 중인 사람이 없는 경우
     * - Map 타입
     * - emptyMap()
     */
    private Mono<Map<Object, Object>> findPlayersUsingLaboratory(JSONObject json) {
        return useLaboratoryRepository.findAll(json);
    }

    /*
     * [UNICASTING : 연구소 사용 불가]
     */
    private Mono<Void> unicastingCannotUseLaboratory(JSONObject json, Map<Object, Object> playerInfos) {
        return unicasting.unicasting(
                json.getString("channelId"),
                json.getLong("userId"),
                MessageConverter.convert(Message.fail("enterLab", CastingType.UNICASTING, playerInfos))
                )
                .then();
    }
}
