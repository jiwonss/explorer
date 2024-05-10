package com.explorer.realtime.gamedatahandling.inventory.dto;

import com.explorer.realtime.gamedatahandling.farming.dto.InventoryInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class InventoryResponse {

    private InventoryInfo fromInventory;
    private InventoryInfo toInventory;

    public static InventoryResponse of(InventoryInfo fromInventory, InventoryInfo toInventory) {
        return InventoryResponse.builder()
                .fromInventory(fromInventory)
                .toInventory(toInventory)
                .build();
    }

}
