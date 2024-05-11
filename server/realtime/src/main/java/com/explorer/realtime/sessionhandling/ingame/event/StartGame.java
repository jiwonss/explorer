package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.event.InitializeMapObject;
import com.explorer.realtime.gamedatahandling.component.personal.inventoryInfo.event.SetInitialInventory;
import com.explorer.realtime.gamedatahandling.component.personal.playerInfo.event.SetInitialPlayerInfo;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.ingame.document.Channel;
import com.explorer.realtime.sessionhandling.ingame.dto.UserInfo;
import com.explorer.realtime.sessionhandling.ingame.repository.ChannelMongoRepository;
import com.explorer.realtime.sessionhandling.ingame.repository.ElementLaboratoryRepository;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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

    private static final int INVENTORY_CNT = 8;

    public Mono<Void> process(String teamCode, String channelName) {
        log.info("Processing game start for teamCode: {}", teamCode);

        Mono<String> saveChannelMono = saveChannel(teamCode, channelName);
//        int mapId = 1;
        saveChannelMono.subscribe(channelId -> {
            transferAndInitializeChannel(teamCode, channelId)
                    .then(Mono.defer(() -> {
                        elementLaboratoryRepository.initialize(channelId).subscribe();
                        initializeMapObject.initializeMapObject(channelId, 2).subscribe();
                        initializeMapObject.initializeMapObject(channelId, 3).subscribe();
                        setInitialPlayerInfo.process(channelId, INVENTORY_CNT).subscribe();

                        Map<String, String> map = new HashMap<>();
                        map.put("channelId", channelId);
                        return broadcasting.broadcasting(
                                channelId,
                                MessageConverter.convert(Message.success("startGame", CastingType.BROADCASTING, map))
                        );
                    }))
                    .subscribe();
        });

        return Mono.empty();
    }

    private Mono<Void> transferAndInitializeChannel(String teamCode, String channelId) {
        return channelRepository.findAll(teamCode)
                .flatMapMany(entries -> Flux.fromIterable(entries.entrySet()))
                .flatMap(entry -> channelRepository.save(channelId, Long.parseLong(entry.getKey().toString()), Integer.parseInt(entry.getValue().toString())))
                .then(channelRepository.deleteAll(teamCode))
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
                    log.info("userInfoList : {}", userInfoList);
                    return channelMongoRepository.save(Channel.from(channelName, new HashSet<>(userInfoList)))
                            .map(Channel::getId)
                            .doOnSuccess(channelId -> {
                                log.info("channelId : {}", channelId);
                                userInfoList.forEach(userInfo -> {
                                    userRepository.updateUserData(userInfo.getUserId(), channelId, "1").subscribe();

                                });
                      });
                });
    }

}
