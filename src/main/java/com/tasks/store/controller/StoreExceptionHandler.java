package com.tasks.store.controller;

import com.tasks.store.error.InsufficientStockException;
import com.tasks.store.error.ItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class StoreExceptionHandler {

    @Autowired
    private Clock clock;

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public StoreErrorResponse handleInsufficientStockException(InsufficientStockException e) {
        log.error("InsufficientStockException: {}", e.getMessage(), e);
        return new StoreErrorResponse(LocalDateTime.now(clock), e.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public StoreErrorResponse handleItemNotFoundException(ItemNotFoundException e) {
        log.error("ItemNotFoundException: {}", e.getMessage(), e);
        return new StoreErrorResponse(LocalDateTime.now(clock), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public StoreErrorResponse handleGenericException(Exception e) {
        String exceptionId = UUID.randomUUID().toString();
        log.error("Exception {}: {}", exceptionId, e.getMessage(), e);
        return new StoreErrorResponse(LocalDateTime.now(clock), "Technical error " + exceptionId);
    }
}

