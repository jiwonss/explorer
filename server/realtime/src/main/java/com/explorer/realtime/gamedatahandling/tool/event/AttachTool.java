package com.explorer.realtime.gamedatahandling.tool.event;

import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.repository.InventoryRepository;
import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.dto.InventoryInfo;
import com.explorer.realtime.gamedatahandling.tool.exception.ToolErrorCode;
import com.explorer.realtime.gamedatahandling.tool.exception.ToolException;
import com.explorer.realtime.gamedatahandling.tool.repository.ToolRepository;
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
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachTool {

    private final InventoryRepository inventoryRepository;
    private final ToolRepository toolRepository;
    private final Unicasting unicasting;
    private final Broadcasting broadcasting;

    private static final String eventName = "attachTool";

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        int inventoryIdx = json.getInt("inventoryIdx");
        log.info("[process] channelId : {}, userId : {}, inventoryIdx : {}", channelId, userId, inventoryIdx);

        return inventoryRepository.findByInventoryIdx(channelId, userId, inventoryIdx)
                .map(Object::toString)
                .flatMap(value -> {
                    log.info("[process] value : {}", value);

                    if (value.isEmpty()) {
                        return Mono.error(new ToolException(ToolErrorCode.EMPTY_INVENTORY));
                    }

                    InventoryInfo inventoryInfo = InventoryInfo.ofString(inventoryIdx, value);
                    if (!inventoryInfo.getItemCategory().equals("tool")) {
                        return Mono.error(new ToolException(ToolErrorCode.NOT_TOOL));
                    }

                    log.info("[process] inventoryInfo : {}", inventoryInfo);

                    AtomicInteger preInventoryIdx = new AtomicInteger(-1);
                    return toolRepository.find(channelId, userId)
                            .flatMap(info -> {
                                if (info != null) {
                                    String[] toolInfo = String.valueOf(info).split(":");
                                    preInventoryIdx.set(Integer.parseInt(toolInfo[0]));
                                }
                                return Mono.empty();
                            })
                            .then(Mono.just(preInventoryIdx.get()))
                            .flatMap(preIdx -> {
                                log.info("[process] inventoryInfo : {}", inventoryInfo);
                                return toolRepository.save(channelId, userId, inventoryIdx, inventoryInfo.getItemId())
                                        .then(unicasting(channelId, userId, inventoryIdx, preIdx))
                                        .then(broadcasting(channelId, userId, inventoryInfo.getItemId()));
                            });
                })
                .onErrorResume(ToolException.class, error -> {
                    log.info("[process] errorCode : {}, errorMessage : {}", error.getErrorCode(), error.getMessage());
                    unicasting.unicasting(
                            channelId,
                            userId,
                            MessageConverter.convert(Message.fail(eventName, CastingType.UNICASTING, String.valueOf(error.getErrorCode()), error.getMessage()))
                    ).subscribe();
                    return Mono.empty();
                })
                .then();
    }

    private Mono<Void> unicasting(String channelId, Long userId, int inventoryIdx, int preInventoryIdx) {
        Map<String, Object> map = new HashMap<>();
        map.put("inventoryIdx", inventoryIdx);
        map.put("preInventoryIdx", preInventoryIdx);
        log.info("[unicasting] map : {}", map);

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
        log.info("[broadcasting] map : {}", map);

        return broadcasting.broadcasting(
                channelId,
                MessageConverter.convert(Message.success(eventName, CastingType.BROADCASTING, map))
        );
    }
}
