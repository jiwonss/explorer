package com.explorer.realtime.staticdatahandling.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@AllArgsConstructor
@Document(collection = "invalidInventoryItemCategories")
public class InvalidInventoryItemCategory {

    @Id
    private String _id;

    private String category;

    public static InvalidInventoryItemCategory from (String category) {
        return InvalidInventoryItemCategory.builder()
                .category(category)
                .build();
    }

}
