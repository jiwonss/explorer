package com.explorer.realtime.gamedatahandling.tool.event;

import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository.InventoryInfoRepository;
import com.explorer.realtime.gamedatahandling.component.personal.playerInfo.repository.PlayerInfoRepository;
import com.explorer.realtime.gamedatahandling.farming.dto.InventoryInfo;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachTool {

    private final PlayerInfoRepository playerInfoRepository;
    private final InventoryInfoRepository inventoryInfoRepository;
    private final Unicasting unicasting;
    private final Broadcasting broadcasting;

    private static final String eventName = "attachTool";

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        int inventoryIdx = json.getInt("inventoryIdx");
        log.info("[process] channelId : {}, userId : {}, inventoryIdx : {}", channelId, userId, inventoryIdx);

        return inventoryInfoRepository.findByInventoryIdx(channelId, userId, inventoryIdx)
                .map(Object::toString)
                .flatMap(value -> {
                    InventoryInfo inventoryInfo = InventoryInfo.ofString(inventoryIdx, value);
                    log.info("[process] inventoryInfo : {}", inventoryInfo);
                    return inventoryInfoRepository.deleteByInventoryIdx(channelId, userId, inventoryIdx)
                            .then(playerInfoRepository.saveTool(channelId, userId, inventoryInfo.getItemId()))
                            .flatMap(result -> unicasting(channelId, userId, inventoryInfo.getItemId()))
                            .then(broadcasting(channelId, userId, inventoryInfo.getItemId()));
                })
                .then();
    }

    private Mono<Void> unicasting(String channelId, Long userId, int itemId) {
        Map<String, Object> map = new HashMap<>();
        map.put("inventoryIdx", itemId);
        return unicasting.unicasting(
                channelId,
                userId,
                MessageConverter.convert(Message.success(eventName, CastingType.UNICASTING, map))
        );
    }

    private Mono<Void> broadcasting(String channelId, Long userId, int itemId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("itemCategory", "tool");
        map.put("itemId", itemId);
        return broadcasting.broadcasting(
                channelId,
                MessageConverter.convert(Message.success(eventName, CastingType.BROADCASTING, map))
        );
    }
}
