package com.explorer.realtime.gamedatahandling.farming.event;

import com.explorer.realtime.gamedatahandling.farming.dto.ConnectionInfo;
import com.explorer.realtime.gamedatahandling.farming.dto.ItemInfo;
import com.explorer.realtime.gamedatahandling.farming.repository.InventoryRepository;
import com.explorer.realtime.gamedatahandling.farming.repository.MapInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetItemFromMap {

    private final MapInfoRepository mapInfoRepository;
    private final InventoryRepository inventoryRepository;

    public Mono<Void> process(ConnectionInfo connectionInfo, String position) {
        log.info("getItemFromMap process");

        String channelId = connectionInfo.getChannelId();
        Long userId = connectionInfo.getUserId();
        int mapId = connectionInfo.getMapId();

        mapInfoRepository.save(channelId, mapId, position, "category", 10, 1).subscribe();

        AtomicReference<ItemInfo> itemInfo = new AtomicReference<>();;
        mapInfoRepository.find(channelId, mapId, position).subscribe(
                value -> {
                    log.info("map : {}", value);
                    itemInfo.set(ItemInfo.of((String) value, position));
                    log.info("itemInfo : {}", itemInfo.get().toString());

                    int idx = 0;
                    while (idx < 8) {

                        idx++;
                    }
                },
                error -> {
                    log.error("Error occurred: {}", error);
                }
        );
        return Mono.empty();
    }

    // 게임 시작 시 초기 인벤토리 세팅
    // 게임 재시작 시 mongodb에 저장된 정보 가져와서 세팅
    // 게임 종료 시 인벤토리 정보 저장

    // inventory idx for문 돌기
    // isFull이 true인지 확인
    // isFull이 true이면 다음 인덱스로
    // isFull이 false이면 itemId가 같은지 비교
    // itemId가 같다면 static item에서 해당 아이템이 인벤토리에 저장될 수 있는 maxCnt를 가져옴
    // 기존 아이템 개수 + 넣으려는 아이템 개수 <= maxCnt 해당 위치에 아이템 저장 후 break
    // 기존 아이템 개수 + 넣으려는 아이템 가수 > maxCnt 해당 위치에 maxCnt 까지 넣고 다음칸으로 이동
    // 다음칸이 빈칸인 경우 아이템 널기
    // 다음칸이 빈칸이 아닌 경우 또 확인하는 과정 반복
    // 만약 인벤토리에 꽉차서 더이상 들어갈 수 없다면 다시 아이템을 떨어뜨려야한다.

}
