package com.tasks.store.model;

import lombok.Data;

import java.util.UUID;

@Data
public class SaleDto {

    private UUID id;

    private CreateItemDto item;

    private Long quantitySold;

}
