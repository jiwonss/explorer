package com.explorer.realtime.staticdatahandling.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@AllArgsConstructor
@Document(collection = "farmableCategories")
public class FarmableCategory {

    @Id
    private String _id;

    private String category;

    public static FarmableCategory from(String category) {
        return FarmableCategory.builder()
                .category(category)
                .build();
    }

}
