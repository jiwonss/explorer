package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository.InventoryRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.component.session.SessionManager;
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
import reactor.netty.Connection;

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
    private final SessionManager sessionManager;

    public Mono<Void> process(JSONObject json, Connection connection) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        saveInventory(channelId, userId).subscribe();
        position(channelId, userId, connection).subscribe();
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
                    inventoryDataMongoRepository.save(inventory).subscribe();
                    unicasting.unicasting(channelId, userId, MessageConverter.convert(Message.success("userInventory", CastingType.UNICASTING, inventory))).subscribe();
                    return Mono.empty();
                });
    }

    private Mono<Void> position(String channelId, Long userId, Connection connection) {
        sessionManager.setConnection(userId, connection);
        String position = getNewPosition(userId);
        return userRepository.findAvatarAndNickname(userId)
                .map(userDetail -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", userId);
                    map.put("mapId", 1);
                    map.put("nickname", userDetail.get("nickname"));
                    map.put("avatar", userDetail.get("avatar"));
                    map.put("position", position);
                    unicasting.unicasting(channelId, userId, MessageConverter.convert(Message.success("getUserPosition", CastingType.UNICASTING, map))).subscribe();
                    return Mono.empty();
                }).then();
    }

    private String getNewPosition(Long userId) {
        String[] positions = {"1:0:1", "2:0:2", "3:0:3", "1:0:2", "2:0:3", "1:0:3"};
        int index = Math.abs(userId.hashCode()) % positions.length;
        return positions[index];
    }
}
