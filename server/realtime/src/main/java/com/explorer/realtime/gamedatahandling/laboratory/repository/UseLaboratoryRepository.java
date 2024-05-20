package com.explorer.realtime.gamedatahandling.laboratory.repository;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Repository("UseLaboratoryRepository")
public class UseLaboratoryRepository {

    private final ReactiveRedisTemplate<String,Object> stringReactiveRedisTemplate;
    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "useLab:";
    private static final String PLAYERINFO_KEY_PREFIX = "playerInfoData:";

    public UseLaboratoryRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> stringReactiveRedisTemplate) {
        this.stringReactiveRedisTemplate = stringReactiveRedisTemplate;
        this.reactiveHashOperations = stringReactiveRedisTemplate.opsForHash();
    }

    /*
     * 특정 연구소를 사용하고 있는 player 정보를 조회한다
     * key:  useLab:{channelId}:{labId}
     * field:  {userId}
     * value:  {nickname}
     */
    public Mono<Map<Object, Object>> findAll(JSONObject json) {
        log.info("finding players using a laboratory...");

        String channelId = json.getString("channelId");
        int labId = json.getInt("labId");

        String redisKey = KEY_PREFIX + channelId + ":" + labId;

        return reactiveHashOperations
                .entries(redisKey)
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .defaultIfEmpty(Collections.emptyMap())
                .doOnSuccess(success -> log.info("SUCCESS to find players using a {} laboratory : {}", labId, success))
                .doOnError(error -> log.error("FAIL to find players using a {} laboratory: {}", labId, error.getMessage()));
    }

    /*
     * [연구소를 사용 중인 플레이어 정보 저장]
     * 파라미터 json : channelId, userId, labId
     *
     * key:  useLab:{channelId}:{labId}
     * field:  {userId}
     * value:  {nickname}
     */
    public Mono<Void> savePlayer(JSONObject json, String nickname) {

        String channelId = json.getString("channelId");
        int labId = json.getInt("labId");
        Long userId = json.getLong("userId");

        String redisKey = KEY_PREFIX + channelId + ":" + labId;

        return reactiveHashOperations.put(redisKey, userId.toString(), nickname).then();
    }

    /*
     * [플레이어의 nickname 조회]
     * key: playerInfoData:{channelId}:{userId}
     * value:
     */
    public Mono<Object> getNickname(JSONObject json) {

        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");

        String redisKey = PLAYERINFO_KEY_PREFIX + channelId + ":" + userId;

        return reactiveHashOperations.get(redisKey, "nickname")
                .doOnSuccess(nickname -> log.info("Successfully retrieved nickname: {}", nickname))
                .doOnError(error -> log.error("Error retrieving nickname: {}", error.getMessage()));
    }

    /*
     * [연구소를 사용 중인 플레이어 정보 삭제]
     * 파라미터 json : channelId, userId, labId
     *
     * key:  useLab:{channelId}:{labId}
     * field:  {userId}
     * value:  {nickname}
     */
    public Mono<Void> deletePlayer(JSONObject json) {

        String channelId = json.getString("channelId");
        int labId = json.getInt("labId");
        String userId = String.valueOf(json.getLong("userId"));

        String redisKey = KEY_PREFIX + channelId + ":" + labId;

        return reactiveHashOperations.remove(redisKey, userId).then();
    }
}
