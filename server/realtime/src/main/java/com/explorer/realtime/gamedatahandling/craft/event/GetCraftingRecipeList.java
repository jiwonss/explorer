package com.explorer.realtime.gamedatahandling.craft.event;

import com.explorer.realtime.gamedatahandling.craft.dto.CraftInfo;
import com.explorer.realtime.gamedatahandling.craft.dto.CraftMaterial;
import com.explorer.realtime.gamedatahandling.craft.repository.CraftRecipeRepository;
import com.explorer.realtime.gamedatahandling.craft.repository.CraftRepository;
import com.explorer.realtime.global.common.dto.Message;
import com.explorer.realtime.global.common.enums.CastingType;
import com.explorer.realtime.global.component.broadcasting.Unicasting;
import com.explorer.realtime.global.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCraftingRecipeList {

    private final CraftRepository craftRepository;
    private final CraftRecipeRepository craftRecipeRepository;
    private final Unicasting unicasting;

    private static final String eventName = "getCraftingRecipeList";

    public Mono<Void> process(JSONObject json) {
        String channelId = json.getString("channelId");
        Long userId = json.getLong("userId");
        log.info("[process] channelId : {}, userId : {}", channelId, userId);

        return craftRepository.findAll()
                .flatMap(craft -> {
                    List<CraftInfo> craftInfoList = new ArrayList<>();
                    return Flux.fromIterable(craft.entrySet())
                            .flatMap(entry -> {
                                int craftId = Integer.parseInt(String.valueOf(entry.getKey()));
                                String[] itemInfo = entry.getValue().toString().split(":");
                                String itemCategory = itemInfo[0];
                                int itemId = Integer.parseInt(itemInfo[1]);

                                CraftInfo craftInfo = CraftInfo.of(craftId, itemCategory, itemId);
                                log.info("[process] craftInfo : {}", craftInfo);
                                craftInfoList.add(craftInfo);

                                return craftRecipeRepository.find(itemCategory, itemId)
                                        .flatMapMany(material -> Flux.fromIterable(material.entrySet()))
                                        .flatMap(materialEntry -> {
                                            String[] materialInfo = materialEntry.getKey().toString().split(":");
                                            String materialItemCategory = materialInfo[0];
                                            int materialItemId = Integer.parseInt(materialInfo[1]);
                                            int materialItemCnt = Integer.parseInt(String.valueOf(materialEntry.getValue()));

                                            CraftMaterial craftMaterial = CraftMaterial.of(materialItemCategory, materialItemId, materialItemCnt);
                                            log.info("[precess] craftMaterial : {}", craftMaterial);

                                            craftInfo.getMaterialList().add(craftMaterial);
                                            return Mono.just(craftInfo);
                                        });
                            })
                            .then(Mono.fromRunnable(() -> {
                                log.info("[process] craftInfoList : {}", craftInfoList);
                                unicasting.unicasting(
                                        channelId,
                                        userId,
                                        MessageConverter.convert(Message.success(eventName, CastingType.UNICASTING, craftInfoList))
                                ).subscribe();
                            }));
                });
    }

}
