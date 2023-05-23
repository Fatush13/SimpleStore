package com.tasks.store.controller;

import com.tasks.store.model.CreateItemDto;
import com.tasks.store.model.ItemDto;
import com.tasks.store.model.SaleDto;
import com.tasks.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/store")
@Tag(name = "Store Management System")
@Validated
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/item")
    @Operation(summary = "Add a new item in the store",
            responses = {@ApiResponse(responseCode = "201", description = "Item successfully created")})
    public ResponseEntity<ItemDto> addItem(@Valid @RequestBody CreateItemDto createItemDto) {
        return new ResponseEntity<>(storeService.addItem(createItemDto), HttpStatus.CREATED);
    }

    @PutMapping("/item/{itemId}")
    @Operation(summary = "Update an existing item",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated item"),
                    @ApiResponse(responseCode = "404", description = "The item you were trying to reach is not found")
            })
    public ResponseEntity<ItemDto> updateItem(
            @Parameter(description = "Item Id to update an item", required = true) @PathVariable UUID itemId,
            @RequestBody CreateItemDto createItemDto) {
        return new ResponseEntity<>(storeService.updateItem(itemId, createItemDto), HttpStatus.OK);
    }

    @DeleteMapping("/item/{itemId}")
    @Operation(summary = "Delete an item",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted item"),
                    @ApiResponse(responseCode = "404", description = "The item you were trying to reach is not found")
            })
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "Item Id to delete an item", required = true) @PathVariable UUID itemId) {
        storeService.deleteItem(itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/item/{itemId}")
    @Operation(summary = "Get details of an item",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved item"),
                    @ApiResponse(responseCode = "404", description = "The item you were trying to reach is not found")
            })
    public ResponseEntity<ItemDto> getItem(
            @Parameter(description = "Item Id to retrieve an item", required = true) @PathVariable UUID itemId) {
        return new ResponseEntity<>(storeService.getItem(itemId), HttpStatus.OK);
    }

    @GetMapping("/items")
    @Operation(summary = "View a list of items from the range",
            responses = {@ApiResponse(responseCode = "200", description = "Successfully retrieved items")})
    public ResponseEntity<Page<ItemDto>> getAllItems(Pageable pageable) {
        return new ResponseEntity<>(storeService.getAllItems(pageable), HttpStatus.OK);
    }

    @PostMapping("/item/{itemId}/sale")
    @Operation(summary = "Sell an item",
            responses = {
                    @ApiResponse(responseCode = "404", description = "The item you were trying to reach is not found"),
                    @ApiResponse(responseCode = "400", description = "Insufficient stock for the item")
            })
    public ResponseEntity<Void> sellItem(
            @Parameter(description = "Item Id to sell item", required = true) @PathVariable UUID itemId,
            @Parameter(description = "Quantity of the item to sell", required = true) @RequestParam long quantity) {
        storeService.sellItem(itemId, quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/item/{itemId}/sales")
    @Operation(summary = "View sell operations for a specific item",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of sold items"),
                    @ApiResponse(responseCode = "404", description = "The item you were trying to reach is not found")
            })
    public ResponseEntity<Page<SaleDto>> getSoldItems(
            @Parameter(description = "Item Id to get sell operations for", required = true) @PathVariable UUID itemId,
            Pageable pageable) {
        return new ResponseEntity<>(storeService.getSoldItems(itemId, pageable), HttpStatus.OK);
    }

    @GetMapping("/item/{itemId}/stock")
    @Operation(summary = "View current stock level for a specific item",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved stock quantity"),
                    @ApiResponse(responseCode = "404", description = "The item you were trying to reach is not found")
            })
    public ResponseEntity<Long> getStockQuantity(
            @Parameter(description = "Item Id to get stock quantity for", required = true) @PathVariable UUID itemId) {
        return new ResponseEntity<>(storeService.getStockQuantity(itemId), HttpStatus.OK);
    }

}
