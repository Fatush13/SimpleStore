package com.tasks.store.controller;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class StoreErrorResponse {

    private LocalDateTime timestamp;
    String message;

}
