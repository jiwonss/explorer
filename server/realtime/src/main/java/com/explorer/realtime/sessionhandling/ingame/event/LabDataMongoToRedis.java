package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.gamedatahandling.laboratory.repository.ElementLaboratoryRepository;
import com.explorer.realtime.global.mongo.repository.LaboratoryDataMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LabDataMongoToRedis {

    private final LaboratoryDataMongoRepository laboratoryDataMongoRepository;
    private final ElementLaboratoryRepository elementLaboratoryRepository;
//    private final MapDataMongoToRedis mapDataMongoToRedis;

    public Mono<Void> process(String channelId) {
        log.info("init process");
        return elementLaboratoryRepository.exist(channelId)
                .flatMap(isExist -> {
                    log.info("isExist {}", isExist);
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("process start");
//                    mapDataMongoToRedis.process(channelId).subscribe();
                    findMongoData(channelId, "element").subscribe();
                    findMongoData(channelId, "compound").subscribe();
                    return Mono.empty();
                }))
                .then();
    }

    private Mono<Void> saveToRedis(String key, List<Integer> itemCntList) {
        return elementLaboratoryRepository.save(key, itemCntList);
    }

    private Mono<Void> findMongoData(String channelId, String itemCategory) {
        log.info("mongo {}", laboratoryDataMongoRepository.findByChannelIdAndItemCategory(channelId, itemCategory).subscribe());
        return laboratoryDataMongoRepository.findByChannelIdAndItemCategory(channelId, itemCategory)
                .flatMap(laboratory -> {
                    String key = "labData:" + channelId + ":" + 0 + ":" + itemCategory;
                    log.info("dataSave");
                    return saveToRedis(key, laboratory.getItemCnt());
//                    return Mono.empty();
                });
    }
}
