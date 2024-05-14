package com.explorer.realtime.staticdatahandling.document;

import com.explorer.realtime.staticdatahandling.dto.MaterialItemInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Document(collection = "synthesizedMaterials")
public class SynthesizedMaterial {

    @Id
    private String _id;

    private int id;

    private List<MaterialItemInfo> materialList;

    public static SynthesizedMaterial from(int id, List<MaterialItemInfo> materialList) {
        return SynthesizedMaterial.builder()
                .id(id)
                .materialList(materialList)
                .build();
    }

}
