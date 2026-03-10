package com.bookstore.management.inventory.mapper;

import com.bookstore.management.inventory.dto.InventoryMovementResponseDTO;
import com.bookstore.management.inventory.model.InventoryMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {InventoryMapper.class})
public interface InventoryMovementMapper {

    @Mapping(target = "inventorySummaryDTO", source = "inventory")
    InventoryMovementResponseDTO toInventoryMovementResponseDTO(InventoryMovement inventoryMovement);

}
