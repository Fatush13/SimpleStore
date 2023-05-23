package com.tasks.store.mapper;

import com.tasks.store.model.Item;
import com.tasks.store.model.CreateItemDto;
import com.tasks.store.model.ItemDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    Item toItem(CreateItemDto dto);

}