package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.global.mongo.repository.LaboratoryLevelMongoRepository;
import com.explorer.realtime.sessionhandling.ingame.repository.LaboratoryLevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class LabLevelDataMongoToRedis {

    private final LaboratoryLevelMongoRepository laboratoryLevelMongoRepository;
    private final LaboratoryLevelRepository laboratoryLevelRepository;

    public Mono<Void> process(String channelId) {
        log.info("level data");
        return laboratoryLevelMongoRepository.findByChannelId(channelId)
                .flatMap(levelData -> {
                    String level = levelData.getLevel();
                    return laboratoryLevelRepository.save(channelId, level);
                }).then();
    }
}
