package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.event.InitializeMapObject;
import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.CurrentMapRepository;
import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.dto.InventoryInfo;
import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository.InventoryRepository;
import com.explorer.realtime.gamedatahandling.component.personal.playerInfo.event.SetInitialPlayerInfo;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.mongo.entity.*;
import com.explorer.realtime.global.mongo.repository.InventoryDataMongoRepository;
import com.explorer.realtime.global.mongo.repository.MapDataMongoRepository;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.ingame.document.Channel;
import com.explorer.realtime.sessionhandling.ingame.dto.UserInfo;
import com.explorer.realtime.sessionhandling.ingame.repository.ChannelMongoRepository;
import com.explorer.realtime.sessionhandling.ingame.repository.ElementLaboratoryRepository;
import com.explorer.realtime.sessionhandling.ingame.repository.LaboratoryLevelRepository;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import com.explorer.realtime.staticdatahandling.event.SaveMapDataToRedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartGame {

    private final ChannelMongoRepository channelMongoRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final Broadcasting broadcasting;
    private final ElementLaboratoryRepository elementLaboratoryRepository;
    private final InitializeMapObject initializeMapObject;
    private final SetInitialPlayerInfo setInitialPlayerInfo;
    private final InitializeSaveLabData initializeSaveLabData;
    private final InventoryRepository inventoryRepository;
    private final InventoryDataMongoRepository inventoryDataMongoRepository;
    private final MapObjectRepository mapObjectRepository;
    private final MapDataMongoRepository mapDataMongoRepository;
    private final CurrentMapRepository currentMapRepository;
    private final Unicasting unicasting;
    private final LaboratoryLevelRepository laboratoryLevelRepository;
    private final SaveMapDataToRedis saveMapDataToRedis;


    private static final int INVENTORY_CNT = 8;

    public Mono<Void> process(String teamCode, String channelName) {
        log.info("Processing game start for teamCode: {}", teamCode);

        Mono<String> saveChannelMono = saveChannel(teamCode, channelName);
//        int mapId = 1;
        saveChannelMono.subscribe(channelId -> {
            Map<String, String> map = new HashMap<>();
            map.put("channelId", channelId);
            transferAndInitializeChannel(teamCode, channelId)
                    .then(Mono.defer(() -> {
                        return elementLaboratoryRepository.initialize(channelId)
                                .then(laboratoryLevelRepository.initialize(channelId))
                                // 아직 맵 정보 update 안되어서 예비로 넣어놈
//                                .then(initializeMapObject.initializeMapObject(channelId, 1))
//                                .then(initializeMapObject.initializeMapObject(channelId, 2))
//                                .then(initializeMapObject.initializeMapObject(channelId, 3))
                                .then(setInitialPlayerInfo.process(channelId, INVENTORY_CNT))
                                .then(broadcasting.broadcasting(
                                        channelId,
                                        MessageConverter.convert(Message.success("startGame", CastingType.BROADCASTING, map))
                                ));
                    }))
                    .then(initializeSaveLabData.process(channelId))
//                    .then(saveMapData(channelId))
                    .then(saveMapDataToRedis.save(channelId, 1))
                    .then(saveAllPlayerInventory(channelId))
                    .then(getMapData(channelId))
                    .then(currentMapRepository.save(channelId, 1))
                    .subscribe();
        });

        return Mono.empty();
    }

    private Mono<Void> transferAndInitializeChannel(String teamCode, String channelId) {
        return channelRepository.findAll(teamCode)
                .flatMapMany(entries -> Flux.fromIterable(entries.entrySet()))
                .flatMap(entry -> channelRepository.save(channelId, Long.parseLong(entry.getKey().toString()), Integer.parseInt(entry.getValue().toString()))
                .then(channelRepository.deleteAll(teamCode)))
                .then();
    }

    private Mono<String> saveChannel(String teamCode, String channelName) {
        return channelRepository.findAllFields(teamCode)
                .flatMap(field -> {
                    Long userId = Long.parseLong(String.valueOf(field));
                    return userRepository.findAll(userId)
                            .map(userMap -> UserInfo.of(userId, String.valueOf(userMap.get("nickname")), Integer.parseInt(String.valueOf(userMap.get("avatar")))));
                })
                .collectList()
                .flatMap(userInfoList -> {
                    return channelMongoRepository.save(Channel.from(channelName, new HashSet<>(userInfoList)))
                            .map(Channel::getId)
                            .doOnSuccess(channelId -> {
                                userInfoList.forEach(userInfo -> {
                                    userRepository.updateUserData(userInfo.getUserId(), channelId, "1").subscribe();

                                });
                            });
                });
    }

    private Mono<Void> saveMapData(String channelId) {
        List<Integer> mapIds = Arrays.asList(1, 2, 3);
        return Flux.fromIterable(mapIds)
                .flatMap(mapId -> mapObjectRepository.findMapData(channelId, mapId)
                        .flatMap(data -> {
                            MapData mapData = new MapData();
                            mapData.setChannelId(channelId);
                            mapData.setMapId(mapId);
                            List<PositionData> positions = new ArrayList<>();
                            data.forEach((position, value) -> {
                                String[] parts = value.split(":");
                                String itemCategory = parts[0];
                                String isFarmable = parts[1];
                                Integer itemId = Integer.parseInt(parts[2]);
                                positions.add(new PositionData(position, itemCategory, isFarmable, itemId));

                            });
                            mapData.setPositions(positions);
                            return mapDataMongoRepository.save(mapData);
                        })).then();
    }

    private Mono<Void> saveAllPlayerInventory(String channelId) {
        return channelRepository.findAllFields(channelId)
                .flatMap(field -> {
                    Long userId = Long.parseLong(String.valueOf(field));
                    return initializeItem(channelId, userId)
                            .then(saveInventory(channelId, userId));
                })
                .then();
    }

    private Mono<Void> initializeItem(String channelId, Long userId) {
        InventoryInfo item1 = InventoryInfo.of(0, "tool", 0, 1, 1);
        InventoryInfo item2 = InventoryInfo.of(1, "tool", 1, 1, 1);
        return inventoryRepository.save(channelId, userId, item1)
                .then(inventoryRepository.save(channelId, userId, item2))
                .then();
    }

    private Mono<Void> getMapData(String channelId) {
        return mapObjectRepository.findMapData(channelId, 1)
                .flatMap(mapData -> broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("mainMapInfo", CastingType.BROADCASTING, mapData))));
    }

    private Mono<Boolean> saveInventory(String channelId, Long userId) {
        return inventoryRepository.findInventoryData(channelId, userId)
                .flatMap(data -> {
                    Inventory inventory = new Inventory();
                    inventory.setChannelId(channelId);
                    inventory.setUserId(userId);
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
                    inventoryDataMongoRepository.save(inventory).subscribe();
//                    unicasting.unicasting(channelId, userId, MessageConverter.convert(Message.success("startInventory", CastingType.UNICASTING, inventory))).subscribe();
                    return Mono.empty();
                });
    }
}
