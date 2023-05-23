package com.tasks.store.service;

import com.tasks.store.error.InsufficientStockException;
import com.tasks.store.error.ItemNotFoundException;
import com.tasks.store.mapper.ItemMapper;
import com.tasks.store.mapper.SaleMapper;
import com.tasks.store.model.CreateItemDto;
import com.tasks.store.model.Item;
import com.tasks.store.model.ItemDto;
import com.tasks.store.model.Sale;
import com.tasks.store.model.SaleDto;
import com.tasks.store.repository.ItemRepository;
import com.tasks.store.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class StoreService {

    private final ItemRepository itemRepository;
    private final SaleRepository saleRepository;
    private final ItemMapper itemMapper;
    private final SaleMapper saleMapper;

    @Transactional
    public ItemDto addItem(CreateItemDto createItemDto) {
        Item item = itemMapper.toItem(createItemDto);

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto updateItem(UUID itemId, CreateItemDto createItemDto) {
        return itemRepository.findById(itemId)
                .map(item -> {
                    item.setName(createItemDto.getName());
                    item.setPrice(createItemDto.getPrice());
                    item.setQuantity(createItemDto.getQuantity());
                    return itemMapper.toItemDto(itemRepository.save(item));
                })
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    @Transactional
    public void deleteItem(UUID itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(itemId);
        }
        itemRepository.markAsDeleted(itemId);
    }

    public ItemDto getItem(UUID itemId) {
        Item item = itemRepository.findById(itemId)
                .filter(product -> !product.isDeleted())
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        return itemMapper.toItemDto(item);
    }

    public Page<ItemDto> getAllItems(Pageable pageable) {
        List<ItemDto> items = itemRepository.findAll(pageable).stream()
                .filter(item -> !item.isDeleted())
                .map(itemMapper::toItemDto)
                .toList();

        return new PageImpl<>(items);
    }

    @Transactional
    public void sellItem(UUID itemId, long quantity) {
        Item item = itemRepository.findById(itemId)
                .filter(product -> !product.isDeleted())
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        if (item.getQuantity() < quantity) {
            throw new InsufficientStockException(itemId);
        }
        item.setQuantity(item.getQuantity() - quantity);
        Sale sale = new Sale();
        sale.setItem(item);
        sale.setQuantitySold(quantity);

        saleRepository.save(sale);
    }

    public Page<SaleDto> getSoldItems(UUID itemId, Pageable pageable) {
        return saleRepository.findByItemId(itemId, pageable).map(saleMapper::toSaleDto);
    }

    public Long getStockQuantity(UUID itemId) {
        return itemRepository.findById(itemId)
                .filter(item -> !item.isDeleted())
                .map(Item::getQuantity)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }

}
