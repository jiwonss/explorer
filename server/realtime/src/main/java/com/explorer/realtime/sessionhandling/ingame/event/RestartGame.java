package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.CurrentMapRepository;
import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
import com.explorer.realtime.gamedatahandling.component.personal.playerInfo.event.SetInitialPlayerInfo;
import com.explorer.realtime.gamedatahandling.laboratory.repository.ElementLaboratoryRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Multicasting;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.exception.WaitingRoomErrorCode;
import com.explorer.realtime.sessionhandling.waitingroom.exception.WaitingRoomException;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class RestartGame {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final ChannelRepository channelRepository;
    private final Unicasting unicasting;
    private final Multicasting multicasting;
    private final LabDataMongoToRedis labDataMongoToRedis;
    private final SetInitialPlayerInfo setInitialPlayerInfo;
    private final InventoryDataMongoToRedis inventoryDataMongoToRedis;
    private final MapObjectRepository mapObjectRepository;
    private final CurrentMapRepository currentMapRepository;

    public Mono<Void> process(String channelId, UserInfo userInfo, Connection connection) {
        // 사용자 정보를 Redis에 저장
        log.info("restart initial");
//        (existChannel(channelId))
        labDataMongoToRedis.process(channelId, "element").subscribe();
        labDataMongoToRedis.process(channelId, "compound").subscribe();
        inventoryDataMongoToRedis.process(channelId, userInfo.getUserId()).subscribe();
        createConnectionInfo(channelId, userInfo, connection).subscribe();
        userRepository.save(userInfo, channelId, "1").subscribe();
        channelRepository.save(channelId, userInfo.getUserId(), 0).subscribe();
//        setInitialPlayerInfo.process(channelId, 8).subscribe();
        return Mono.empty();
    }

    private Mono<Map<String, Object>> createConnectionInfo(String channelId, UserInfo userInfo, Connection connection) {
        sessionManager.setConnection(userInfo.getUserId(), connection);
        setInitialPlayerInfo.process(channelId, 8).subscribe();
        Map<String, Object> map = new HashMap<>();
        return currentMapRepository.findMapId(channelId)
                .flatMap(field -> {
                    Integer mapId = Integer.parseInt(String.valueOf(field));
                    return mapObjectRepository.findMapData(channelId, mapId)
                            .flatMap(mapData -> {
                                map.put("mapId", mapId);
                                map.put("mapData", mapData);
                                unicasting.unicasting(channelId, userInfo.getUserId(), MessageConverter.convert(Message.success("restartGame", CastingType.UNICASTING, map))).subscribe();
                                return Mono.just(map);
                            });
                })
                .switchIfEmpty(Mono.defer(() -> {
                    Integer mapId = 1;
                    return mapObjectRepository.findMapData(channelId, 1)
                            .flatMap(mapData -> {
                                map.put("mapId", mapId);
                                map.put("mapData", mapData);
                                unicasting.unicasting(channelId, userInfo.getUserId(), MessageConverter.convert(Message.success("restartGame", CastingType.UNICASTING, map))).subscribe();
                                return Mono.just(map);
                            });
                }));
    }

}

