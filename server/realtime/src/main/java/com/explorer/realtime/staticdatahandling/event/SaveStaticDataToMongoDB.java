package com.explorer.realtime.staticdatahandling.event;

import com.explorer.realtime.staticdatahandling.document.*;
import com.explorer.realtime.staticdatahandling.dto.AvailableInventoryItemInfo;
import com.explorer.realtime.staticdatahandling.dto.DroppedItemInfo;
import com.explorer.realtime.staticdatahandling.dto.ItemInfo;
import com.explorer.realtime.staticdatahandling.dto.MaterialItemInfo;
import com.explorer.realtime.staticdatahandling.repository.mongo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveStaticDataToMongoDB {

    private final AvailableInventoryItemMongoRepository availableInventoryItemMongoRepository;
    private final DroppedItemMongoRepository droppedItemMongoRepository;
    private final ExtractionMaterialMongoRepository extractionMaterialMongoRepository;
    private final FarmableCategoryMongoRepository farmableCategoryMongoRepository;
    private final InvalidInventoryItemCategoryMongoRepository invalidInventoryItemCategoryMongoRepository;
    private final ItemMongoRepository itemMongoRepository;
    private final LabEfficiencyMongoRepository labEfficiencyMongoRepository;
    private final NonDiscardableInventoryItemCategoryMongoRepository nonDiscardableInventoryItemCategoryMongoRepository;
    private final SynthesizedMaterialMongoRepository synthesizedMaterialMongoRepository;
    private final UpgradeMaterialMongoRepository upgradeMaterialMongoRepository;

    public Mono<Void> process(JSONObject json) {
        String documentName = json.getString("documentName");
        log.info("[process] documentName : {}", documentName);

        switch (documentName) {
            case "availableInventoryItem":
                setAvailableInventoryItems(json).subscribe();
                break;

            case "droppedItem":
                setDroppedItem(json).subscribe();
                break;

            case "extractionMaterial":
                setExtractionMaterial(json).subscribe();
                break;

            case "farmableCategory":
                setFarmableCategories(json).subscribe();
                break;

            case "invalidInventoryItemCategory":
                setInvalidInventoryItemCategories(json).subscribe();
                break;

            case "item":
                setItem(json).subscribe();
                break;

            case "labEfficiency":
                setLabEfficiency(json).subscribe();
                break;

            case "nonDiscardableInventoryItemCategory":
                setNonDiscardableInventoryItemCategories(json).subscribe();
                break;

            case "synthesizedMaterial":
                setSynthesizedMaterial(json).subscribe();
                break;

            case "upgradeMaterial":
                setUpgradeMaterial(json).subscribe();
                break;
        }

        return Mono.empty();
    }
    private Mono<Void> setAvailableInventoryItems(JSONObject json) {
        String[] availableItems = json.getString("availableItems").split(",");
        List<AvailableInventoryItemInfo> itemList = new ArrayList<>();
        Arrays.stream(availableItems).forEach(availableItem -> {
            AvailableInventoryItemInfo availableInventoryItemInfo = AvailableInventoryItemInfo.of(availableItem);
            itemList.add(availableInventoryItemInfo);
        });
        AvailableInventoryItem availableInventoryItem = AvailableInventoryItem.from(itemList);
        return availableInventoryItemMongoRepository.save(availableInventoryItem).then();
    }

    private Mono<Void> setDroppedItem(JSONObject json) {
        String category = json.getString("category");
        int id = json.getInt("id");
        String[] droppedItems = json.getString("droppedItems").split(",");
        List<DroppedItemInfo> itemList = new ArrayList<>();
        Arrays.stream(droppedItems).forEach(droppedItem -> {
            DroppedItemInfo droppedItemInfo = DroppedItemInfo.of(droppedItem);
            itemList.add(droppedItemInfo);
        });
        DroppedItem droppedItem = DroppedItem.from(category, id, itemList);
        return droppedItemMongoRepository.save(droppedItem).then();
    }

    private Mono<Void> setExtractionMaterial(JSONObject json) {
        int id = json.getInt("id");
        String[] materialItems = json.getString("materialItems").split(",");
        List<MaterialItemInfo> materialList = new ArrayList<>();
        Arrays.stream(materialItems).forEach(materialItem -> {
            MaterialItemInfo materialItemInfo = MaterialItemInfo.of(materialItem);
            materialList.add(materialItemInfo);
        });
        ExtractionMaterial extractionMaterial = ExtractionMaterial.from(id, materialList);
        return extractionMaterialMongoRepository.save(extractionMaterial).then();
    }

    private Mono<Void> setFarmableCategories(JSONObject json) {
        String[] categories = json.getString("categories").split(",");
        List<Mono<Void>> saveMonos = new ArrayList<>();
        Arrays.stream(categories).forEach(category -> {
            FarmableCategory farmableCategory = FarmableCategory.from(category);
            saveMonos.add(farmableCategoryMongoRepository.save(farmableCategory).then());
        });
        return Mono.when(saveMonos).then();
    }

    private Mono<Void> setInvalidInventoryItemCategories(JSONObject json) {
        String[] categories = json.getString("categories").split(",");
        List<Mono<Void>> saveMonos = new ArrayList<>();
        Arrays.stream(categories).forEach(category -> {
            InvalidInventoryItemCategory invalidInventoryItemCategory = InvalidInventoryItemCategory.from(category);
            saveMonos.add(invalidInventoryItemCategoryMongoRepository.save(invalidInventoryItemCategory).then());
        });
        return Mono.when(saveMonos).then();
    }

    private Mono<Void> setItem(JSONObject json) {
        String category = json.getString("category");
        int maxId = json.getInt("maxId");
        int maxCnt = json.getInt("maxCnt");
        List<ItemInfo> itemList = new ArrayList<>();
        for (int i = 0; i < maxId; i++) {
            ItemInfo itemInfo = ItemInfo.of(i, maxCnt);
            itemList.add(itemInfo);
        }
        Item item = Item.from(category, itemList);
        return itemMongoRepository.save(item).then();
    }

    private Mono<Void> setLabEfficiency(JSONObject json) {
        int level = json.getInt("level");
        float efficiency = json.getFloat("efficiency");
        LabEfficiency labEfficiency = LabEfficiency.from(level, efficiency);
        return labEfficiencyMongoRepository.save(labEfficiency).then();
    }

    private Mono<Void> setNonDiscardableInventoryItemCategories(JSONObject json) {
        String[] categories = json.getString("categories").split(",");
        List<Mono<Void>> saveMonos = new ArrayList<>();
        Arrays.stream(categories).forEach(category -> {
            NonDiscardableInventoryItemCategory nonDiscardableInventoryItemCategory = NonDiscardableInventoryItemCategory.from(category);
            saveMonos.add(nonDiscardableInventoryItemCategoryMongoRepository.save(nonDiscardableInventoryItemCategory).then());
        });
        return Mono.when(saveMonos).then();
    }

    private Mono<Void> setSynthesizedMaterial(JSONObject json) {
        int id = json.getInt("id");
        String[] materialItems = json.getString("materialItems").split(",");
        List<MaterialItemInfo> materialList = new ArrayList<>();
        Arrays.stream(materialItems).forEach(materialItem -> {
            MaterialItemInfo materialItemInfo = MaterialItemInfo.of(materialItem);
            materialList.add(materialItemInfo);
        });
        SynthesizedMaterial synthesizedMaterial = SynthesizedMaterial.from(id, materialList);
        return synthesizedMaterialMongoRepository.save(synthesizedMaterial).then();
    }

    private Mono<Void> setUpgradeMaterial(JSONObject json) {
        int level = json.getInt("level");
        String[] materialItems = json.getString("materialItems").split(",");
        List<MaterialItemInfo> materialList = new ArrayList<>();
        Arrays.stream(materialItems).forEach(materialItem -> {
            MaterialItemInfo materialItemInfo = MaterialItemInfo.of(materialItem);
            materialList.add(materialItemInfo);
        });
        UpgradeMaterial upgradeMaterial = UpgradeMaterial.from(level, materialList);
        return upgradeMaterialMongoRepository.save(upgradeMaterial).then();
    }


}
