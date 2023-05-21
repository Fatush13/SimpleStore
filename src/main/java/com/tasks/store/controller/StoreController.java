package com.tasks.store.controller;

import com.tasks.store.model.ItemDto;
import com.tasks.store.model.Sale;
import com.tasks.store.service.StoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/store")
@Api(value = "Store Management System")
@Validated
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/item")
    @ApiOperation(value = "Add a new item in the store")
    @ApiResponse(code = 201, message = "Item successfully created")
    public ResponseEntity<ItemDto> addItem(@Valid @RequestBody ItemDto itemDto) {
        return new ResponseEntity<>(storeService.addItem(itemDto), HttpStatus.CREATED);
    }

    @PutMapping("/item/{itemId}")
    @ApiOperation(value = "Update an existing item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated item"),
            @ApiResponse(code = 404, message = "The item you were trying to reach is not found")
    })
    public ResponseEntity<ItemDto> updateItem(
            @ApiParam(value = "Item Id to update an item", required = true) @PathVariable UUID itemId,
            @RequestBody ItemDto itemDto) {
        return new ResponseEntity<>(storeService.updateItem(itemId, itemDto), HttpStatus.OK);
    }

    @DeleteMapping("/item/{itemId}")
    @ApiOperation(value = "Delete an item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted item"),
            @ApiResponse(code = 404, message = "The item you were trying to reach is not found")
    })
    public ResponseEntity<Void> deleteItem(
            @ApiParam(value = "Item Id to delete an item", required = true) @PathVariable UUID itemId) {
        storeService.deleteItem(itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/item/{itemId}")
    @ApiOperation(value = "Get details of an item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved item"),
            @ApiResponse(code = 404, message = "The item you were trying to reach is not found")
    })
    public ResponseEntity<ItemDto> getItem(
            @ApiParam(value = "Item Id to retrieve an item", required = true) @PathVariable UUID itemId) {
        return new ResponseEntity<>(storeService.getItem(itemId), HttpStatus.OK);
    }

    @GetMapping("/items")
    @ApiOperation(value = "View a list of items from the range", response = Page.class)
    public ResponseEntity<Page<ItemDto>> getAllItems(Pageable pageable) {
        return new ResponseEntity<>(storeService.getAllItems(pageable), HttpStatus.OK);
    }

    @PostMapping("/item/{itemId}/sale")
    @ApiOperation(value = "Sell an item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully sold the item"),
            @ApiResponse(code = 404, message = "The item you were trying to reach is not found"),
            @ApiResponse(code = 400, message = "Insufficient stock for the item")
    })
    public ResponseEntity<Void> sellItem(
            @ApiParam(value = "Item Id to sell item", required = true) @PathVariable UUID itemId,
            @ApiParam(value = "Quantity of the item to sell", required = true) @RequestParam long quantity) {
        storeService.sellItem(itemId, quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/item/{itemId}/sales")
    @ApiOperation(value = "View sell operations for a specific item", response = Page.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of sold items"),
            @ApiResponse(code = 404, message = "The item you were trying to reach is not found")
    })
    public ResponseEntity<Page<Sale>> getSoldItems(
            @ApiParam(value = "Item Id to get sell operations for", required = true) @PathVariable UUID itemId,
            Pageable pageable) {
        return new ResponseEntity<>(storeService.getSoldItems(itemId, pageable), HttpStatus.OK);
    }

    @GetMapping("/item/{itemId}/stock")
    @ApiOperation(value = "View current stock level for a specific item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved stock quantity"),
            @ApiResponse(code = 404, message = "The item you were trying to reach is not found")
    })
    public ResponseEntity<Long> getStockQuantity(
            @ApiParam(value = "Item Id to get stock quantity for", required = true) @PathVariable UUID itemId) {
        return new ResponseEntity<>(storeService.getStockQuantity(itemId), HttpStatus.OK);
    }

}
