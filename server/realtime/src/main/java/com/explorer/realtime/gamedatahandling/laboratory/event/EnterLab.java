package com.explorer.realtime.gamedatahandling.laboratory.event;

import com.explorer.realtime.gamedatahandling.laboratory.repository.ElementLaboratoryRepository;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnterLab {

    private final UseLaboratoryRepository useLaboratoryRepository;
    private final ElementLaboratoryRepository elementLaboratoryRepository;
    private final Unicasting unicasting;

    public Mono<Void> process(JSONObject json) { // json: channelId, userId, labId

        log.info("EnterLab process start...");

        /*
         * 해당 연구소를 사용하고 있는 사람이 있는지 확인
         */
        return findPlayersUsingLaboratory(json)
                .flatMap(playerInfos -> {
                    // 연구소 입장 가능
                    if(playerInfos.isEmpty()) {
                        log.info("No one is currently using the laboratory");
                        return enterLaboratory(json)                                            // 1) redis-game에 연구소 사용 중인 Player 데이터 저장
                                .then(getLaboratoryInfo(json))                                  // 2) 현재 laboratory 상태 조회(레벨, element/compound 저장 상태)
                                .flatMap(labInfo -> unicastingEnterLaboratory(json, labInfo));  // 3) UNICASTING :: success 및 laboratory 정보
                    }
                    // 연구소는 이미 사용 중
                    else {
                        log.warn("Laboratory is currently being used by: {}", playerInfos);
                        return unicastingCannotUseLaboratory(json, playerInfos);
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
     * [redis-game의 연구소 사용 중인 player 정보에 현재 userId/nickname을 저장]
     */
    private Mono<Void> enterLaboratory(JSONObject json) {
        return useLaboratoryRepository.getNickname(json)    // userId의 nickname 조회
                .flatMap(nickname -> {
                    return useLaboratoryRepository.savePlayer(json, nickname.toString());
                });
    }

    /*
     * [연구소 정보 조회]
     * 연구소 입장 시 전송되는 연구소의 정보를 조회한다
     * - 연구소 레벨
     * - 연구소 저장 상태::element
     * - 연구소 저장 상태::compound
     */
    private Mono<Map<Object, Object>> getLaboratoryInfo(JSONObject json) {

        Map<Object, Object> dataBody = new HashMap<>();

        return Mono.zip(
                elementLaboratoryRepository.findAllElements(json),  // 연구소 저장 상태 :: element 조회
                elementLaboratoryRepository.findAllCompounds(json), // 연구소 저장 상태 :: compound 조회
                (elements, compounds) -> {
                    dataBody.put("element", elements);
                    dataBody.put("compound", compounds);
                    return dataBody;
                }
        );
    }

    /*
     * [UNICASTING : 연구소 입장]
     * 현재 연구소 정보(레벨, 저장 상태::element/compound) 전송
     */
    private Mono<Void> unicastingEnterLaboratory(JSONObject json, Map<Object, Object> dataBody) {
        return unicasting.unicasting(
                        json.getString("channelId"),
                        json.getLong("userId"),
                        MessageConverter.convert(Message.success("enterLab", CastingType.UNICASTING, dataBody))
                )
                .then();
    }

    /*
     * [UNICASTING : 연구소 사용 불가]
     * 현재 사용 중인 player 정보(userId, nickname) 전송
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
