package com.explorer.realtime.staticdatahandling.event;

import com.explorer.realtime.staticdatahandling.dto.DroppedItemInfo;
import com.explorer.realtime.staticdatahandling.dto.ItemInfo;
import com.explorer.realtime.staticdatahandling.dto.MaterialItemInfo;
import com.explorer.realtime.staticdatahandling.repository.redis.*;
import com.explorer.realtime.staticdatahandling.service.MongoService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveStaticDataToRedis {

    private final MongoService mongoService;
    private final DroppedItemRepository droppedItemRepository;
    private final ExtractionMaterialRepository extractionMaterialRepository;
    private final FarmableCategoryRepository farmableCategoryRepository;
    private final LabEfficiencyRepository labEfficiencyRepository;
    private final StaticItemRepository staticItemRepository;
    private final SynthesizedMaterialRepository synthesizedMaterialRepository;
    private final UpgradeMaterialRepository upgradeMaterialRepository;

    @PostConstruct
    public void init() {
        log.info("[init] mongoDB to redis");
        process()
                .doOnError(error -> log.error("[process] Error processing static data", error))
                .subscribe();
    }

    public Mono<Void> process() {
        return Mono.when(
                        saveDroppedItemList(),
                        saveExtractionMaterialList(),
                        saveFarmableCategoryList(),
                        saveLabEfficiencyList(),
                        saveItemList(),
                        saveSynthesizedMaterialList(),
                        saveUpgradeMaterialList()
                )
                .doOnError(error -> log.error("[process] Error processing static data", error))
                .then();
    }

    private Mono<Void> saveDroppedItemList() {
        return mongoService.findAllDroppedItem()
                .flatMap(droppedItem -> {
                    List<DroppedItemInfo> droppedItemList = droppedItem.getItemList();
                    return Flux.fromIterable(droppedItemList)
                            .flatMap(item -> droppedItemRepository.save(droppedItem.getCategory(), droppedItem.getId(), item));
                })
                .then();
    }

    private Mono<Void> saveExtractionMaterialList() {
        return mongoService.findAllExtractionMaterial()
                .flatMap(extractionMaterial -> {
                    List<MaterialItemInfo> materialList = extractionMaterial.getMaterialList();
                    return Flux.fromIterable(materialList)
                            .flatMap(item -> extractionMaterialRepository.save(extractionMaterial.getId(), item));
                })
                .then();
    }

    private Mono<Void> saveFarmableCategoryList() {
        return mongoService.findAllFarmableCategory()
                .flatMap(farmableCategory -> farmableCategoryRepository.save(farmableCategory.getCategory()))
                .then();
    }

    private Mono<Void> saveLabEfficiencyList() {
        return mongoService.findAllLabEfficiency()
                .flatMap(labEfficiency -> labEfficiencyRepository.save(labEfficiency.getLevel(), labEfficiency.getEfficiency()))
                .then();
    }

    private Mono<Void> saveItemList() {
        return mongoService.findAllItem()
                .flatMap(staticItem -> {
                    List<ItemInfo> itemList = staticItem.getItemList();
                    return Flux.fromIterable(itemList)
                            .flatMap(item -> staticItemRepository.save(staticItem.getCategory(), item));
                })
                .then();
    }

    private Mono<Void> saveSynthesizedMaterialList() {
        return mongoService.findAllSynthesizedMaterial()
                .flatMap(synthesizedMaterial -> {
                    List<MaterialItemInfo> materialList = synthesizedMaterial.getMaterialList();
                    return Flux.fromIterable(materialList)
                            .flatMap(item -> synthesizedMaterialRepository.save(synthesizedMaterial.getId(), item));
                })
                .then();
    }

    private Mono<Void> saveUpgradeMaterialList() {
        return mongoService.findAllUpgradeMaterial()
                .flatMap(upgradeMaterial -> {
                    List<MaterialItemInfo> materialList = upgradeMaterial.getMaterialList();
                    return Flux.fromIterable(materialList)
                            .flatMap(item -> upgradeMaterialRepository.save(upgradeMaterial.getLevel(), item));
                })
                .then();
    }

}
