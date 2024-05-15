package com.explorer.realtime.staticdatahandling.document;

import com.explorer.realtime.staticdatahandling.dto.ItemInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Document(collection = "items")
public class Item {

    @Id
    private String _id;

    private String category;

    private List<ItemInfo> itemList;

    public static Item from(String category, List<ItemInfo> itemList) {
        return Item.builder()
                .category(category)
                .itemList(itemList)
                .build();
    }

}
