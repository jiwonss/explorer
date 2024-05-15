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
public class LeaveLab {

    private final UseLaboratoryRepository useLaboratoryRepository;
    private final Unicasting unicasting;

    public Mono<Void> process(JSONObject json) {

        log.info("LeaveLab process start...");

        /*
         * 해당 연구소를 사용하고 있는 사람이 있는지 확인
         */
        return findPlayersUsingLaboratory(json)
                .flatMap(playerInfos -> {
                    // 연구소 사용 중인 사람이 없는 경우 : UNICASTING(fail)
                    if (playerInfos.isEmpty()) {
                        log.info("No one is currently using the laboratory");
                        return unicastingCannotUseLaboratory(json);
                    }
                    else {
                        return isInLaboratory(json, playerInfos)
                                .flatMap(isInLaboratory -> {
                                    // 본인이 연구소를 사용하고 있는 경우
                                    if (isInLaboratory) {
                                        log.info("{} IS in Laboratory", json.getLong("userId"));
                                        return leaveLaboratory(json)
                                                .then(unicastingLeaveLaboratory(json));
                                    }
                                    // 본인이 연구소를 사용하고 있지 않은 경우 : UNICASTING(fail)
                                    else {
                                        log.info("{} is NOT in Laboratory", json.getLong("userId"));
                                        return unicastingCannotUseLaboratory(json);
                                    }
                                });
                    }
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
     * [연구소 사용 중인 플레이어 정보에 자신이 있는지 확인]
     */
    private Mono<Boolean> isInLaboratory(JSONObject json, Map<Object, Object> playerInfos) {
        String userId = String.valueOf(json.getLong("userId"));
        return Mono.just(playerInfos.containsKey(userId));
    }

    /*
     * [redis-game의 연구소 사용 중인 player 정보에 현재 userId/nickname을 삭제]
     */
    private Mono<Void> leaveLaboratory(JSONObject json) {
        return useLaboratoryRepository.deletePlayer(json);
    }

    /*
     * [UNICASTING : 연구소 입장]
     * 현재 연구소 정보(레벨, 저장 상태::element/compound) 전송
     */
    private Mono<Void> unicastingLeaveLaboratory(JSONObject json) {
        return unicasting.unicasting(
                        json.getString("channelId"),
                        json.getLong("userId"),
                        MessageConverter.convert(Message.success("leaveLab", CastingType.UNICASTING))
                )
                .then();
    }

    /*
     * [UNICASTING : 연구소 사용 불가]
     * 현재 사용 중인 player 정보(userId, nickname) 전송
     */
    private Mono<Void> unicastingCannotUseLaboratory(JSONObject json) {
        return unicasting.unicasting(
                        json.getString("channelId"),
                        json.getLong("userId"),
                        MessageConverter.convert(Message.fail("leaveLab", CastingType.UNICASTING))
                )
                .then();
    }
}