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
@Document(collection = "upgradeMaterials")
public class UpgradeMaterial {

    @Id
    private String _id;

    private int level;

    private List<MaterialItemInfo> materialList;

    public static UpgradeMaterial from(int level, List<MaterialItemInfo> materialList) {
        return UpgradeMaterial.builder()
                .level(level)
                .materialList(materialList)
                .build();
    }


}
