package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository.InventoryRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.mongo.entity.Inventory;
import com.explorer.realtime.global.mongo.entity.InventoryData;
import com.explorer.realtime.global.mongo.repository.InventoryDataMongoRepository;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserInfoData {

    private final InventoryRepository inventoryRepository;
    private final InventoryDataMongoRepository inventoryDataMongoRepository;
    private final UserRepository userRepository;
    private final Unicasting unicasting;
    private final ChannelRepository channelRepository;

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        Integer mapId = json.getInt("mapId");
        saveInventory(channelId, userId).subscribe();
        positions(channelId, userId, mapId).subscribe();
        return Mono.empty();
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
//                    inventoryDataMongoRepository.save(inventory).subscribe();
                    unicasting.unicasting(channelId, userId, MessageConverter.convert(Message.success("userInventory", CastingType.UNICASTING, inventory))).subscribe();
                    return Mono.empty();
                });
    }

    private Mono<Void> positions(String channelId, Long userId, Integer mapId) {
        return channelRepository.findAllFields(channelId)
                .flatMap(field -> {
                    Long userIds = Long.parseLong(String.valueOf(field));
                    String position = getNewPosition(userIds);

                    return userRepository.findAvatarAndNickname(userIds)
                            .flatMap(userDetail -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("position", position);
                                map.put("userId", userIds);
                                map.put("mapId", mapId);
                                map.put("nickname", userDetail.get("nickname"));
                                map.put("avatar", userDetail.get("avatar"));
                                return Mono.just(map);
                            });
                })
                .collectList()
                .flatMap(allUsers -> {
                    Map<String, Object> unicastMap = new HashMap<>();
                    unicastMap.put("positions", allUsers);
                    unicasting.unicasting(channelId, userId, MessageConverter.convert(Message.success("getUserPosition", CastingType.UNICASTING, unicastMap))).subscribe();
                    return Mono.empty();
                });
    }

    private String getNewPosition(Long userId) {
        String[] positions = {
                "1:0:1:0:0:0", "2:0:2:0:0:0", "3:0:3:0:0:0",
                "1:0:2:0:0:0", "2:0:3:0:0:0", "1:0:3:0:0:0"};
        int index = Math.abs(userId.hashCode()) % positions.length;
        return positions[index];
    }
}
