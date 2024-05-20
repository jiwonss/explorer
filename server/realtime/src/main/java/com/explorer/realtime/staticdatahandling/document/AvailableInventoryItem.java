package com.explorer.realtime.staticdatahandling.document;

import com.explorer.realtime.staticdatahandling.dto.AvailableInventoryItemInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Document(collection = "availableInventoryItems")
public class AvailableInventoryItem {

    @Id
    private String _id;

    private List<AvailableInventoryItemInfo> availableInventoryItemList;

    public static AvailableInventoryItem from(List<AvailableInventoryItemInfo> availableInventoryItemList) {
        return AvailableInventoryItem.builder()
                .availableInventoryItemList(availableInventoryItemList)
                .build();
    }

}
