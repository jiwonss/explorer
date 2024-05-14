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
@Document(collection = "extractionMaterials")
public class ExtractionMaterial {

    @Id
    private String _id;

    private int id;

    private List<MaterialItemInfo> materialList;

    public static ExtractionMaterial from(int id, List<MaterialItemInfo> materialList) {
        return ExtractionMaterial.builder()
                .id(id)
                .materialList(materialList)
                .build();
    }

}
