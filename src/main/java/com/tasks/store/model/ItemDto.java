package com.tasks.store.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ItemDto {

    private UUID id;

    private String name;

    private BigDecimal price;

    private Long quantity;

}
