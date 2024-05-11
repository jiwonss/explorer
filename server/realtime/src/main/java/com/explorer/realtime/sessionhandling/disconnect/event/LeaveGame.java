package com.explorer.realtime.sessionhandling.disconnect.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.MapObjectRepository;
import com.explorer.realtime.gamedatahandling.component.personal.playerInfo.repository.PlayerInfoRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.component.session.SessionManager;
import com.explorer.realtime.global.mongo.entity.MapData;
import com.explorer.realtime.global.mongo.entity.PositionData;
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

    public Mono<Void> process(String channelId, Long userId) {
        log.info("Leave game");
        List<Integer> mapIds = Arrays.asList(1, 2, 3);
        Map<String, String> map = new HashMap<>();
        map.put("userId", String.valueOf(userId));

        return Flux.fromIterable(mapIds)
                .flatMap(mapId -> mapObjectRepository.findMapData(channelId, mapId)
                        .switchIfEmpty(Mono.defer(() -> {
                            log.warn("No data found");
                            return Mono.empty();
                        }))
                        .doOnNext(data -> log.info("data found for mapId {}: {}", mapId, data))
                        .doOnError(error -> log.error("error {} : {}", mapId, error))
                        .flatMap(data -> {
                            MapData mapData = new MapData();
                            mapData.setChannelId(channelId);
                            mapData.setMapId(mapId);
                            List<PositionData> positions = new ArrayList<>();
log.info("Leave game start {}", positions);
                            data.forEach((position, value) -> {
                                String[] parts = value.split(":");
                                String itemCategory = parts[0];
                                String isFarmable = parts[1];
                                Integer itemId = Integer.parseInt(parts[2]);
                                positions.add(new PositionData(position, itemCategory, isFarmable, itemId));
                            });

                            mapData.setPositions(positions);
                            log.info("Leave game end {}", mapData);
                            return mapDataMongoRepository.save(mapData);
                        }))
                .then(userCount(channelId))
                .flatMap(count -> {
                            if (count == 1) {
                                log.info("Only one user in channel {}", channelId);
                                deleteData(channelId, userId).subscribe();
                                deleteUserData(channelId, userId).subscribe();
                            } else {
                                log.info("More than one user in channel {}", channelId);
                                deleteData(channelId, userId).subscribe();
                                return broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("leaveGame", CastingType.BROADCASTING, map)));
                            }
                            return Mono.empty();
                        }
                );
    }

    private Mono<Long> userCount(String channelId) {
        return channelRepository.count(channelId);
    }

    private Mono<Boolean> deleteData(String channelId, Long userId) {
        playerInfoRepository.deleteUserChannelInfo(channelId, userId).subscribe();
        userRepository.delete(userId).subscribe();
        channelRepository.deleteByUserId(channelId, userId).subscribe();
        return Mono.empty();
    }

    private Mono<Void> deleteUserData(String channelId, Long userId) {
        channelRepository.deleteAll(channelId).subscribe();
        mapObjectRepository.deleteAllMap(channelId).subscribe();
        sessionManager.removeConnection(userId);
        return Mono.empty();
    }
}
