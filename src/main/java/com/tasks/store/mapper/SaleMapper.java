package com.tasks.store.mapper;

import com.tasks.store.model.Sale;
import com.tasks.store.model.SaleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    SaleDto toSaleDto(Sale sale);

}
