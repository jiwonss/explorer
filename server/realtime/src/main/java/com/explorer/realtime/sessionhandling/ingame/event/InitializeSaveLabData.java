package com.explorer.realtime.sessionhandling.ingame.event;

import com.explorer.realtime.gamedatahandling.farming.dto.InventoryInfo;
import com.explorer.realtime.gamedatahandling.laboratory.repository.ElementLaboratoryRepository;
import com.explorer.realtime.global.mongo.entity.Laboratory;
import com.explorer.realtime.global.mongo.repository.LaboratoryDataMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitializeSaveLabData {

    private final ElementLaboratoryRepository elementLaboratoryRepository;
    private final LaboratoryDataMongoRepository laboratoryDataMongoRepository;

    public Mono<Void> process(String channelId) {
        log.info("lab data");
        return saveElementData(channelId)
                .then(saveCompoundData(channelId))
                .then();
    }

    private Mono<Boolean> saveElementData(String channelId) {
        return elementLaboratoryRepository.findElementData(channelId)
                .flatMap(itemCntList -> {
                    Laboratory laboratory = new Laboratory();
                    laboratory.setChannelId(channelId);
                    laboratory.setLabId(0);
                    laboratory.setItemCategory("element");
                    laboratory.setItemCnt(itemCntList);
                    laboratoryDataMongoRepository.save(laboratory).subscribe();
                    return Mono.empty();
                });
    }

    private Mono<Boolean> saveCompoundData(String channelId) {
        return elementLaboratoryRepository.findCompoundData(channelId)
                .flatMap(itemCntList -> {
                    Laboratory laboratory = new Laboratory();
                    laboratory.setChannelId(channelId);
                    laboratory.setLabId(0);
                    laboratory.setItemCategory("compound");
                    laboratory.setItemCnt(itemCntList);
                    laboratoryDataMongoRepository.save(laboratory).subscribe();
                    return Mono.empty();
                });
    }
}
