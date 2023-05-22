package com.tasks.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasks.store.model.ItemDto;
import com.tasks.store.model.Sale;
import com.tasks.store.service.StoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StoreController.class)
@Import(TestConfig.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testAddItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setPrice(new BigDecimal("10.0"));
        itemDto.setQuantity(10L);

        Mockito.when(storeService.addItem(Mockito.any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/api/v1/store/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateItem() throws Exception {
        UUID itemId = UUID.randomUUID();
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Test Item");
        itemDto.setPrice(new BigDecimal("20.0"));
        itemDto.setQuantity(5L);

        Mockito.when(storeService.updateItem(Mockito.eq(itemId), Mockito.any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(put("/api/v1/store/item/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteItem() throws Exception {
        UUID itemId = UUID.randomUUID();
        Mockito.doNothing().when(storeService).deleteItem(itemId);

        mockMvc.perform(delete("/api/v1/store/item/{itemId}", itemId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetItem() throws Exception {
        UUID itemId = UUID.randomUUID();
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setPrice(new BigDecimal("10.0"));
        itemDto.setQuantity(10L);

        Mockito.when(storeService.getItem(itemId)).thenReturn(itemDto);

        mockMvc.perform(get("/api/v1/store/item/{itemId}", itemId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllItems() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setPrice(new BigDecimal("10.0"));
        itemDto.setQuantity(10L);
        Page<ItemDto> page = new PageImpl<>(Collections.singletonList(itemDto));

        Mockito.when(storeService.getAllItems(Mockito.any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/store/items").param("page", "0").param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void testSellItem() throws Exception {
        UUID itemId = UUID.randomUUID();
        Mockito.doNothing().when(storeService).sellItem(itemId, 1L);

        mockMvc.perform(post("/api/v1/store/item/{itemId}/sale", itemId).param("quantity", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSoldItems() throws Exception {
        UUID itemId = UUID.randomUUID();
        Sale sale = new Sale();
        sale.setQuantitySold(1L);
        Page<Sale> page = new PageImpl<>(Collections.singletonList(sale));

        Mockito.when(storeService.getSoldItems(Mockito.eq(itemId), Mockito.any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/store/item/{itemId}/sales", itemId).param("page", "0").param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetStockQuantity() throws Exception {
        UUID itemId = UUID.randomUUID();
        Mockito.when(storeService.getStockQuantity(itemId)).thenReturn(10L);

        mockMvc.perform(get("/api/v1/store/item/{itemId}/stock", itemId))
                .andExpect(status().isOk());
    }
}

@TestConfiguration
class TestConfig {

    private static final String TIME_ZONE = "Europe/Tallinn";

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of(TIME_ZONE));
    }

}
