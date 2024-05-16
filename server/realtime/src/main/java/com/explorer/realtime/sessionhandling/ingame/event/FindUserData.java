package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.gamedatahandling.component.common.mapinfo.repository.CurrentMapRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Multicasting;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.waitingroom.dto.UserInfo;
import com.explorer.realtime.sessionhandling.waitingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindUserData {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final Unicasting unicasting;
    private final Multicasting multicasting;
    private final CurrentMapRepository currentMapRepository;

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        log.info("findUserdata start");
        Long userId = json.getLong("userId");
        findAllUserInfoByChannelId(channelId, userId).subscribe();
        position(channelId, userId).subscribe();
        return Mono.empty();
    }

    private Mono<List<UserInfo>> findAllUserInfoByChannelId(String channelId, Long userId) {
        log.info("userfind");
        return channelRepository.findAllFields(channelId)
                .flatMap(id -> {
                    if (!Long.valueOf(String.valueOf(id)).equals(userId)) {
                        log.info("in flatMap");
                        return userRepository.findAll(Long.valueOf(String.valueOf(id)))
                                .map(userInfo -> UserInfo.of(
                                        Long.valueOf(String.valueOf(id)),
                                        (String) userInfo.get("nickname"),
                                        Integer.parseInt(String.valueOf(userInfo.get("avatar")))
                                ));
                    } else {
                        return Mono.empty();
                    }
                }).collectList()
                .flatMap(userInfoList -> {
                    Map<String, Object> unicastMap = new HashMap<>();
                    unicastMap.put("positions", userInfoList);
                    log.info("userInfo {}", userInfoList);
                    unicasting.unicasting(channelId, userId, MessageConverter.convert(Message.success("ingameUserInfo", CastingType.UNICASTING, unicastMap))).subscribe();
                    return Mono.empty();
                });
    }

    private Mono<Void> position(String channelId, Long userId) {
        Map<String, Object> map = new HashMap<>();
        return currentMapRepository.findMapId(channelId)
                .flatMap(mapId -> {
                    map.put("mapId", mapId);
                    map.put("position", "1:0:1");
                    return userRepository.findAvatarAndNickname(userId)
                            .flatMap(userDetail -> {
                                map.putAll(userDetail);
                                unicasting.unicasting(channelId, userId, MessageConverter.convert(Message.success("newUserInfo", CastingType.UNICASTING, map))).subscribe();
                                multicasting.multicasting(channelId, String.valueOf(userId), MessageConverter.convert(Message.success("newUserInfo", CastingType.MULTICASTING, map))).subscribe();
                                return Mono.empty();
                            });
                });
    }
}
