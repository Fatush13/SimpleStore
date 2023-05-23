package com.tasks.store.service;

import com.tasks.store.error.InsufficientStockException;
import com.tasks.store.error.ItemNotFoundException;
import com.tasks.store.model.CreateItemDto;
import com.tasks.store.model.ItemDto;
import com.tasks.store.repository.ItemRepository;
import com.tasks.store.repository.SaleRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.UUID;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(refresh = AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"/sql/create_schema.sql", "/sql/add_item_table.sql", "/sql/add_sale_table.sql"})
class StoreServiceIntTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SaleRepository saleRepository;

    private CreateItemDto createItemDto;
    private ItemDto itemDto;
    private UUID itemId;

    @BeforeEach
    public void setUp() {
        createItemDto = new CreateItemDto();
        createItemDto.setName("ItemName");
        createItemDto.setPrice(new BigDecimal("100.00"));
        createItemDto.setQuantity(10L);

        itemDto = storeService.addItem(createItemDto);

        itemId = itemDto.getId();
    }

    @AfterEach
    public void tearDown() {
        saleRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void whenAddItem_thenItemShouldBeAdded() {
        assertThat(storeService.getItem(itemId)).isEqualTo(itemDto);
    }

    @Test
    void whenUpdateItem_thenItemShouldBeUpdated() {
        CreateItemDto updateDto = new CreateItemDto();
        updateDto.setName("UpdatedName");
        updateDto.setPrice(new BigDecimal(200));
        updateDto.setQuantity(20L);

        ItemDto updatedItem = storeService.updateItem(itemId, updateDto);

        assertThat(updatedItem).usingRecursiveComparison().ignoringFieldsOfTypes(UUID.class)
                .isEqualTo(updateDto);
    }

    @Test
    void whenDeleteItem_thenItemShouldBeDeleted() {
        storeService.deleteItem(itemId);

        assertThrows(ItemNotFoundException.class, () -> storeService.getItem(itemId));
    }

    @Test
    void whenGetAllItems_thenAllItemsShouldBeReturned() {
        var pageable = PageRequest.of(0, 10);
        var items = storeService.getAllItems(pageable);

        assertThat(items.getContent()).contains(itemDto);
    }

    @Test
    void whenSellItem_thenItemQuantityShouldDecrease() {
        long soldQuantity = 1L;

        storeService.sellItem(itemId, soldQuantity);

        assertThat(storeService.getItem(itemId).getQuantity()).isEqualTo(createItemDto.getQuantity() - soldQuantity);
    }

    @Test
    void whenSellItemWithInsufficientStock_thenExceptionShouldBeThrown() {
        long soldQuantity = createItemDto.getQuantity() + 1;

        assertThrows(InsufficientStockException.class, () -> storeService.sellItem(itemId, soldQuantity));
    }

    @Test
    void whenGetSoldItems_thenAllSoldItemsShouldBeReturned() {
        var pageable = PageRequest.of(0, 10);

        storeService.sellItem(itemId, 1L);
        var sales = storeService.getSoldItems(itemId, pageable);

        assertThat(sales.getContent()).hasSize(1);
    }

    @Test
    void whenGetStockQuantity_thenCorrectStockQuantityShouldBeReturned() {
        assertThat(storeService.getStockQuantity(itemId)).isEqualTo(createItemDto.getQuantity());
    }

    @Test
    void whenUpdateNonexistentItem_thenExceptionShouldBeThrown() {
        UUID nonExistentItemId = UUID.randomUUID();

        assertThrows(ItemNotFoundException.class, () -> storeService.updateItem(nonExistentItemId, createItemDto));
    }

}
