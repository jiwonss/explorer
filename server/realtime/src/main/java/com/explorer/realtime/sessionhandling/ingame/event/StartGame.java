package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Broadcasting;
import com.explorer.realtime.global.redis.ChannelRepository;
import com.explorer.realtime.global.util.MessageConverter;
import com.explorer.realtime.sessionhandling.ingame.entity.Channel;
import com.explorer.realtime.sessionhandling.ingame.repository.ChannelMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartGame {

    private final ChannelMongoRepository channelMongoRepository;
    private final ChannelRepository channelRepository;
    private final Broadcasting broadcasting;

    public Mono<Void> process(String teamCode, String channelName) {
        log.info("Processing game start for teamCode: {}", teamCode);
        String channelId = createChannelId();
        transferAndInitializeChannel(teamCode, channelId, channelName).subscribe();
        SaveChannel(teamCode, channelId, channelName);
        Map<String, String> map = new HashMap<>();
        map.put("channelId", channelId);

        broadcasting.broadcasting(channelId, MessageConverter.convert(Message.success("startGame", CastingType.BROADCASTING, map))).subscribe();

        return Mono.empty();
    }

    private String createChannelId() {
        ObjectId newChannelId = new ObjectId();
        return newChannelId.toHexString();
    }

    private Mono<Void> transferAndInitializeChannel(String teamCode, String channelId, String channelName) {
        return channelRepository.findAll(teamCode)
                .flatMapMany(entries -> Flux.fromIterable(entries.entrySet()))
                .flatMap(entry -> channelRepository.save(channelId, Long.parseLong((String) entry.getKey()), Integer.parseInt((String) entry.getValue())))
                .then(channelRepository.deleteAll(teamCode))
                .then()
                .doOnSuccess(unused -> SaveChannel(teamCode, channelId, channelName));
    }

    private Mono<Void> SaveChannel(String teamCode, String channelId, String channelName) {
        return channelRepository.findAll(teamCode)
                .flatMapMany(entries -> Flux.fromIterable(entries.entrySet()))
                .flatMap(entry ->
                        channelRepository.save(channelId, Long.parseLong((String) entry.getKey()), Integer.parseInt((String) entry.getValue()))
                                .thenReturn(entry) // Mono<Boolean> 결과를 원본 entry로 변환
                )
                .collectList() // 모든 entries를 한번에 수집
                .flatMap(entries -> {
                    Set<Long> memberIds = entries.stream()
                            .map(entry -> Long.parseLong((String) entry.getKey())) // 각 entry의 Key를 Long으로 변환
                            .collect(Collectors.toSet()); // Set으로 변환하여 중복 제거
                    Channel channel = new Channel(channelId, channelName, memberIds); // MongoDB에 저장할 Channel 객체 생성
                    channelMongoRepository.save(channel).subscribe();
                    return Mono.empty();
                }
            );
        }
    }


