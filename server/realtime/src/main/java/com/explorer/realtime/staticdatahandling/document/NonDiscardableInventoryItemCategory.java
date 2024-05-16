package com.explorer.realtime.staticdatahandling.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@AllArgsConstructor
@Document(collection = "nonDiscardableInventoryItemCategories")
public class NonDiscardableInventoryItemCategory {

    @Id
    private String _id;

    private String category;

    public static NonDiscardableInventoryItemCategory from (String category) {
        return NonDiscardableInventoryItemCategory.builder()
                .category(category)
                .build();
    }

}
