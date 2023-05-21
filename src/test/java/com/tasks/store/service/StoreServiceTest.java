package com.tasks.store.service;

import com.tasks.store.error.InsufficientStockException;
import com.tasks.store.error.ItemNotFoundException;
import com.tasks.store.mapper.ItemMapper;
import com.tasks.store.model.Item;
import com.tasks.store.model.ItemDto;
import com.tasks.store.model.Sale;
import com.tasks.store.repository.ItemRepository;
import com.tasks.store.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ItemMapper itemMapper;

    private StoreService storeService;

    @BeforeEach
    public void setup() {
        storeService = new StoreService(itemRepository, saleRepository, itemMapper);
    }

    @Test
    void testAddItem() {
        ItemDto itemDto = new ItemDto();
        Item item = new Item();

        when(itemMapper.toItem(itemDto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = storeService.addItem(itemDto);

        assertThat(result).isEqualTo(itemDto);
    }

    @Nested
    class GetItem {

        @Test
        void getItem_whenItemExists() {
            UUID itemId = UUID.randomUUID();
            Item item = new Item();
            ItemDto itemDto = new ItemDto();

            when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
            when(itemMapper.toItemDto(item)).thenReturn(itemDto);

            ItemDto result = storeService.getItem(itemId);

            assertThat(result).isEqualTo(itemDto);
        }

        @Test
        void getItem_whenItemDoesNotExist() {
            UUID itemId = UUID.randomUUID();

            when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

            assertThatExceptionOfType(ItemNotFoundException.class)
                    .isThrownBy(() -> storeService.getItem(itemId));
        }

    }

    @Nested
    class UpdateItem {

        @Test
        void updateItem_whenItemExists() {
            UUID itemId = UUID.randomUUID();
            ItemDto itemDto = new ItemDto();
            Item item = new Item();

            when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
            when(itemRepository.save(item)).thenReturn(item);
            when(itemMapper.toItemDto(item)).thenReturn(itemDto);

            ItemDto result = storeService.updateItem(itemId, itemDto);

            assertThat(result).isEqualTo(itemDto);
        }

        @Test
        void updateItem_whenItemDoesNotExist() {
            UUID itemId = UUID.randomUUID();
            ItemDto itemDto = new ItemDto();

            when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

            assertThatExceptionOfType(ItemNotFoundException.class)
                    .isThrownBy(() -> storeService.updateItem(itemId, itemDto));
        }

    }

    @Nested
    class DeleteItem {

        @Test
        void deleteItem_whenItemExists() {
            UUID itemId = UUID.randomUUID();

            when(itemRepository.existsById(itemId)).thenReturn(true);

            storeService.deleteItem(itemId);

            verify(itemRepository, times(1)).deleteById(itemId);
        }

        @Test
        void deleteItem_whenItemDoesNotExist() {
            UUID itemId = UUID.randomUUID();

            when(itemRepository.existsById(itemId)).thenReturn(false);

            assertThatExceptionOfType(ItemNotFoundException.class).isThrownBy(() -> storeService.deleteItem(itemId));
        }

    }

    @Test
    void getAllItems() {
        Pageable pageable = PageRequest.of(0, 5);
        Item item = new Item();
        ItemDto itemDto = new ItemDto();
        Page<Item> itemPage = new PageImpl<>(Collections.singletonList(item), pageable, 1);

        when(itemRepository.findAll(pageable)).thenReturn(itemPage);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        Page<ItemDto> result = storeService.getAllItems(pageable);

        verify(itemRepository, times(1)).findAll(pageable);
        verify(itemMapper, times(1)).toItemDto(item);

        assertThat(result.getContent().get(0)).isEqualTo(itemDto);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Nested
    class SellItem {

        @Test
        void sellItem_whenItemExists_andSufficientStock() {
            UUID itemId = UUID.randomUUID();
            Item item = new Item();
            item.setQuantity(10L);

            when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

            storeService.sellItem(itemId, 5);

            assertThat(item.getQuantity()).isEqualTo(5L);
            verify(saleRepository, times(1)).save(any());
        }

        @Test
        void sellItem_whenItemExists_andInsufficientStock() {
            UUID itemId = UUID.randomUUID();
            Item item = new Item();
            item.setQuantity(5L);

            when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

            assertThatExceptionOfType(InsufficientStockException.class)
                    .isThrownBy(() -> storeService.sellItem(itemId, 10));
        }

        @Test
        void sellItem_whenItemDoesNotExist() {
            UUID itemId = UUID.randomUUID();

            when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

            assertThatExceptionOfType(ItemNotFoundException.class)
                    .isThrownBy(() -> storeService.sellItem(itemId, 5));
        }

    }

    @Test
    void getSoldItems() {
        UUID itemId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);
        Sale sale = new Sale();
        Page<Sale> salePage = new PageImpl<>(Collections.singletonList(sale), pageable, 1);

        when(saleRepository.findByItemId(itemId, pageable)).thenReturn(salePage);

        Page<Sale> result = storeService.getSoldItems(itemId, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(sale);
    }


    @Nested
    class GetStockQuantity {

        @Test
        void getStockQuantity_whenItemExists() {
            UUID itemId = UUID.randomUUID();
            Item item = new Item();
            item.setQuantity(10L);

            when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

            Long result = storeService.getStockQuantity(itemId);

            assertThat(result).isEqualTo(10L);
        }

        @Test
        void getStockQuantity_whenItemDoesNotExist() {
            UUID itemId = UUID.randomUUID();

            when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

            assertThatExceptionOfType(ItemNotFoundException.class)
                    .isThrownBy(() -> storeService.getStockQuantity(itemId));
        }

    }

}