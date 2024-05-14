package com.explorer.realtime.sessionhandling.disconnect.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository.InventoryRepository;
import com.explorer.realtime.gamedatahandling.component.personal.playerInfo.repository.PlayerInfoRepository;
import com.explorer.realtime.gamedatahandling.laboratory.repository.ElementLaboratoryRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.mongo.entity.*;
import com.explorer.realtime.global.mongo.repository.InventoryDataMongoRepository;
import com.explorer.realtime.global.mongo.repository.LaboratoryDataMongoRepository;
import com.explorer.realtime.global.mongo.repository.MapDataMongoRepository;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaveGame {

    private final MapDataMongoRepository mapDataMongoRepository;
    private final MapObjectRepository mapObjectRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final Broadcasting broadcasting;
    private final SessionManager sessionManager;
    private final PlayerInfoRepository playerInfoRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryDataMongoRepository inventoryDataMongoRepository;
    private final SaveLabDatatest saveLabData;
    private final ElementLaboratoryRepository elementLaboratoryRepository;
    private final LaboratoryDataMongoRepository laboratoryDataMongoRepository;

    public Mono<Void> process(String channelId, Long userId) {
        log.info("Leave game");
        List<Integer> mapIds = Arrays.asList(1, 2, 3);
        Map<String, String> map = new HashMap<>();
        map.put("userId", String.valueOf(userId));

        return saveInventory(channelId, userId)
                .then(saveElementData(channelId))
                .then(saveCompoundData(channelId))
                .then(saveMapData(channelId))
                .then(userCount(channelId))
                .flatMap(count -> {
                    if (count == 1) {
                        log.info("Only one user in channel {}", channelId);
                        return deleteData(channelId, userId)
                                .then(deleteUserData(channelId, userId));

                    } else {
                        log.info("More than one user in channel {}", channelId);
                        return deleteData(channelId, userId)
                                .then(broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("leaveGame", CastingType.BROADCASTING, map))));

                    }

                }
                )
                .then();
    }

    private Mono<Long> userCount(String channelId) {
        return channelRepository.count(channelId);
    }

    private Mono<Boolean> deleteData(String channelId, Long userId) {
        return playerInfoRepository.deleteUserChannelInfo(channelId, userId)
                        .then(userRepository.delete(userId))
                                 .then(channelRepository.deleteByUserId(channelId, userId))
                                        .then(inventoryRepository.deleteUserInventory(channelId, userId));
    }

    private Mono<Void> deleteUserData(String channelId, Long userId) {
        channelRepository.deleteAll(channelId).subscribe();
        mapObjectRepository.deleteAllMap(channelId).subscribe();
        sessionManager.removeConnection(userId);
        elementLaboratoryRepository.deleteAllData(channelId).subscribe();
        return Mono.empty();
    }

    private Mono<Laboratory> saveElementData(String channelId) {
        return laboratoryDataMongoRepository.findByChannelIdAndItemCategory(channelId, "element")
                .flatMap(laboratory -> {
                    log.info("Id is exist");
                    return elementLaboratoryRepository.findElementData(channelId)
                            .flatMap(elements -> {
                                // 이 시점에서 elements는 실제 데이터 리스트입니다.
                                log.info("elements {}", elements);
                                laboratory.setItemCnt(elements);
                                return laboratoryDataMongoRepository.save(laboratory);
                            });
                });
    }

    private Mono<Laboratory> saveCompoundData(String channelId) {
        return laboratoryDataMongoRepository.findByChannelIdAndItemCategory(channelId, "compound")
                .flatMap(laboratory -> {
                    log.info("Id is exist");
                    return elementLaboratoryRepository.findCompoundData(channelId)
                            .flatMap(compounds -> {
                                // 이 시점에서 elements는 실제 데이터 리스트입니다.
                                log.info("compound {}", compounds);
                                laboratory.setItemCnt(compounds);
                                return laboratoryDataMongoRepository.save(laboratory);
                            });
                });
    }

    private Mono<Void> saveInventory(String channelId, Long userId) {
        return inventoryDataMongoRepository.findByChannelIdAndUserId(channelId, userId)
                .flatMap(inventory -> {
                    return inventoryRepository.findInventoryData(channelId, userId)
                            .flatMap(data -> {
                                List<InventoryData> inventoryDataList = new ArrayList<>();
                                data.forEach((inventoryIdx, value) -> {
                                    String[] parts = value.split(":");
                                    String itemCategory = parts[0];
                                    Integer itemId = Integer.parseInt(parts[1]);
                                    Integer itemCnt = Integer.parseInt(parts[2]);
                                    String isFull = parts[3];
                                    inventoryDataList.add(new InventoryData(Integer.parseInt(inventoryIdx), itemCategory, itemId, itemCnt, isFull));
                                });
                                inventory.setInventoryData(inventoryDataList);
                                return inventoryDataMongoRepository.save(inventory);
                            });

                })
                .then();
    }

    private Mono<Void> saveMapData(String channelId) {
        List<Integer> mapIds = Arrays.asList(1, 2, 3);
        return Flux.fromIterable(mapIds)
                .flatMap(mapId -> mapDataMongoRepository.findByChannelIdAndMapId(channelId, mapId)
                        .flatMap(mapData -> {
                            return mapObjectRepository.findMapData(channelId, mapId)
                                    .flatMap(data -> {
                                        List<PositionData> positionData = new ArrayList<>();
                                        data.forEach((position, value) -> {
                                            String[] parts = value.split(":");
                                            String itemCategory = parts[0];
                                            String isFarmable = parts[1];
                                            Integer itemId = Integer.parseInt(parts[2]);
                                            positionData.add(new PositionData(position, itemCategory, isFarmable, itemId));
                                        });
                                        mapData.setPositions(positionData);
                                        return mapDataMongoRepository.save(mapData);
                                    });
                        }))
                .then();
    }
}
