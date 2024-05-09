package com.explorer.realtime.gamedatahandling.component.common.boxinfo.event;

import com.explorer.realtime.gamedatahandling.component.common.boxinfo.dto.UserConnectionInfo;
import com.explorer.realtime.gamedatahandling.component.common.boxinfo.repository.UserInventoryRepository;
import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class BoxInstall {

    private final MapObjectRepository mapObjectRepository;
    private final UserInventoryRepository userInventoryRepository;
    private final Unicasting unicasting;
    private final Broadcasting broadcasting;

    public Mono<Void> process(JSONObject json) {
        log.info("boxInstall process");
        UserConnectionInfo userConnectionInfo = UserConnectionInfo.of(json);
        int inventoryIdx = json.getInt("inventoryIdx");
        String itemCategory = "installation";
        String position = json.getString("position");
        String channelId = userConnectionInfo.getChannelId();
        Long userId = userConnectionInfo.getUserId();
        // 인벤토리 idx에 위치한 itmeid값과 일치 하는지 점검
        userInventoryRepository.findInventoryItem(channelId, userId, inventoryIdx)
                .switchIfEmpty(Mono.fromRunnable(() -> {
                    log.warn("No inventory item userId: {} and inventoryIdx: {}", userId, inventoryIdx);
                    unicasting.unicasting(channelId, userId, MessageConverter.convert(Message.fail("boxInstall", CastingType.UNICASTING, "No inventory item found"))).subscribe();
                }))
                        .flatMap(inventoryItemInfo -> {
                            log.info("itemId {}", inventoryItemInfo.getItemId());
                            log.info("userId {}", userId);
                            if (inventoryItemInfo.getItemId() == 0 && itemCategory.equals(inventoryItemInfo.getItemCategory())) {

                                mapObjectRepository.save(channelId, 1, position, "installation", 0).subscribe();
                                log.info("map save");
                                userInventoryRepository.deleteInventoryItem(channelId, userId, inventoryIdx).subscribe();
                                broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("boxInstall", CastingType.BROADCASTING, position + ":" + "box" + ":" + 0))).subscribe();
                            } else {
                                unicasting.unicasting(channelId, userId, MessageConverter.convert(Message.fail("boxInstall", CastingType.UNICASTING))).subscribe();
                                log.info("error");
                            }
                            return Mono.empty();
                        }).subscribe();

        return Mono.empty();
    }

}
