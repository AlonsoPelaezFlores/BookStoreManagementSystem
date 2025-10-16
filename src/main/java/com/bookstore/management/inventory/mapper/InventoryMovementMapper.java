package com.bookstore.management.inventory.mapper;

import com.bookstore.management.inventory.dto.InventoryMovementResponseDTO;
import com.bookstore.management.inventory.model.InventoryMovement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {InventoryMapper.class})
public interface InventoryMovementMapper {

    InventoryMovementResponseDTO toInventoryMovementResponseDTO(InventoryMovement inventoryMovement);

}
