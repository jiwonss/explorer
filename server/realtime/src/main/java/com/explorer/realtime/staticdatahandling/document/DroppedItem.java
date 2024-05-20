package com.explorer.realtime.staticdatahandling.document;

import com.explorer.realtime.staticdatahandling.dto.DroppedItemInfo;
import com.explorer.realtime.staticdatahandling.dto.ItemInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Document(collection = "droppedItems")
public class DroppedItem {

    @Id
    private String _id;

    private String category;

    private int id;

    private List<DroppedItemInfo> itemList;

    public static DroppedItem from(String category, int id, List<DroppedItemInfo> itemList) {
        return DroppedItem.builder()
                .category(category)
                .id(id)
                .itemList(itemList)
                .build();
    }

}
