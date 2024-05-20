package com.explorer.realtime.gamedatahandling.component.common.mapinfo.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.CurrentMapRepository;
import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.initializing.repository.MapRepository;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsteroidMapObject {

    private final MapRepository mapRepository;
    private final MapObjectRepository mapObjectRepository;
    private final Broadcasting broadcasting;
    private final CurrentMapRepository currentMapRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;


    public Mono<Void> asteroidMapObject(String channelId) {

        String itemCategory = "debris";
        Integer itemId = 3;
        Integer mapId = 4;
        Integer spaceSheep = 0;
        log.info("asteroidMapObject channelId: {}, mapId: {}", channelId, mapId);
        // 행성 이동 시 소행성 정보 삭제 시켜주는 것으로 변경
        return mapRepository.findMapData(mapId)
                .collectList()
                .flatMap(data -> {
                    List<String> selectData = selectRandomEntries(data, 150);
                    List<String> selectSpaceSheep = selectRandomEntries(data, 1);
                    log.info("one position : {}", selectSpaceSheep);
                    Mono<Void> saveSpaceSheep = Mono.defer(() -> {
                        if (Math.random() < 0.05) {
                            log.info("Saving space sheep data...");
                            return mapObjectRepository.saveMapData(channelId, mapId, selectSpaceSheep, itemCategory, spaceSheep);
                        } else {
                            log.info("Not saving space sheep data.");
                            return Mono.empty();
                        }
                    }).then();

                    mapObjectRepository.saveMapData(channelId, mapId, selectData, itemCategory, itemId)
                            .then(saveSpaceSheep).subscribe();
                    currentMapRepository.save(channelId, mapId).subscribe();
                    position(channelId).subscribe();
                    return mapObjectRepository.findMapData(channelId, mapId)
                            .flatMap(mapData -> broadcasting.broadcasting(channelId,
                                    MessageConverter.convert(Message.success("asteroidMapObject", CastingType.BROADCASTING, mapData))));
                });
    }

    private List<String> selectRandomEntries(List<String> positions, int count) {
        List<String> selectedEntries = new ArrayList<>(positions);
        Collections.shuffle(selectedEntries);  // 리스트를 무작위로 섞음
        return selectedEntries.subList(0, Math.min(selectedEntries.size(), count));  // 랜덤하게 count개의 요소를 선택
    }

    private Mono<Void> position(String channelId) {
        return channelRepository.findAllFields(channelId)
                .flatMap(field -> {
                    Long userId = Long.parseLong(String.valueOf(field));
                    String position = getNewPosition(userId);
                    return userRepository.findAvatarAndNickname(userId)
                            .map(userDetail -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("position", position);
                                map.put("userId", userId);
                                map.put("mapId", 4);
                                map.put("nickname", userDetail.get("nickname"));
                                map.put("avatar", userDetail.get("avatar"));
                                return map;
                            });
                })
                .collectList()
                .flatMap(allUsers -> {
                    Map<String, Object> broadcastMap = new HashMap<>();
                    broadcastMap.put("positions", allUsers);
                    return broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("asteroidMapPositions", CastingType.BROADCASTING, broadcastMap)));
                });
    }

    private String getNewPosition(Long userId) {
        String[] positions = {"1:0:1", "2:0:2", "3:0:3", "1:0:2", "2:0:3", "1:0:3"};
        int index = Math.abs(userId.hashCode()) % positions.length;
        return positions[index];
    }
}