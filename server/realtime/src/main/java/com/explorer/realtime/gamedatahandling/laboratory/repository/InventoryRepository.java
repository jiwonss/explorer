package com.explorer.realtime.gamedatahandling.laboratory.repository;

import com.explorer.realtime.gamedatahandling.laboratory.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Repository("InventoryRepositoryForLab")
public class InventoryRepository {
    private final ReactiveRedisTemplate<String,Object> stringReactiveRedisTemplate;
    private final ReactiveHashOperations<String, Object, Object> reactiveHashOperations;

    private static final String KEY_PREFIX = "inventoryData:";

    public InventoryRepository(@Qualifier("gameReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> stringReactiveRedisTemplate) {
        this.stringReactiveRedisTemplate = stringReactiveRedisTemplate;
        this.reactiveHashOperations = stringReactiveRedisTemplate.opsForHash();
    }

    /*
     * [특정 플레이어의 인벤토리를 반환한다]
     */
    public Mono<Map<Object, Object>> findAll(UserInfo userInfo) {
        return reactiveHashOperations
                .entries(KEY_PREFIX + userInfo.getChannelId() + ":" + userInfo.getUserId())
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .defaultIfEmpty(Collections.emptyMap())
                .doOnError(error -> log.error("InventoryRepositoryForLab findAll error : {}", error.getMessage()));
    }

    /*
     * [추출된 아이템을 inventory에서 삭제]
     * hasItemInventoryIds 리스트에 포함되어 있는 field 값을 삭제한다
     * 모든 필드가 삭제되면 해당 키 자체도 자동으로 삭제된다
     * => 인벤토리에 아이템이 없으면 인벤토리 데이터가 redis-ingame에서 사라진다
     */
    public Mono<Void> deleteFields(UserInfo userInfo, List<Object> hasItemInventoryIds) {
        String redisKey = KEY_PREFIX + userInfo.getChannelId() + ":" + userInfo.getUserId();

        return reactiveHashOperations
                .entries(redisKey)
                .filter(entry -> hasItemInventoryIds.contains(entry.getKey().toString()))
                .flatMap(entry -> reactiveHashOperations.remove(redisKey, entry.getKey()))
                .then()
                .doOnError(error -> log.error("Error deleting fields: {}", error.getMessage()));
    }

    /*
     * [인벤토리에 특정 아이템이 있는지 확인]
     * redis-game의 inventory 데이터 (hash)
     * key: inventoryData:{channelId}:{userId}
     * field: {inventoryIdx}
     * value: {itemCategory}:{itemId}:{itemCnt}:{isFull}
     */
    public Mono<Boolean> findMaterial(String channelId, Long userId, String info, int cnt) {
        String redisKey = KEY_PREFIX + channelId + ":" + userId;

        String itemCategory = info.split(":")[0];
        String itemId = info.split(":")[1];
        AtomicInteger remainingCnt = new AtomicInteger(cnt);

        return reactiveHashOperations.entries(redisKey)
                .map(entry -> {
                    String[] parts = entry.getValue().toString().split(":");
                    return new Object[]{entry.getKey(), parts[0], parts[1], Integer.parseInt(parts[2])};
                })
                .filter(items -> items[1].equals(itemCategory) && items[2].equals(itemId))
                .sort((a, b) -> Integer.compare((int) b[3], (int) a[3])) // itemCnt를 기준으로 내림차순 정렬
                .collectList()
                .flatMap(items -> {
                    for (Object[] item : items) {
                        log.info("item: {}", item);
                        if (remainingCnt.get() <= 0) {
                            return Mono.just(true); // 종료 조건을 만족하면 반복 중단
                        }
                        int currentQty = (int) item[3];
                        remainingCnt.addAndGet(-currentQty);
                    }
                    return Mono.just(remainingCnt.get() <= 0); // 남은 수량이 0 이하인지 여부 반환
                })
                .doOnError(error -> log.error("Error checking material from inventory: {}", error.getMessage()));
    }

    /*
     * [인벤토리에서 필요한 재료 소진]
     * redis-game의 inventory 데이터 (hash)
     * key: inventoryData:{channelId}:{userId}
     * field: {inventoryIdx}
     * value: {itemCategory}:{itemId}:{itemCnt}:{isFull}
     */
    public Mono<Void> useMaterial(String channelId, Long userId, String info, int cnt) {
        String redisKey = KEY_PREFIX + channelId + ":" + userId;
        String itemCategory = info.split(":")[0];
        String itemId = info.split(":")[1];
        AtomicInteger remainingCnt = new AtomicInteger(cnt);

        return reactiveHashOperations.entries(redisKey)
                .map(entry -> {
                    String[] parts = entry.getValue().toString().split(":");
                    return new Object[]{entry.getKey(), parts[0], parts[1], Integer.parseInt(parts[2])};
                })
                .filter(items -> items[1].equals(itemCategory) && items[2].equals(itemId) && (int) items[3] >= 0)
                .sort((a, b) -> Integer.compare((int) b[3], (int) a[3])) // itemCnt를 기준으로 내림차순 정렬
                .collectList()
                .flatMap(items -> {
                    Mono<Void> result = Mono.empty();
                    for (Object[] item : items) {
                        if (remainingCnt.get() <= 0) break; // 재료 모두 사용
                        int currentQty = (int) item[3];
                        if (currentQty > remainingCnt.get()) {
                            // 인벤토리에서 아이템 개수 감소
                            result = result.then(reactiveHashOperations.put(redisKey, item[0], item[1] + ":" + item[2] + ":" + (currentQty - remainingCnt.get()) + ":" + item[4]).then());
                            remainingCnt.set(0);
                        } else {
                            // 아이템 완전히 제거
                            result = result.then(reactiveHashOperations.remove(redisKey, item[0]).then());
                            remainingCnt.addAndGet(-currentQty);
                        }
                    }
                    return result;
                })
                .doOnError(error -> log.error("Error using material from inventory: {}", error.getMessage()))
                .then();
    }
}
