package com.explorer.realtime.gamedatahandling.inventory.exception;

import lombok.Getter;

@Getter
public class InventoryException extends RuntimeException {

    private final InventoryErrorCode errorCode;

    public InventoryException(InventoryErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
