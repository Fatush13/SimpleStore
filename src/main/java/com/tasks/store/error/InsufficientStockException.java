package com.tasks.store.error;

import java.util.UUID;

public class InsufficientStockException extends RuntimeException{

    public InsufficientStockException(UUID id) {
        super("Insufficient stock for item with id" + id);
    }

}
