package com.explorer.realtime.channeldatahandling.event;

import com.explorer.realtime.channeldatahandling.service.ChannelService;
import com.explorer.realtime.channeldatahandling.service.DeleteMongoService;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteChannel {

    private final ChannelService channelService;
    private final DeleteMongoService deleteMongoService;
    private final Unicasting unicasting;

    private static final String eventName = "deleteChannel";

    public Mono<Void> process(JSONObject json, Connection connection) {
        Long userId = json.getLong("userId");
        String channelId = json.getString("channelId");
        log.info("[process] userId : {}, channelId : {}, connection : {}", userId, channelId, connection);

        return channelService.countPlayerListByChannelId(channelId)
                .flatMap(playerListCnt -> {
                    log.info("[process] playerListCnt : {}", playerListCnt);

                    if (playerListCnt > 1) {
                        return channelService.deleteUserInfoByChannelId(channelId, userId)
                                .then(deleteMongoService.deleteInventoryByChannelIdAndUserId(channelId, userId));
                    } else {
                        return channelService.deleteChannelByChannelId(channelId)
                                .then(deleteMongoService.deleteInventoryByChannelIdAndUserId(channelId, userId))
                                .then(deleteMongoService.deleteLaboratoryByChannelId(channelId))
                                .then(deleteMongoService.deleteMapDataByChannelId(channelId));
                    }
                })
                .then(unicasting.unicasting(
                        connection,
                        userId,
                        MessageConverter.convert(Message.success(eventName, CastingType.UNICASTING))
                ))
                .then();
    }

}
