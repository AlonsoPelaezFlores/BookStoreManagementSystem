package com.bookstore.management.inventory.mapper;

import com.bookstore.management.book.mapper.BookMapper;
import com.bookstore.management.inventory.dto.CreateInventoryDTO;
import com.bookstore.management.inventory.dto.InventoryResponseDTO;
import com.bookstore.management.inventory.dto.InventorySummaryDTO;
import com.bookstore.management.inventory.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface InventoryMapper {


    Inventory toEntity(CreateInventoryDTO createInventoryDto);

    @Mapping(target = "realStockAvailable",
            expression = "java(inventory.getQuantityAvailable() - inventory.getQuantityReserved())")
    InventoryResponseDTO toInventoryResponseDTO(Inventory inventory);


    InventorySummaryDTO toInventorySummaryDTO(Inventory inventory);

    List<InventorySummaryDTO> toInventorySummaryDTOList(List<Inventory> inventories);
}
